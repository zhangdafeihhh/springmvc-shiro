package com.zhuanche.serv.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constants.BusConst;
import com.zhuanche.constants.busManage.EnumOrder;
import com.zhuanche.constants.busManage.EnumOrderType;
import com.zhuanche.constants.busManage.EnumPayType;
import com.zhuanche.constants.busManage.EnumServiceType;
import com.zhuanche.dto.busManage.*;
import com.zhuanche.entity.busManage.BusCostDetail;
import com.zhuanche.entity.busManage.BusOrderPayExport;
import com.zhuanche.entity.busManage.OrgCostInfo;
import com.zhuanche.entity.mdbcarmanage.BusOrderOperationTime;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.mongo.BusDriverMongoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.*;
import com.zhuanche.vo.busManage.*;
import mapper.mdbcarmanage.ex.BusOrderOperationTimeExMapper;
import mapper.rentcar.CarBizServiceMapper;
import mapper.rentcar.ex.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("busAssignmentService")
public class BusAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(BusAssignmentService.class);
    /**
     * 校验是否为数字组成
     */
    private static final Pattern pattern_compile = Pattern.compile("^[0-9]*$");

    @Autowired
    private CarBizServiceMapper carBizServiceMapper;

    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;

    @Autowired
    private BusCarBizDriverInfoExMapper busCarBizDriverInfoExMapper;
    @Autowired
    private CarBizCustomerExMapper customerExMapper;
    @Autowired
    private BusCarBizCustomerAppraisalExMapper appraisalExMapper;
    @Autowired
    private BusOrderOperationTimeExMapper operationTimeExMapper;
    @Autowired
    private BusCarBizCarGroupExMapper busGroupExMapper;

    @Autowired
    private BusCarBizCustomerAppraisalService busCarBizCustomerAppraisalService;

    @Autowired
    private BusCarBizCustomerAppraisalStatisticsService busCarBizCustomerAppraisalStatisticsService;

    @Autowired
    private BusDriverMongoService busDriverMongoService;
    @Autowired
    private BusCarViolatorsService busCarViolatorsService;
    @Autowired
    private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

    @Autowired
    @Qualifier("busAssignmentTemplate")
    private MyRestTemplate busAssignmentTemplate;

    @Autowired
    @Qualifier("carRestTemplate")
    private MyRestTemplate carRestTemplate;

    @Value("${bus.order.cost.url}")
    private String chargeBaseUrl;

    @Value("${business.url}")
    private String businessRestBaseUrl;

    @Value("${order.pay.old.url}")
    private String paymentBaseUrl;
    @Value("${order.pay.url}")
    private String orderPayUrl;
    /**
     * 订单组域名
     */
    @Value("${car.rest.url}")
    private String ORDER_API_URL;
    /**
     * 查询订单列表url
     */
    private String ORDER_INFO_URL="/busOrder/selectOrderInfo";

    private static String COMPANY_SUPPLIER_URL="/api/v1/inside/company/supplier/findCompanyAll";



    /**
     * @param params
     * @return BaseEntity
     * @throws
     * @Title: selectList
     * @Description: 查询巴士订单列表
     */
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public PageDTO selectList(BusOrderDTO params) {
        try {

            JSONObject result = queryOrderData(params);
            if (result == null) {
                return new PageDTO();
            }
            // 返回数据
            JSONObject data = result.getJSONObject("data");
            JSONArray dataList = data.getJSONArray("dataList");
            int total = data.getIntValue("totalCount");
            List<BusOrderVO> orderList = JSON.parseArray(dataList.toString(), BusOrderVO.class);
            if (orderList != null && orderList.size() > 0) {
            	// 计费信息
            	String orderNos = orderList.stream().map(order -> order.getOrderNo()).collect(Collectors.joining(","));
            	JSONArray busCostDetailList = getBusCostDetailList(orderNos);
            	// 企业信息
            	String companyIds = orderList.stream().map(BusOrderVO::getBusinessId).filter(Objects::nonNull).distinct().map(String::valueOf).collect(Collectors.joining(","));
            	JSONArray queryBusinessInfoBatch = queryBusinessInfoBatch(companyIds);
            	
                orderList.forEach(order -> {
                    // a)司机姓名/司机手机号
                    if (order.getDriverId() != null) {
                        BusDriverDetailInfoVO driver = busCarBizDriverInfoExMapper.queryDriverExtendInfoById(order.getDriverId());
                        if (driver != null) {
                            order.setDriverName(driver.getName());
                            order.setDriverPhone(driver.getPhone());
                            order.setSupplierName(driver.getSupplierName());
                        }
                    }
                    // b)本单司机评分
                    if (StringUtils.isNotBlank(order.getOrderNo())) {
                        CarBizCustomerAppraisal appraisal = busCarBizCustomerAppraisalService.queryAppraisal(order.getOrderNo());
                        if (appraisal != null) {
                            order.setDriverScore(appraisal.getEvaluateScore());
                        }
                    }
                    // c)预约车别类型
                    if (StringUtils.isNotBlank(order.getBookingGroupid())) {
                        order.setBookingGroupName(carBizCarGroupExMapper.getGroupNameByGroupId(Integer.valueOf(order.getBookingGroupid())));
                    }

                    //d)实际指派车型类别名称
                    Integer carGroupId = order.getCarGroupId();
                    if(carGroupId!=null){
                        String carGroupName = carBizCarGroupExMapper.getGroupNameByGroupId(carGroupId);
                        order.setCarGroupName(carGroupName);
                    }
                    // e)订单类型名称
                    if (order.getServiceTypeId() != null) {
                        CarBizService service = carBizServiceMapper.selectByPrimaryKey(order.getServiceTypeId());
                        if (service != null) {
                            order.setServiceTypeName(service.getServiceName());
                        }
                    }
                    // f)预估里程
                    if (busCostDetailList != null) {
                    	String orderNo = order.getOrderNo();
                    	if (StringUtils.isNotBlank(orderNo)) {
                    		busCostDetailList.stream().filter(cost -> {
                    			JSONObject jsonObject = (JSONObject) JSON.toJSON(cost);
                    			return orderNo.equals(jsonObject.getString("orderNo"));
                    		}).findFirst().ifPresent(cost -> {
                    			JSONObject jsonObject = (JSONObject) JSON.toJSON(cost);
                    			order.setEstimateDistance(jsonObject.getDouble("estimateDistance"));
                    		});
                    	}
                    }
                    // g)企业名称/企业折扣/付款类型
                    if (queryBusinessInfoBatch != null) {
                    	Integer businessId = order.getBusinessId();
                    	if (businessId != null) {
                    		queryBusinessInfoBatch.stream().filter(business -> {
                    			JSONObject jsonObject = (JSONObject) JSON.toJSON(business);
                    			return businessId.toString().equals(jsonObject.getString("businessId"));
                    		}).findFirst().ifPresent(business -> {
                    			JSONObject jsonObject = (JSONObject) JSON.toJSON(business);
                    			order.setCompanyId(businessId);
                    			order.setCompanyName(jsonObject.getString("businessName"));
                    			order.setCompanyType(jsonObject.getInteger("type"));
                    			order.setPercent(jsonObject.getDouble("percent"));
                    		});
                    	}
                    }
                });
            }
            // 返回结果
            return new PageDTO(params.getPageNum(), params.getPageSize(), total, orderList);
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-selectList ] 查询巴士订单列表出错 {}", e);
        }
        return new PageDTO();
    }

    /**
     * @param params 根据页面选择条件导出订单信息
     * @param permission 是不是巴士运营权限，只有巴士运营才能导出预定人的企业相关信息
     * @return
     */
    public PageDTO buidExportData(BusOrderDTO params,boolean permission) {
        JSONObject result = queryOrderData(params);
        if (result == null) {
            return new PageDTO();
        }
        // 返回数据
        JSONObject data = result.getJSONObject("data");
        JSONArray dataList = data.getJSONArray("dataList");
        int total = data.getIntValue("totalCount");
        List<BusOrderExVO> orderList = JSON.parseArray(dataList.toString(), BusOrderExVO.class);
        if (orderList == null || orderList.isEmpty()) {
            return new PageDTO();
        }
        List<BusOrderExportVO> export = this.addOtherResult4Export(orderList,permission);
        PageDTO page = new PageDTO(params.getPageNum(), params.getPageSize(), total, export);
        return page;
    }

    /**
     * 传入最多30个订单号，返回符合导出条件的所有字段
     * @param orderNos 订单号，用英文逗号分隔，最多30个
     * @param permission 是不是巴士运营权限，只有巴士运营才能导出预定人的企业相关信息
     * @return
     */
    public List<BusOrderExportVO> buidExportData(String orderNos,boolean permission){
        List<BusOrderExVO> orderExVOS = queryOrderDetailByOrderNos(orderNos);
        if(orderExVOS==null||orderExVOS.isEmpty()){
            return null;
        }
        List<BusOrderExportVO> busOrderExportVOS = this.addOtherResult4Export(orderExVOS, permission);
        return busOrderExportVOS;
    }

    /**
     *
     * @param orderNos 逗号分隔的订单号，用于批量查询订单信息
     * @return
     */
    public List<BusOrderExVO> queryOrderDetailByOrderNos(String orderNos){
        Map<String,Object>orderParam=new TreeMap<>();
        orderParam.put("orderNos",orderNos);
        // 签名
        orderParam.put("businessId", Common.BUSINESSID);
        String sign = SignUtils.createMD5Sign(orderParam, Common.KEY);
        orderParam.put("sign", sign);
        JSONObject orderResult = MpOkHttpUtil.okHttpPostBackJson(ORDER_API_URL+ORDER_INFO_URL, orderParam, 2000, "批量查询订单信息");
        if(orderResult!=null&&orderResult.getInteger("code")!=null&&orderResult.getInteger("code")==0) {
            JSONArray orderArray = orderResult.getJSONObject("data").getJSONArray("dataList");
            if (orderArray == null || orderArray.isEmpty()) {
                return null;
            }
         List<BusOrderExVO> orderExVOS = orderArray.stream().map(o -> (JSONObject) o).map(o -> JSONObject.toJavaObject(o, BusOrderExVO.class)).collect(Collectors.toList());
            return orderExVOS;
        }
        return null;
    }

    /**
     * 将bean中的所有属性，按照顺序组成字符串，以便导出csv格式的文件
     * @param t
     * @return
     */
    public String fieldValueToString(Object t) {
        StringBuffer sb = new StringBuffer();
        String tab="\t";
        String split=",";
        //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
        Field[] fields = t.getClass().getDeclaredFields();
        try {
            for (short i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String fieldName = field.getName();
                String fieldType = field.getGenericType().toString();
                String getMethodName = "get"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                Class tCls = t.getClass();
                Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                Object value = getMethod.invoke(t, new Object[]{});
                //判断值的类型后进行强制类型转换
                String textValue = null;
                if (value == null || "null".equalsIgnoreCase(value.toString().trim())) {
                	textValue = StringUtils.EMPTY;
                } else if (value instanceof Date) {
                    Date value1 = (Date) value;
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    textValue = sf.format(value1);
                    sb.append(tab);
                } else if (value instanceof BigDecimal || value instanceof Double) {
                	textValue = BusConst.decimalFormat.format(value);
				} else {
                    //其它数据类型都当作字符串简单处理
                	textValue = value.toString().trim();
                    //如果是数字组成的字符串，前面加上制表符防止其变成科学计数法
                    if("class java.lang.String".equals(fieldType)&&pattern_compile.matcher((String)value).matches()){
                        sb.append(tab);
                    }
                }
                sb.append(textValue).append(split);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return sb.length()==0?StringUtils.EMPTY:sb.substring(0,sb.length()-1);
    }


    /**
     *根据权限和页面参数调用订单查询列表接口，有分页
     */
    private JSONObject queryOrderData(BusOrderDTO params) {
        // 数据权限控制SSOLoginUser
        String cityIds = null;
        String supplierIds = null;
        Integer cityId = params.getCityId();
        Integer supplierId = params.getSupplierId();
        Set<Integer> authOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
        Set<Integer> authOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID
        if (Integer.valueOf(10103).equals(params.getStatus())) {
            // 指派列表不限制供应商
            //params.setSupplierId(null);
            //supplierIds = null;
            //指派列表根据供应商过滤订单
            SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
            boolean accountIsAdmin = WebSessionUtil.isSupperAdmin();
            String citiesStr = authOfCity.stream().map( c -> String.valueOf(c) ).collect(Collectors.joining(","));
            String suppliersStr = authOfSupplier.stream().map( c -> String.valueOf(c) ).collect(Collectors.joining(","));
            if (!accountIsAdmin && StringUtils.isNotBlank(citiesStr)) {
                Integer serviceCityId = params.getCityId();
                if (serviceCityId != null) {
                    String[] cities = citiesStr.split(",");
                    boolean cityFlag = false;
                    for (String c : cities) {
                        if (String.valueOf(cityId).equals(c)) {
                            cityFlag = true;
                            break;
                        }
                    }
                    if (!cityFlag) {
                        logger.warn(user.getName()+ " 没有查询该城市的权限，cityId=：" + cityId);
                        return null;
                    }
                }
                // 如果城市权限不为空，但是供应商权限为空，查询城市权限下所有的供应商
                if (StringUtils.isBlank(suppliersStr)) {
                    // 查询该城市下的所有的供应商
                    Map<String,Set<Integer>> cityIdsMap = Maps.newHashMap();
                    cityIdsMap.put("cityIds",authOfCity);
                    List<Integer> supplierIdByCitys = busCarBizSupplierExMapper.querySupplierIdByCitys(cityIdsMap);
                    suppliersStr = supplierIdByCitys.stream().map( c -> String.valueOf(c) ).collect(Collectors.joining(","));
                }
                // 将该用户权限下的所有供应商，传入企业接口查询所有跟该企业绑定的供应商，以及 所有存在绑定关系的企业
                Map<String,Object> supplierParam = Maps.newHashMap();
                supplierParam.put("supplierIds",suppliersStr);
                String url = businessRestBaseUrl + COMPANY_SUPPLIER_URL ;
                JSONObject company = MpOkHttpUtil.okHttpGetBackJson(url, supplierParam, 3000, "指派列表调用企业接口");
                logger.info("指派列表调用企业接口返回结果=" + JSON.toJSONString(company));
                if (company == null || company.getInteger("code") == null || company.getInteger("code") != 0) {
                    return null;
                }
                JSONObject data = company.getJSONObject("data");
                JSONArray allList = data.getJSONArray("allList");
                JSONArray supplierOfList = data.getJSONArray("supplierOfList");// 权限供应商下绑定的企业ID
                JSONArray remoteBindList = data.getJSONArray("remoteBindList");

                params.setBusinessIds(StringUtils.defaultIfBlank(StringUtils.join(allList, ","), StringUtils.EMPTY));
                params.setSpecialBusinessIds(StringUtils.defaultIfBlank(StringUtils.join(supplierOfList, ","), StringUtils.EMPTY));
                params.setCrossDomainBusinessIds(StringUtils.defaultIfBlank(StringUtils.join(remoteBindList, ","), StringUtils.EMPTY));
            }
        } else if (supplierId == null && authOfSupplier != null) {
            if (cityId == null && authOfCity != null) {
                int size = authOfCity.size();
                if (size == 1) {
                    authOfCity.stream().findFirst().ifPresent(params::setCityId);
                } else {
                    cityIds = StringUtils.join(authOfCity, ",");
                }
            }
            int size = authOfSupplier.size();
            if (size == 1) {
                authOfSupplier.stream().findFirst().ifPresent(params::setSupplierId);
            } else {
                supplierIds = StringUtils.join(authOfSupplier, ",");
            }
        }

        // 请求参数
        String jsonString = JSON.toJSONStringWithDateFormat(params, JSON.DEFFAULT_DATE_FORMAT);
        JSONObject json = (JSONObject) JSONObject.parse(jsonString);
        Map<String, Object> paramMap = json.getInnerMap();

        // 补充司机信息
        Set<Integer> driverIds = new HashSet<>();
        if (StringUtils.isNotBlank(params.getDriverName())) {
            List<DriverMongo> driversByName = busDriverMongoService.queryDriverByName(params.getDriverName());
            if (driversByName != null) {
                driverIds.addAll(driversByName.stream().map(driver -> driver.getDriverId()).collect(Collectors.toList()));
            }
        }
        if (StringUtils.isNotBlank(params.getDriverPhone())) {
            List<DriverMongo> driversByPhone = busDriverMongoService.queryDriverByPhone(params.getDriverPhone());
            if (driversByPhone != null) {
                driverIds.addAll(driversByPhone.stream().map(driver -> driver.getDriverId()).collect(Collectors.toList()));
            }
        }
        if (driverIds.size() == 1) {
            driverIds.stream().findFirst().ifPresent(driverId -> paramMap.put("driverId", driverId));
        } else {
            String driverIdsStr = StringUtils.join(driverIds, ",");
            paramMap.put("driverIds", StringUtils.isBlank(driverIdsStr) ? null : driverIdsStr);
        }
        // 补充权限控制
        paramMap.put("cityIds", StringUtils.isBlank(cityIds) ? null : cityIds);
        paramMap.put("supplierIds", StringUtils.isBlank(supplierIds) ? null : supplierIds);

        // 签名
        paramMap.put("businessId", Common.BUSINESSID);
        String sign = SignUtils.createMD5Sign(paramMap, Common.KEY);
        logger.info("[ BusAssignmentService-selectList ] 请求参数签名   sign={}", sign);
        paramMap.put("sign", sign);

        logger.info("[ BusAssignmentService-selectList ] 请求参数   paramMap={}", paramMap);

        // 请求接口
        JSONObject result = MpOkHttpUtil.okHttpPostBackJson(ORDER_API_URL + BusConst.Order.SELECT_ORDER_LIST, paramMap, 3000, "查询订单列表");
        //String response = carRestTemplate.postForObject(BusConst.Order.SELECT_ORDER_LIST, JSONObject.class, paramMap);
        //JSONObject result = JSON.parseObject(response);
        int code = result.getIntValue("code");
        String msg = result.getString("msg");

        // 接口错误
        if (code == 1) {
            logger.info("[ BusAssignmentService-selectList ] 查询巴士订单列表出错, 错误码:{}, 错误原因:{}", code, msg);
            return null;
        }
        return result;
    }
    /**
     * 导出订单详情时，封装其他信息 如司机姓名，供应商，计费信息，企业信息，评分信息
     */
    private List<BusOrderExportVO> addOtherResult4Export(List<BusOrderExVO> orderList,boolean permission){
        List<String> orderIdList = new ArrayList<>();
        List<String> orderNoList = new ArrayList<>();
        Set<Integer> businessIds=new HashSet<>();
        Set<Integer> userIds= new HashSet<>();
        Set<Integer> driverIds=new HashSet<>();
        Set<Integer> groupIds=new HashSet();
        orderList.forEach(order -> {
            orderIdList.add(String.valueOf(order.getOrderId()));
            orderNoList.add(order.getOrderNo());
            Integer businessId = order.getBusinessId();
            //type==2代表是企业订单
            if(order.getType()==2&&businessId!=null){
                businessIds.add(businessId);
            }
            //收集司机ID
            Integer driverId = order.getDriverId();
            if(driverId!=null){
                driverIds.add(driverId);
            }
            // 收集预定人ID
            Integer bookingUserId = order.getBookingUserId();
            if (bookingUserId != null) {
                userIds.add(bookingUserId);
            }
            //预约车型ID
            String bookingGroupid = order.getBookingGroupid();
            if(bookingGroupid!=null){groupIds.add(bookingUserId);}
            //实际指定车型ID
            Integer carGroupId = order.getCarGroupId();
            if(carGroupId!=null){groupIds.add(carGroupId);}
        });
        //批量查询司机信息和供应商名称
        Map<Integer,Map<String,Object>> driverInfoMap=new HashMap<>();
        if(driverIds.size()>0){
            List<Map<String, Object>> driverInfos = busCarBizDriverInfoExMapper.queryDriverSimpleBatch(driverIds);
            driverInfos.forEach(o->{
                driverInfoMap.put(Integer.parseInt(o.get("driverId").toString()),o);
            });
        }
        //批量查询预定人名称
        Map<Integer,String> userNames = new HashMap<>();
        if(userIds.size()>0){
            List<CarBizCustomer> carBizCustomers = customerExMapper.selectBatchCusName(userIds);
            carBizCustomers.forEach(o->{userNames.put(o.getCustomerId(),o.getName());});
        }
        //批量查询车型类别
        Map<Integer,CarBizCarGroup>groupMap=new HashMap();
        List<CarBizCarGroup> groups = busGroupExMapper.queryGroupByIds(groupIds);
        groups.forEach(o->groupMap.put(o.getGroupId(),o));
        //批量调用计费接口
        String orderNos = StringUtils.join(orderNoList, ",");
        Map<String, BusCostDetail> orderCostMap = new HashMap<>(16);
        JSONArray busCostList = this.getBusCostDetailList(orderNos);
        if (busCostList != null && !busCostList.isEmpty()) {
            busCostList.stream().map(o -> JSONObject.toJavaObject((JSONObject) o, BusCostDetail.class))
                    .forEach(cost -> {
                        orderCostMap.put(cost.getOrderNo(), cost);
                    });
        }
        //调用支付接口，查询支付信息
        Map<String, BusOrderPayExport> orderPayMap = new HashMap<>();
        List<BusOrderPayExport> orderPayList = this.getPayDetailList(orderNos);
        if (orderPayList != null && !orderPayList.isEmpty()) {
            orderPayList.forEach(pay -> {
                orderPayMap.put(pay.getOrderNo(), pay);
            });
        }
        //查询指派改派时间
        //指派时间
        Map<String, String> assignMap = new HashMap<>();
        //改派时间
        Map<String, String> reassigMap = new HashMap<>();
        List<BusOrderOperationTime> operTimes = operationTimeExMapper.selectOperByNos(orderNoList);
        operTimes.stream().sorted(Comparator.comparing(BusOrderOperationTime::getId).reversed()).forEach(o -> {
            //1 指派 2 改派
            if (o.getType() == 1) {
                assignMap.put(o.getOrderNo(), DateUtils.formatDate(o.getTime()));
            } else {
                reassigMap.put(o.getOrderNo(), DateUtils.formatDate(o.getTime()));
            }
        });
        //查询每一个订单的评分
        Map<String, String> appraisalMap = new HashMap<>();
        List<CarBizCustomerAppraisal> appraisals = appraisalExMapper.queryBatchAppraisal(orderNoList);
        appraisals.forEach(o -> {
            appraisalMap.put(o.getOrderNo(), o.getEvaluateScore());
        });
        // 企业信息
        Map<Integer, OrgCostInfo> orgResult=new HashMap<>(16);
        //有导出企业信息的权限才导出企业信息
        if(permission&&businessIds.size()>0){
            String businessIdStr = businessIds.stream().map(o -> String.valueOf(o)).collect(Collectors.joining(","));
            JSONArray orgCosts = queryBusinessInfoBatch(businessIdStr);
            orgCosts.stream().map(orgCost -> JSONObject.toJavaObject((JSONObject) orgCost, OrgCostInfo.class)).forEach(o -> {
                orgResult.put(o.getBusinessId(), o);
            });
        }
        //=====================拼装信息==================================================================
        List<BusOrderExportVO> export = new ArrayList<>();
        //拼装参数
        for (BusOrderExVO order : orderList) {
            String orderNo = order.getOrderNo();
            //计费信息
            BusCostDetail cost = orderCostMap.get(orderNo);
            //支付信息
            BusOrderPayExport pay = orderPayMap.get(orderNo);
            //指派时间
            String assigTime = reassigMap.get(orderNo);
            //改派时间
            String reassingTime = reassigMap.get(orderNo);
            //企业信息
            Integer businessId = order.getBusinessId();
            OrgCostInfo orgInfo = orgResult.get(businessId);

            BusOrderExportVO orderExport = new BusOrderExportVO();
            orderExport.setOrderId(order.getOrderId());
            orderExport.setOrderNo(order.getOrderNo());
            orderExport.setCreateDate(order.getCreateDate());
            orderExport.setBookingDate(order.getBookingDate());
            orderExport.setBookingStartAddr(order.getBookingStartAddr() == null ? StringUtils.EMPTY : order.getBookingStartAddr());
            orderExport.setBookingEndAddr(order.getBookingEndAddr() == null ? StringUtils.EMPTY : order.getBookingEndAddr());
            orderExport.setCityName(order.getCityName());
            //服务类型
            Integer serviceTypeId = order.getServiceTypeId();
            if (serviceTypeId != null && serviceTypeId > 0) {
                orderExport.setServiceName(EnumServiceType.getI18nByValue(serviceTypeId));
            }
            if (order.getBookingGroupid() != null) {
                int groupId = 0;
                try {
                    groupId = Integer.parseInt(order.getBookingGroupid());
                } catch (NumberFormatException e) {
                    logger.error("巴士导出订单解析groupId 异常");
                }
                if (groupId != 0) {
                    CarBizCarGroup group = groupMap.get(groupId);
                    if(group!=null){orderExport.setBookingGroupName(group.getGroupName());}
                }
            }
            orderExport.setFactGroupName(order.getCarGroupName());
            Integer orderType = order.getOrderType();
            if (orderType != null) {
                orderExport.setOrderType(EnumOrderType.getDis(orderType));
            }
            orderExport.setRiderCount(order.getRiderCount());
            orderExport.setLuggageCount(order.getLuggageCount());
            orderExport.setBookingUserPhone(order.getBookingUserPhone());
            orderExport.setRiderName(order.getRiderName());
            orderExport.setRiderPhone(order.getRiderPhone());
            orderExport.setLicensePlates(order.getLicensePlates());
            orderExport.setFactEndDate(order.getFactEndDate());
            orderExport.setFactDate(order.getFactDate());
            orderExport.setFactStartAddr(order.getFactStartAddr());
            orderExport.setFactEndAddr(order.getFactEndAddr());
            Integer isReturn = order.getIsReturn();
            if (isReturn != null) {
                orderExport.setIsReturn(isReturn == 1 ? "往返" : "单程");
            } else {
                orderExport.setIsReturn("单程");
            }
            Integer status = order.getStatus();
            if (status != null) {
                orderExport.setStatus(EnumOrder.getDis(status));
            }
            orderExport.setMemo(order.getMemo());
            Integer type = order.getType();
            if (type != null) {
                orderExport.setType(type == 1 ? "普通用户订单" : "企业用户订单");
            }
            orderExport.setSupplierName(order.getSupplierName());
            orderExport.setBookingUserName(order.getBookingUserName());
            orderExport.setEstimatedAmountYuan(order.getEstimatedAmountYuan());
            orderExport.setDriverName(order.getDriverName());
            orderExport.setDriverPhone(order.getDriverPhone());
            if (cost != null) {
                orderExport.setAmount(cost.getAmount() != null ? cost.getAmount() : BigDecimal.ZERO);
                if (cost.getSettleDate() != null) {
                    orderExport.setSettleDate(cost.getSettleDate());
                }
                BigDecimal damageFee = cost.getDamageFee();
                orderExport.setDamageFee(damageFee != null ? cost.getDamageFee() : BigDecimal.ZERO);
                orderExport.setCouponAmount(cost.getCouponAmount() != null ?cost.getCouponAmount() : BigDecimal.ZERO);
                orderExport.setDistance(cost.getDistance() != null ? cost.getDistance() : BigDecimal.ZERO);
                orderExport.setDuration(cost.getDuration() != null ? cost.getDuration() : 0);
                Integer payType = cost.getPayType();
                if (payType != null) {
                    orderExport.setPayType(EnumPayType.getDis(payType));
                }
                orderExport.setTcFee(cost.getTcFee());
                orderExport.setGsFee(cost.getGsFee());
                orderExport.setHotelFee(cost.getHotelFee());
                orderExport.setMealFee(cost.getMealFee());
                orderExport.setQtFee(cost.getQtFee());
                orderExport.setSettleOriginalAmount(cost.getSettleOriginalAmount());
            }
            if (pay != null) {
                orderExport.setPayToolName(pay.getPayToolName());
                if(pay.getFinishDate()!=null){
                    orderExport.setFinishDate(DateUtils.getDate(pay.getFinishDate()));
                }
            }
            if (assigTime != null) {
                orderExport.setAssigTime(assigTime);
            }
            if (reassingTime != null) {
                orderExport.setReassingTime(reassingTime);
            }
            if (orgInfo != null) {
                orderExport.setBusinessName(StringUtils.trimToEmpty(orgInfo.getBusinessName()));
                //空默认是 100%
                orderExport.setPercent(orgInfo.getPercent() == null ? "1" : String.valueOf(orgInfo.getPercent()));
                if (orgInfo.getType() != null) {
                    orderExport.setOrgCostType(orgInfo.getType() == 1 ? "预付费" : "后附费");
                }
            }
            //司机信息
            Integer driverId = order.getDriverId();
            Map<String, Object> driverInfo = driverInfoMap.get(driverId);
            if(driverInfo!=null){
                orderExport.setDriverName(driverInfo.get("name")==null?StringUtils.EMPTY:driverInfo.get("name").toString());
                orderExport.setDriverPhone(driverInfo.get("phone")==null?StringUtils.EMPTY:driverInfo.get("phone").toString());
                orderExport.setSupplierName(driverInfo.get("supplierName")==null?StringUtils.EMPTY:driverInfo.get("supplierName").toString());
            }
            //预定人名称
            String username = userNames.get(order.getBookingUserId());
            orderExport.setBookingUserName(username==null?StringUtils.EMPTY:username);
            //订单评分
            orderExport.setEvaluateScore(StringUtils.trimToEmpty(appraisalMap.get(orderNo)));
            export.add(orderExport);
        }
        return export;
    }
    private List<BusOrderPayExport> getPayDetailList(String orderNos) {
        logger.info("导出巴士订单列表，开始调用支付接口");
        Map<String, Object> param = new HashMap<>();
        param.put("tradeOrderNos", orderNos);
        try {
            String url = orderPayUrl + BusConst.Payment.PAY_LIST;
            JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, param, 3000, "大巴车-批量获取支付明细");
            if (result.getIntValue("code") != 0) {
                logger.info("导出巴士订单列表,调用支付接口出错 code:" + result.getIntValue("code") + ",错误原因:" + result.getString("msg"));
                return null;
            }
            String data = result.getString("data");
            List<BusOrderPayExport> busOrderVOS = JSON.parseArray(data, BusOrderPayExport.class);
            return busOrderVOS;
        } catch (Exception e) {
            logger.error("导出巴士订单列表,调用支付异常", e);
            return null;
        }
    }

    /**
     * @Title: getBusCostDetailList
     * @Description: 批量获取费用明细
     * @param orderNos
     * @return
     * @return JSONArray
     * @throws
     */
    public JSONArray getBusCostDetailList(String orderNos) {
        Map<String,Object> params = new HashMap<>();
        params.put("orderNos", orderNos);
        logger.info("[ BusAssignmentService-getBusCostDetailList ] 大巴车-批量获取费用明细,请求参数,params={}", params);
        try {
            String url = chargeBaseUrl + BusConst.Charge.BUSS_GETBUSCOSTDETAILLIST;
            JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 3000, "大巴车-批量获取费用明细");
            if (result.getIntValue("code") != 0) {
                logger.info("[ BusAssignmentService-getBusCostDetailList ] 大巴车-批量获取费用明细,调用接口出错 code:{},错误原因:{}",
                        result.getIntValue("code"), result.getString("msg"));
                return null;
            }
            JSONArray jsonArray = result.getJSONArray("data");
            return jsonArray;
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-getBusCostDetailList ] 大巴车-批量获取费用明细,调用接口异常errorMsg={}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * @Title: queryBatchOrgInfo
     * @Description: 根据手机号查询企业信息
     * @param phones
     * @return
     * @return JSONArray
     * @throws
     */
    public JSONArray queryCompanyByPhone(String phones) {
        try {
            Map<String,Object> params = new HashMap<>();
            params.put("phone", phones);
            logger.info("[ BusAssignmentService-queryCompanyByPhone ] 根据手机号查询企业信息,请求参数,params={}", params);

            String url = businessRestBaseUrl + BusConst.BusinessRest.COMPANY_QUERYCOMPANYBYPHONE;
            JSONObject result = MpOkHttpUtil.okHttpGetBackJson(url, params, 3000, "根据手机号查询企业信息");
            int code = result.getIntValue("code");
            String msg = result.getString("msg");
            if (code != 0) {
                logger.error("[ BusAssignmentService-queryCompanyByPhone ] 根据手机号查询企业信息,调用接口错误,code:{},错误原因:{}", code, msg);
                return null;
            }
            JSONArray jsonArray = result.getJSONArray("data");
            return jsonArray;
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-queryCompanyByPhone ] 根据手机号查询企业信息,订单接口异常,{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * @Title: queryBusinessInfoBatch
     * @Description: 批量查询企业信息
     * @param companyIds
     * @return
     * @return JSONArray
     * @throws
     */
    public JSONArray queryBusinessInfoBatch(String companyIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("businessIdStr", companyIds);
        logger.info("[ BusAssignmentService-queryBusinessInfoBatch ] 批量查询企业信息,请求参数,params={}", params);
        try {
            String url = paymentBaseUrl + BusConst.Payment.BUSINESS_QUERYBUSINESSINFOBATCH;
            JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 3000, "批量查询企业信息");
            int code = result.getIntValue("code");
            String msg = result.getString("msg");
            if (code != 0) {
                logger.error("[ BusAssignmentService-queryBusinessInfoBatch ] 批量查询企业信息,错误码:{},错误原因:{}", code, msg);
                return null;
            }
            JSONArray jsonArray = result.getJSONArray("data");
            return jsonArray;
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-queryBusinessInfoBatch ] 批量查询企业信息,调用接口异常errorMsg={}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * @Title: orderToDoListForCar
     * @Description: 根据订单编号调取订单服务获取当前可派单车辆
     * @param busCarDTO
     * @return BaseEntity
     * @throws
     */
    @SuppressWarnings({ "resource" })
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public PageDTO orderToDoListForCar(BusCarDTO busCarDTO) {

        // 参数
        Map<String, Object> param = new HashMap<>();
        param.put("orderNo", busCarDTO.getOrderNo());
        param.put("businessId", Common.BUSINESSID);
        param.put("type", busCarDTO.getType());
        param.put("sign", SignUtils.createMD5Sign(param, Common.KEY));// 签名
        logger.info("[ BusAssignmentService-orderToDoListForCar ] 请求参数   param={}", param);

        try {
            // 请求接口
            String urlParam = BusConst.Order.BUS_IN_SERVICE_LIST + "?" + MapUrlParamUtils.getUrlParamsByMap(param);
            logger.info("[ BusAssignmentService-orderToDoListForCar ] urlParam:{}", urlParam);

            JSONObject result = carRestTemplate.getForObject(urlParam, JSONObject.class);
            int code = result.getIntValue("code");
            String msg = result.getString("msg");

            // 接口异常
            if (code == 1) {
                logger.info("[ BusAssignmentService-orderToDoListForCar ] 查询不可派单车辆列表出错, 错误码:{}, 错误原因:{}", code, msg);
                return new PageDTO();
            }

            // 返回数据
            JSONArray data = result.getJSONArray("data");
            List<String> invalidLicensePlatesList = new ArrayList<>();
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    JSONObject bean = data.getJSONObject(i);
                    invalidLicensePlatesList.add((String) bean.get("licensePlates"));
                }
            }
            logger.info("[ BusAssignmentService-orderToDoListForCar ] 当前不可以指派车辆车牌号:{}", invalidLicensePlatesList);

            // 封装查询参数, 查询可用车辆
            BusCarRicherDTO richerDTO = BeanUtil.copyObject(busCarDTO, BusCarRicherDTO.class);
            // 扩大车型查询范围
            if (busCarDTO.getGroupId() != null) {
                int seatNum = carBizCarGroupExMapper.getSeatNumByGroupId(busCarDTO.getGroupId());
                richerDTO.setSeatNum(seatNum);
            }
            // 不可用车辆
            if (!invalidLicensePlatesList.isEmpty()) {
                richerDTO.setInvalidLicensePlatesList(invalidLicensePlatesList);
            }
            // 当然用户的供应商权限
            richerDTO.setSupplierIds(WebSessionUtil.getCurrentLoginUser().getSupplierIds());
            List<Map<String, Object>> busCarList = carBizCarInfoExMapper.queryBusCarList(richerDTO);
            Page<Map<String, Object>> page = (Page<Map<String, Object>>) busCarList;
            return new PageDTO(page.getPageNum(), page.getPageSize(), page.getTotal(), busCarList);
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-orderToDoListForCar ] 获取不可指派司机错误", e);
        }
        return new PageDTO();
    }

    /**
     * @Title: orderToDoListForDriver
     * @Description: 根据订单编号调取订单服务获取当前可派单司机
     * @param busDriverDTO
     * @return BaseEntity
     * @throws
     */
    @SuppressWarnings("resource")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public PageDTO orderToDoListForDriver(BusDriverDTO busDriverDTO) {

        // 参数
        Map<String, Object> param = new HashMap<>();
        param.put("orderNo", busDriverDTO.getOrderNo());
        param.put("businessId", Common.BUSINESSID);
        param.put("type", busDriverDTO.getType());
        param.put("sign", SignUtils.createMD5Sign(param, Common.KEY));// 签名
        logger.info("[ BusAssignmentService-orderToDoListForDriver ] 请求参数   param={}", param);

        try {
            // 请求接口
            String urlParam = BusConst.Order.BUS_IN_SERVICE_LIST + "?" + MapUrlParamUtils.getUrlParamsByMap(param);
            logger.info("[ BusAssignmentService-orderToDoListForDriver ] urlParam:{}", urlParam);

            JSONObject result = carRestTemplate.getForObject(urlParam, JSONObject.class);
            int code = result.getIntValue("code");
            String msg = result.getString("msg");

            // 接口异常
            if (code == 1) {
                logger.info("[ BusAssignmentService-orderToDoListForDriver ] 查询不可派单司机列表出错, 错误码:{}, 错误原因:{}", code, msg);
                return new PageDTO();
            }

            // 返回数据
            JSONArray data = result.getJSONArray("data");
            Set<Integer> invalidDriverIds = new HashSet<>();
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    JSONObject bean = data.getJSONObject(i);
                    invalidDriverIds.add((Integer) bean.get("driverId"));
                }
            }
            logger.info("[ BusAssignmentService-orderToDoListForDriver ] 当前不可以指派司机ID:{}", invalidDriverIds);

            // 封装查询参数, 查询可用司机
            BusDriverRicherDTO richerDTO = BeanUtil.copyObject(busDriverDTO, BusDriverRicherDTO.class);
            // 扩大车型查询范围
            if (busDriverDTO.getGroupId() != null) {
                int seatNum = carBizCarGroupExMapper.getSeatNumByGroupId(busDriverDTO.getGroupId());
                richerDTO.setSeatNum(seatNum);
            }
            //查询被停运的司机
            List<BusBizDriverViolatorsVO> violators = busCarViolatorsService.queryCurrentOutOfDriver();
            if(violators!=null&&violators.size()>0) {
                violators.forEach(o -> invalidDriverIds.add(o.getBusDriverId()));
                logger.info("[ BusAssignmentService-orderToDoListForDriver ] 当前因为停运不可以指派司机ID:{}", invalidDriverIds);
            }
            // 不可用司机
            if (!invalidDriverIds.isEmpty()) {
                richerDTO.setInvalidDriverIds(invalidDriverIds);
            }
            // 当然用户的供应商权限
            richerDTO.setSupplierIds(WebSessionUtil.getCurrentLoginUser().getSupplierIds());
            List<Map<String, Object>> busDriverList = carBizDriverInfoExMapper.queryBusDriverList(richerDTO);
            // 补充司机评分
            if (busDriverList != null && busDriverList.size() > 0) {
                busDriverList.forEach(driver -> {
                    CarBizCustomerAppraisalStatistics appraisal = busCarBizCustomerAppraisalStatisticsService
                            .queryAppraisal((Integer) driver.get("driverId"), LocalDate.now());
                    if (appraisal != null) {
                        driver.put("monthlyScore", appraisal.getEvaluateScore());
                    }
                });
            }
            Page<Map<String, Object>> page = (Page<Map<String, Object>>) busDriverList;
            return new PageDTO(page.getPageNum(), page.getPageSize(), page.getTotal(), busDriverList);
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-orderToDoListForDriver ] 获取不可指派司机错误", e);
        }
        return new PageDTO();
    }

    /**
     * @Title: busDispatcher
     * @Description: 指派巴士订单
     * @param cityName
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param dispatcherPhone
     * @param groupId
     * @param groupName
     * @param licensePlates
     * @param orderId
     * @param orderNo
     * @param serviceTypeId
     * @return JSONObject
     * @throws
     */
    public JSONObject busDispatcher(String cityName, Integer driverId, String driverName, String driverPhone,
                                    String dispatcherPhone, Integer groupId, String groupName, String licensePlates,
                                    Integer orderId, String orderNo, Integer serviceTypeId) {

        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("orderNo", orderNo);
            paramMap.put("orderId", orderId);
            paramMap.put("driverId", driverId);
            paramMap.put("driverPhone", driverPhone);
            paramMap.put("driverName", driverName);
            paramMap.put("licensePlates", licensePlates);
            paramMap.put("groupId", groupId);
            paramMap.put("groupName", groupName);
            paramMap.put("serviceTypeId", serviceTypeId);
            paramMap.put("cityName", cityName);
            paramMap.put("yardmanPhone", dispatcherPhone);
            logger.info("[ BusAssignmentService-busDispatcher ] 接口参数：{}", paramMap);
            String response = busAssignmentTemplate.postForObject(BusConst.Dispatcher.BUS_DISPATCHER, JSONObject.class, paramMap);

            return JSON.parseObject(response);
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-busDispatcher ] 巴士派单失败.", e);
        }
        return null;
    }

    /**
     * @Title: updateDriver
     * @Description: 指派巴士订单
     * @param cityName
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param groupId
     * @param groupName
     * @param licensePlates
     * @param orderId
     * @param orderNo
     * @param serviceTypeId
     * @return JSONObject
     * @throws
     */
    public JSONObject updateDriver(String cityName, Integer driverId, String driverName, String driverPhone,
                                   Integer groupId, String groupName, String licensePlates, Integer orderId, String orderNo,
                                   Integer serviceTypeId) {
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            String key = Common.MAIN_ORDER_KEY; // 请求key
            paramMap.put("orderNo", orderNo);
            paramMap.put("businessId", Common.BUSSINESSID);
            paramMap.put("driverId", driverId);
            paramMap.put("licensePlates", licensePlates);
            paramMap.put("carGroupId", groupId);
            paramMap.put("carGroupName", groupName);
            paramMap.put("sign", SignUtils.createMD5Sign(paramMap, key));
            logger.info("[ BusAssignmentService-updateDriver ] 接口参数：{}", paramMap);
            String response = carRestTemplate.postForObject(BusConst.Order.UPDATE_DRIVER, JSONObject.class, paramMap);
            return JSON.parseObject(response);
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-updateDriver ] 巴士改派失败", e);
        }
        return null;
    }
    
    /**
     * @Title: orderNoToMaid
     * @Description: 通知分佣账户
     * @param orderNo void
     * @throws
     */
    public void orderNoToMaid(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            logger.error("[ BusAssignmentService-orderNoToMaid ] 同步分佣订单号，orderNo is null");
        }
        //派单成功后，调用分佣接口，告诉分佣
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        logger.info("[ BusAssignmentService-orderNoToMaid ] 同步分佣订单号,请求参数,params={}", params);
        try {
        	String url = chargeBaseUrl + BusConst.Charge.BUS_MAID;
        	JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 3000, "同步分佣订单号");
        	logger.info("[ BusAssignmentService-orderNoToMaid ] 同步分佣订单号,响应结果,result={}", result);
        	
            if (result.getIntValue("code") == 0) {
                logger.info("[ BusAssignmentService-orderNoToMaid ] 同步分佣订单号成功,orderNo={}", orderNo);
            } else {
				logger.info("[ BusAssignmentService-orderNoToMaid ] 同步分佣订单号失败,orderNo={}, msg={}", orderNo, result.getString("msg"));
            }
        } catch (Exception e) {
            logger.error("[ BusAssignmentService-orderNoToMaid ] 同步分佣订单号异常", e);
        }
    }

}
