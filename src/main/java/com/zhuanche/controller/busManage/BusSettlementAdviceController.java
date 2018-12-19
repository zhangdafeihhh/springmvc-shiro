package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.constants.busManage.BusConstant.SupplierMaidConstant;
import com.zhuanche.dto.busManage.BusSettleChangeDTO;
import com.zhuanche.dto.busManage.BusSettlementOrderChangeDTO;
import com.zhuanche.dto.busManage.BusSupplierSettleListDTO;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.serv.busManage.BusSettlementAdviceService;
import com.zhuanche.serv.busManage.BusSupplierService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierSettleDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: BusSettlementAdviceController
 * @Description: 巴士结算单管理
 * @author: yanyunpeng
 * @date: 2018年12月7日 上午11:28:50
 */
@RestController
@RequestMapping("/bus/settlement")
@Validated
public class BusSettlementAdviceController {

    private static final Logger logger = LoggerFactory.getLogger(BusSettlementAdviceController.class);
    private static final String LOG_PRE = "【供应商分佣结算单管理】";

    @Autowired
    private BusCommonService busCommonService;

    @Autowired
    private BusSettlementAdviceService busSettlementAdviceService;

    @Autowired
    private BusSupplierService busSupplierService;


    /**
     * 查询供应商账单列表 TODO 等计费完成接口完善
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/pageList")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleDetailList(BusSupplierSettleListDTO dto) {
        logger.info(LOG_PRE + "查询账单列表参数=" + JSON.toJSONString(dto));
        getAuth(dto);
        Integer cityId = dto.getCityId();
        Integer supplierId = dto.getSupplierId();
        if (supplierId != null) {
            dto.setSupplierIds(String.valueOf(supplierId));
        } else {
            if (cityId == null) {
                Set<Integer> authOfSupplier = dto.getAuthOfSupplier();
                String join = StringUtils.join(authOfSupplier, ",");
                dto.setSupplierIds(join);
            } else {
                List<Map<Object, Object>> maps = busCommonService.querySuppliers(cityId);
                if (maps.isEmpty()) {
                    return AjaxResponse.success(new ArrayList<>());
                }
                StringBuffer sb = new StringBuffer();
                for (Map<Object, Object> map : maps) {
                    sb.append(String.valueOf(map.get("supplierId"))).append(",");
                }
                dto.setSupplierIds(sb.substring(0, sb.length() - 1));
            }
        }
        JSONObject result = busSettlementAdviceService.querySettleDetailList(dto);
        Integer code = result.getInteger("code");
        if (code == null || 0 != code) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "查询失败，请联系管理员");
        }
        JSONArray data = result.getJSONArray("data");
        if (data == null || data.isEmpty()) {
            return AjaxResponse.success(new ArrayList<>());
        }
        Map<Integer, BusSupplierInfoVO> supplierInfoMap = querySupplierInfo(data);
        List<BusSupplierSettleDetailVO> collect = data.stream().map(o -> (JSONObject) o).map(o -> {
            BusSupplierSettleDetailVO detail = JSONObject.toJavaObject(o, BusSupplierSettleDetailVO.class);
            Date startTime = new Date(o.getLong("startTime"));
            Date endTime = new Date(o.getLong("endTime"));
            Date settleTime = new Date(o.getLong("settleTime"));
            detail.setStartTime(DateUtil.getTimeString(startTime));
            detail.setEndTime(DateUtil.getTimeString(endTime));
            detail.setSettleTime(DateUtil.getTimeString(settleTime));
            return detail;
        }).map(o -> {
            return buidSettleDetailVO(o, supplierInfoMap);
        }).collect(Collectors.toList());
        logger.info(LOG_PRE + "查询账单列表参数=" + JSON.toJSONString(dto) + "结果=" + JSON.toJSONString(collect));
        return AjaxResponse.success(collect);
    }

    private BusSupplierSettleDetailVO buidSettleDetailVO(BusSupplierSettleDetailVO settleDetail, Map<Integer, BusSupplierInfoVO> infoMap) {
        BusSupplierInfoVO info = infoMap.get(settleDetail.getSupplierId());
        if (info != null) {
            settleDetail.setCityName(info.getCityName());
            settleDetail.setSupplierName(info.getSupplierName());
        }
        return settleDetail;
    }

    private Map<Integer, BusSupplierInfoVO> querySupplierInfo(JSONArray array) {
        Set<Integer> queryparam = array.stream().map(O -> (JSONObject) O).map(O -> O.getInteger("supplierId")).collect(Collectors.toSet());
        //查询供应商的基本信息
        List<BusSupplierInfoVO> supplierInfo = busSupplierService.queryBasicInfoByIds(queryparam);
        Map<Integer, BusSupplierInfoVO> supplierInfoMap = new HashMap<>(16);
        supplierInfo.forEach(o -> {
            supplierInfoMap.put(o.getSupplierId(), o);
        });
        return supplierInfoMap;
    }

    private void getAuth(BusSupplierSettleListDTO dto) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Set<Integer> authSupper = loginUser.getSupplierIds();
        Set<Integer> authCity = loginUser.getCityIds();
        if (authSupper.isEmpty()) {
            Map<String, Set<Integer>> param = new HashMap<>(2);
            param.put("cityIds", authCity);
            List<Integer> integers = busSupplierService.querySupplierIdByCitys(param);
            authSupper = new HashSet<>(integers);
        }
        dto.setAuthOfCity(authCity);
        dto.setAuthOfSupplier(authSupper);
    }

    @RequestMapping("/exportList")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public void exportSettleDetailList(BusSupplierSettleListDTO dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("巴士供应商查询账单列表参数=" + JSON.toJSONString(dto));
        //文件名称
        String fileName = BusConstant.buidFileName(request, SupplierMaidConstant.BILL_FILE_NAME);
        //构建文件标题
        List<String> headerList = new ArrayList<>();
        headerList.add(SupplierMaidConstant.BILL_EXPORT_HEAD);
        CsvUtils utilEntity = new CsvUtils();
        getAuth(dto);
        Integer cityId = dto.getCityId();
        Integer supplierId = dto.getSupplierId();
        if (supplierId != null) {
            dto.setSupplierIds(String.valueOf(supplierId));
        } else {
            if (cityId == null) {
                Set<Integer> authOfSupplier = dto.getAuthOfSupplier();
                String join = StringUtils.join(authOfSupplier, ",");
                dto.setSupplierIds(join);
            } else {
                List<Map<Object, Object>> maps = busCommonService.querySuppliers(cityId);
                if (maps.isEmpty()) {
                    List<String> csvDataList = new ArrayList<>();
                    csvDataList.add("没有符合条件的数据");
                    utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
                    return;
                }
                StringBuffer sb = new StringBuffer();
                for (Map<Object, Object> map : maps) {
                    sb.append(String.valueOf(map.get("supplierId"))).append(",");
                }
                dto.setSupplierIds(sb.substring(0, sb.length() - 1));
            }
        }
        Integer pageNum = 0;
        boolean isFirst = true;
        boolean isList = false;
        do {
            pageNum++;
            dto.setPageNum(pageNum);
            dto.setPageSize(BusConstant.EXPORT_PAGE_SIZE);
            JSONObject result = busSettlementAdviceService.querySettleDetailList(dto);
            Integer code = result.getInteger("code");
            if (code == null || code != 0) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("导出异常，请稍后重试，或者联系管理员");
                isList = true;
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isList);
                break;
            }
            JSONArray array = result.getJSONArray("data");
            if (array == null || array.isEmpty()) {
                isList = true;
            }
            List<String> csvData;
            if (pageNum == 1) {
                csvData = new ArrayList<>();
                csvData.add("没有符合条件的数据");
            }
            Map<Integer, BusSupplierInfoVO> supplierInfoMap = querySupplierInfo(array);
            csvData = array.stream().map(o -> (JSONObject) o).map(o -> {
                BusSupplierSettleDetailVO detail = JSONObject.toJavaObject(o, BusSupplierSettleDetailVO.class);
                Date startTime = new Date(o.getLong("startTime"));
                Date endTime = new Date(o.getLong("endTime"));
                Date settleTime = new Date(o.getLong("settleTime"));
                detail.setStartTime(DateUtil.getTimeString(startTime));
                detail.setEndTime(DateUtil.getTimeString(endTime));
                detail.setSettleTime(DateUtil.getTimeString(settleTime));
                return detail;
            }).map(o -> {
                return buidSettleDetailVO(o, supplierInfoMap);
            }).map(BusSupplierSettleDetailVO::toString).collect(Collectors.toList());
            utilEntity.exportCsvV2(response, csvData, headerList, fileName, isFirst, isList);
            // isList=true时表示时之后一页停止循环
        } while (!isList);
    }

    /**
     * 修改账单
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/update/save", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse updateSettlement(BusSettleChangeDTO dto) {
        BusSettlementOrderChangeDTO orderChangeDTO = new BusSettlementOrderChangeDTO();
        BeanUtils.copyProperties(dto, orderChangeDTO);
        orderChangeDTO.setOrderNo(dto.getSupplierBillId() + DateUtil.creatConciseTimeString());
        logger.info(LOG_PRE + "修改结算单参数=" + JSON.toJSONString(orderChangeDTO));
        JSONObject result = busSettlementAdviceService.updateSupplierBill(orderChangeDTO);
        Integer code = result.getInteger("code");
        if (code == null || 0 != code) {
            logger.error(LOG_PRE + "修改账单失败=" + result.getString("msg"));
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "修改账单失败");
        }
        return AjaxResponse.success(new ArrayList());
    }

    /**
     * 修改订单
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/transactions/save", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse orderRevision(BusSettlementOrderChangeDTO dto) {
        logger.info(LOG_PRE + "修改订单记录,参数=" + JSON.toJSONString(dto));
        JSONObject result = busSettlementAdviceService.updateSupplierBill(dto);
        Integer code = result.getInteger("code");
        if (code == null || 0 != code) {
            logger.error(LOG_PRE + "修改订单记录失败=" + result.getString("msg"));
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "修改订单记录失败");
        }
        return AjaxResponse.success(new ArrayList());
    }
}
