package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.constants.busManage.BusConstant.CarConstant;
import com.zhuanche.constants.busManage.EnumFuel;
import com.zhuanche.dto.busManage.BusCarSaveDTO;
import com.zhuanche.dto.busManage.BusInfoDTO;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.busManage.BusBizChangeLogService;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.serv.busManage.BusInfoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.excel.ExportExcelUtil;
import com.zhuanche.vo.busManage.*;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @program: mp-manage
 * @description: 巴士车辆管理
 * @author: niuzilian
 * @create: 2018-11-22 14:22
 **/
@RestController
@RequestMapping("/bus/busInfo")
public class BusInfoController {
    private static Logger logger = LoggerFactory.getLogger(BusInfoController.class);
    private static String LOG_PRE = "【巴士车辆】";
    @Autowired
    private BusInfoService busInfoService;
    @Autowired
    private CarBizCarGroupService groupService;
    @Autowired
    private CarBizSupplierService supplierService;
    @Autowired
    private CarBizCityService cityService;
    @Autowired
    private BusBizChangeLogService busBizChangeLogService;
    @Autowired
    private BusCommonService commonService;

    @Autowired
    private CarBizCarGroupService carBizCarGroupService;
    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;

    private static final String licensePlateTemplate = "京A12345";

    /**
     * @Description:查询巴士车辆列表
     * @Param: [busDTO]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/23
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse queryList(BusInfoDTO busDTO) {
        //从session中获取权限
        busDTO.setAuthOfCity(WebSessionUtil.getCurrentLoginUser().getCityIds());
        busDTO.setAuthOfSupplier(WebSessionUtil.getCurrentLoginUser().getSupplierIds());
        logger.info(LOG_PRE + "查询车辆列表参数=" + JSON.toJSONString(busDTO));
        PageInfo<BusInfoVO> pageInfo = busInfoService.queryList(busDTO);
        return AjaxResponse.success(new PageDTO(busDTO.getPageNum(), busDTO.getPageSize(), Integer.parseInt(pageInfo.getTotal() + ""), pageInfo.getList()));
    }

    @RequestMapping("queryAuditList")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE))
    public AjaxResponse queryAuditList(BusInfoDTO busDTO){
        logger.info(LOG_PRE + "查询车辆审核列表参数=" + JSON.toJSONString(busDTO));
        //获取数据权限
        Set<Integer> supplierIds = commonService.getAuthSupplierIds();
        if(supplierIds==null){
          return AjaxResponse.fail(RestErrorCode.PERMISSION_NOT_EXIST);
        }
        if(busDTO.getSupplierId()!=null){
           if(supplierIds.isEmpty()||supplierIds.contains(busDTO.getSupplierId())){
               supplierIds.clear();
               supplierIds.add(busDTO.getSupplierId());
           }else{
              return AjaxResponse.fail(RestErrorCode.PERMISSION_NOT_EXIST);
           }
        }
        busDTO.setAuthOfSupplier(supplierIds);
        try {
            AjaxResponse response = busInfoService.queryAuditList(busDTO);
            return  response;
        } catch (Exception e) {
            logger.error(LOG_PRE + "查询车辆审核列表参数=" + JSON.toJSONString(busDTO)+" 异常，e：{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping("/auditCar")

    public AjaxResponse audit(@Verify(param="ids",rule="required")  String ids){
        logger.info(LOG_PRE + " 审核车辆信息，参数：" + ids );
        String repeatCommitKey = "AUDIT_BUS_CAR_KEY" + ids;
        Long incr = RedisCacheUtil.incr(repeatCommitKey);
        RedisCacheUtil.expire(repeatCommitKey,10);
        try {
            if(incr == 1){
                AjaxResponse audit = busInfoService.audit(ids);
                return audit;
            }else {
                return AjaxResponse.failMsg(RestErrorCode.BUS_COMMON_ERROR_CODE,"请勿重复点击");
            }
        } catch (Exception e) {
            logger.error(LOG_PRE + " 审核车辆信息，参数：" + ids +" 异常：{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping("getAuditDetail")

    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE))
    public AjaxResponse getAuditDetail(@Verify(param="id",rule="required")String id){
        logger.info(LOG_PRE + " 查询审核车辆信息详情，参数：" + id );
        try {
            AjaxResponse auditDetail = busInfoService.getAuditDetail(id);
            return AjaxResponse.success(auditDetail);
        } catch (Exception e) {
            logger.error(LOG_PRE + " 查询审核车辆信息详情，参数：" + id+" 异常：{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
    @RequestMapping("updateAuditCar")
    public AjaxResponse updateAuditCar(BusCarSaveDTO saveDTO){
        logger.info(LOG_PRE+" 修改审核信息，参数："+JSON.toJSONString(saveDTO));
        try {
            AjaxResponse response = busInfoService.updateAuditCar(saveDTO);
            return AjaxResponse.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LOG_PRE+" 修改审核信息，参数："+JSON.toJSONString(saveDTO)+" 异常：{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
    /**
     * @Description: 查询车辆详情
     * @Param: [carId]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/23
     */
    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse getDetail(@Verify(param = "carId", rule = "required") Integer carId) {
        logger.info(LOG_PRE + "查询车辆详情参数carId=" + carId);
        BusDetailVO detail = busInfoService.getDetail(carId);
        if (detail == null) {
            return AjaxResponse.failMsg(RestErrorCode.BUS_NOT_EXIST, "车辆信息不存在");
        }
        String fuelName = EnumFuel.getFuelNameByCode(detail.getFueltype());
        detail.setFuelName(fuelName);
        logger.info(LOG_PRE + "查询车辆详情结果" + JSON.toJSONString(detail));
        return AjaxResponse.success(detail);
    }

    @RequestMapping(value = "/saveCar", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER))
    public AjaxResponse saveCar(@Validated BusCarSaveDTO busCarSaveDTO) {
        String repeatCommitKey = "SAVE_BUS_DRIVER_KEY" + busCarSaveDTO.getLicensePlates();
        Long incr = RedisCacheUtil.incr(repeatCommitKey);
        RedisCacheUtil.expire(repeatCommitKey,10);
        if(incr != 1){
            return AjaxResponse.failMsg(RestErrorCode.BUS_COMMON_ERROR_CODE,"请勿重复点击");
        }
        logger.info(LOG_PRE + "保存车辆信息，参数=" + JSON.toJSONString(busCarSaveDTO));
        String fuelName = EnumFuel.getFuelNameByCode(busCarSaveDTO.getFueltype());
        if (fuelName == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "燃料类型不存在");
        }
        CarBizCarGroup group = carBizCarGroupService.selectByPrimaryKey(busCarSaveDTO.getGroupId());
        if (group == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "服务类型有误");
        }
        if (busCarSaveDTO.getCarId() == null) {
            boolean b = busInfoService.licensePlatesIfExist(busCarSaveDTO.getLicensePlates());
            if (b) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "车牌号已经存在");
            }
            return busInfoService.saveCarToAuditCollect(busCarSaveDTO);
        } else {
            logger.info(LOG_PRE + "修改车辆信息，参数=" + JSON.toJSONString(busCarSaveDTO));
            return busInfoService.updateCarById(busCarSaveDTO);
        }

    }

    /**
     * @Description: 导入巴士车辆信息
     * @Param: [cityId, supplierId, file]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/28
     */
    @RequestMapping(value = "/importBusInfo", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse importBusInfo(Integer cityId,
                                      Integer supplierId,
                                      MultipartFile file) throws Exception {
        if (file==null||file.isEmpty() || cityId == null || supplierId == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "有参数为空");
        }
        String supplierName = supplierService.getSupplierNameById(supplierId);
        if (StringUtils.isBlank(supplierName)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "供应商不存在");
        }
        String cityName = cityService.queryNameById(cityId);
        if (StringUtils.isBlank(cityName)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "城市不存在");
        }

        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        logger.info(LOG_PRE + "上传的文件名为:{},上传的后缀名为:{}", fileName, suffixName);
        InputStream is = file.getInputStream();
        Workbook workbook = null;
        if (suffixName.equals(".xls")) {
            workbook = new HSSFWorkbook(is);
        } else if (suffixName.equals(".xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            logger.error(LOG_PRE + "巴士导入车辆文件后缀名错误");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        //默认值导入第一个sheet;
        Sheet sheet = workbook.getSheetAt(0);
        //判断是否是运营角色
        boolean roleBoolean = commonService.ifOperate();
        //校验模板表头
        Row rowFirst = sheet.getRow(0);

        String[] heads = null;
        if (roleBoolean) {
            heads = CarConstant.TEMPLATE_HEAD;
        } else {
            heads = new String[CarConstant.TEMPLATE_HEAD.length - 2];
            System.arraycopy(CarConstant.TEMPLATE_HEAD, 2, heads, 0, CarConstant.TEMPLATE_HEAD.length - 2);
        }
        if (rowFirst == null || rowFirst.getLastCellNum() != heads.length) {
            logger.error(LOG_PRE + "巴士导入车辆文件表头为空或者长度错误");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        boolean templateFlag = checkTableHead(rowFirst, heads);
        if (!templateFlag) {
            logger.error(LOG_PRE + "巴士导入车辆文件表头内容错误");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }

        // 最后一行数据的下标，刚好可以表示总数据条数（标题不算）
        int listDataIdx = sheet.getLastRowNum();
        int total = listDataIdx;

        //判断除了标题外，判断下标1有没有样例数据，如果有从下标2开始读取数据，没有，则从下标1开始读取
        int licensePlateIdx = 0;
        for (int i = 0; i < heads.length; i++) {
            if (heads[i].equals("车牌号")) {
                licensePlateIdx = i;
                break;
            }
        }
        //读取数据开始行的下标
        int start = 1;
        //京A12345 为导出模板时写入的样例数据
        Row rowDataFirst = sheet.getRow(1);
        Cell cellFirst = rowDataFirst.getCell(licensePlateIdx);
        String licenseFirst = readCellValue(cellFirst);
        if (licensePlateTemplate.equals(licenseFirst)) {
            start = 2;
            //过滤掉样例，总的条数需要-1；
            total = total - 1;
        }
        if (total == 0) {
            logger.error(LOG_PRE + "巴士导入车辆文件内容为空");
            return AjaxResponse.fail(RestErrorCode.BUS_NOT_EXIST);
        }
        // 成功导入条数
        int successCount = 0;
        List<ErrorReason> errList = new ArrayList();
        logger.info(LOG_PRE + "巴士导入车辆，总条数=" + total);
        //循环行
        for (int rowIdx = start; rowIdx <= listDataIdx; rowIdx++) {
            // 获取行对象
            Row row = sheet.getRow(rowIdx);
            if (row == null) {
                saveErrorMsg(errList, rowIdx, 0, "数据为空");
                continue;
            }
            // BusCarInfo carInfo = new BusCarInfo();
            BusCarSaveDTO saveDTO = new BusCarSaveDTO();
            boolean validFlag = true;
            // 循环列
            for (int colIdx = 0; colIdx < heads.length; colIdx++) {
                Cell cell = row.getCell(colIdx);
                String value = readCellValue(cell);
                DateFormat df = new SimpleDateFormat("yyyy-M-d");
                //获取表头信息
                String head = heads[colIdx];
                switch (head) {
                    //城市：
                    case "城市":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        if (!value.equals(cityName)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 城市名称和页面选择的不一致");
                            validFlag = false;
                            break;
                        }
                        break;
                    //供应商：
                    case "供应商":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                        }
                        if (!value.equals(supplierName)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 供应商名称和页面选择的不一致");
                            validFlag = false;
                        }
                        break;
                    //车牌号
                    case "车牌号":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        if (value.length() > 10) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 最大长度为10");
                            validFlag = false;
                            break;
                        }
                        boolean checkResult = busInfoService.licensePlatesIfExist(value);
                        if (checkResult) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 已经存在");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setLicensePlates(value);
                        break;
                    // 车型类别
                    case "车型类别名称":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        CarBizCarGroup group = carBizCarGroupExMapper.queryGroupByGroupNameAndStatus(value,1);
                        if (group == null) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 不存在");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setGroupId(group.getGroupId());
                        break;
                    //车辆颜色
                    case "车辆颜色":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        if (value.length() > 10) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 最大长度为10");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setColor(value);
                        break;
                    //燃料类型
                    case "燃料类别":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        String code = EnumFuel.getFuelCodeByName(value);
                        if (code == null) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 不存在");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setFueltype(code);
                        break;
                    //道路运输证号
                    case "道路运输证号":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        if (value.length() > 50) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 最大长度为50");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setTransportnumber(value);
                        break;
                    //车厂厂牌
                    case "车辆厂牌":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 为空");
                            validFlag = false;
                            break;
                        }
                        if (value.length() > 50) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 最大长度为50");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setVehicleBrand(value);
                        break;
                    //具体车型
                    case "具体车型(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            if (value.length() > 50) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 最大长度为50");
                                validFlag = false;
                                break;
                            }
                            saveDTO.setModelDetail(value);
                        }
                        break;
                    //下次车检时间
                    case "下次车检时间(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextInspectDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 时间格式错误，例：2018-01-01");
                            }
                        }
                        break;
                    //下次维保时间
                    case "下次维保时间(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextMaintenanceDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 时间格式错误，例：2018-01-01");
                            }
                        }
                        break;
                    //下次运营检测时间
                    case "下次运营证检测时间(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextOperationDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 时间格式错误，例：2018-01-01");
                            }
                        }
                        break;
                    //购买时间
                    case "购买时间(选填)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setCarPurchaseDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " 时间格式错误，例：2018-01-01");
                            }
                        }
                        break;
                    default:
                        break;
                }// switch end

            }// 列循环结束
            if (validFlag) {
                saveDTO.setCityId(cityId);
                saveDTO.setSupplierId(supplierId);
                //默认有效
                saveDTO.setStatus(1);
                AjaxResponse response = busInfoService.saveCarToAuditCollect(saveDTO);
                if (response.isSuccess()) {
                    successCount++;
                }
            }
        }
        ImportErrorVO errorVO = new ImportErrorVO(total, successCount, (total - successCount), errList);
        //如果有错误信息将信息放到redis中以便下载
        if (errList.size() > 0) {
            String errMsgKey = BusConstant.ERROR_CAR_KEY + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            saveErrorMsg2Redis(errMsgKey, errList);
            errorVO.setErrorMsgKey(errMsgKey);
        }
        return AjaxResponse.success(errorVO);
    }

    private String transfTime(String data) {
        if (data.contains("/")) {
            data = data.replace("/", "-");
        }
        return data;
    }

    private void saveErrorMsg2Redis(String key, List<ErrorReason> errorList) {
        List<String> array = new ArrayList();
        errorList.forEach(o -> {
            array.add("第" + o.getRow() + "行，" + "第" + o.getCol() + "列  " + o.getReason());
        });
        RedisCacheUtil.set(key, array, BusConstant.ERROR_IMPORT_KEY_EXPIRE);
    }

    /**
     * @Description: 下载导入模板
     * @Param: [request, response]
     * @return: void
     * @Date: 2018/11/28
     */
    @RequestMapping("/exportTemplate")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = "巴士车辆导入模板";
        List<Map<Object, Object>> maps = commonService.queryGroups();
        String[] groupNames = new String[maps.size()];
        for (int i = 0; i < maps.size(); i++) {
            groupNames[i] = (String) maps.get(i).get("groupName");
        }
        String allFuelName = EnumFuel.getAllFuelName();
        String[] fuelNames = allFuelName.split(",");
        List<String[]> downdata = new ArrayList<>();
        downdata.add(groupNames);
        downdata.add(fuelNames);

        //构建模板事例
        List contentList = new ArrayList();
        BusCarImportTemplateVO templateVO = new BusCarImportTemplateVO();
        templateVO.setCityName("北京");
        templateVO.setSupplierName("测试集团01");
        templateVO.setLicensePlates(licensePlateTemplate);
        templateVO.setGroupName("巴士18座");
        templateVO.setColor("黄色");
        templateVO.setFuelName("汽油");
        templateVO.setVehicleBrand("宇通");
        templateVO.setModelDetail("宇通ZK6908H9");
        templateVO.setTransportnumber("粤交运管朝阳字123456 123456号");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        templateVO.setNextInspectDate(sf.parse("2008-01-01"));
        templateVO.setNextMaintenanceDate(sf.parse("2019-01-01"));
        templateVO.setNextOperationDate(sf.parse("2019-01-01"));
        templateVO.setCarPurchaseDate(sf.parse("2011-01-01"));
        //判断是否是运营角色
        boolean roleBoolean = commonService.ifOperate();
        //文件表头
        String[] head = null;
        //下拉框所在位置
        String[] downRows = new String[2];
        //如果是巴士运营角色,下载的模板包括城市和供应商
        if (roleBoolean) {
            head = CarConstant.TEMPLATE_HEAD;
            downRows[0] = "3";
            downRows[1] = "5";
            contentList.add(templateVO);
        } else {
            //非运营角色下载的模板不包括城市和供应商
            head = new String[CarConstant.TEMPLATE_HEAD.length - 2];
            System.arraycopy(CarConstant.TEMPLATE_HEAD, 2, head, 0, CarConstant.TEMPLATE_HEAD.length - 2);
            downRows[0] = "1";
            downRows[1] = "3";
            BusCarBaseImportTemplateVO busCarBaseImportTemplateVO = new BusCarBaseImportTemplateVO();
            BeanUtils.copyProperties(templateVO, busCarBaseImportTemplateVO);
            contentList.add(busCarBaseImportTemplateVO);
        }

        ExportExcelUtil.exportExcel(fileName, head, downdata, downRows, contentList, request, response);
    }


    /**
     * @Description: 导出车辆信息
     * @Param: [busDTO, request, response]
     * @return: void
     * @Date: 2018/11/28
     */
    @RequestMapping("/exportBusInfo")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public void exportCarInfo(BusInfoDTO busDTO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //从session中获取权限
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        busDTO.setAuthOfCity(user.getCityIds());
        busDTO.setAuthOfSupplier(user.getSupplierIds());
        //导出信息指定每次查询的页数
         busDTO.setPageSize(CsvUtils.downPerSize);
        logger.info(LOG_PRE + "下载车辆信息参数=" + JSON.toJSONString(busDTO));
        PageInfo<BusInfoVO> pageInfo = busInfoService.queryList(busDTO);
        //文件标题
        List<String> headerList = new ArrayList<>();
        String head = StringUtils.join(CarConstant.TEMPLATE_HEAD, ",");
        headerList.add(head);
        String  fileName = BusConstant.buidFileName(request, CarConstant.FILE_NAME);
        List<String> csvDataList = new ArrayList<>();
        CsvUtils utilEntity = new CsvUtils();
        long total = pageInfo.getTotal();
        if (total == 0) {
            csvDataList.add("没有符合条件的车辆");
            utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
            return;
        }
        buidCSVdata(pageInfo.getList(), csvDataList);
        //总页数
        int pages = pageInfo.getPages();

        if (pages == 1) {
            utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
            return;
        }
        utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, false);
        for (int i = 2; i <= pages; i++) {
            csvDataList.clear();
            busDTO.setPageNum(i);
            List<BusInfoVO> list = busInfoService.queryList(busDTO).getList();
            buidCSVdata(list, csvDataList);
            if (i == pages) {
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, false, true);
            } else {
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, false, false);
            }
        }
    }

    private void buidCSVdata(List<BusInfoVO> infoVOS, List<String> csvData) {
        for (BusInfoVO info : infoVOS) {
            String split = ",";
            StringBuffer sb = new StringBuffer();
            //        String[] TEMPLATE_HEAD={"城市","供应商","车牌号","车型类别名称","车辆颜色","燃料类别","运输证字号","车辆厂牌","具体车型（选填）","下次车检时间（选填）","下次维保时间（选填）","下次运营证检测时间（选填）","购买时间（选填）"};

            String fuelName = EnumFuel.getFuelNameByCode(info.getFueltype());
            sb.append(StringUtils.defaultString(info.getCityName())).append(split)
                    .append(StringUtils.defaultString(info.getSupplierName())).append(split)
                    .append(StringUtils.defaultString(info.getLicensePlates())).append(split)
                    .append(StringUtils.defaultString(info.getGroupName())).append(split)
                    .append(StringUtils.defaultString(info.getColor())).append(split)
                    .append(StringUtils.defaultString(fuelName)).append(split)
                    .append(StringUtils.defaultString(info.getTransportnumber())).append(split)
                    .append(StringUtils.defaultString(info.getVehicleBrand())).append(split)
                    .append(StringUtils.defaultString(info.getModelDetail())).append(split)
                    .append(info.getNextInspectDate() == null ? StringUtils.EMPTY : DateUtils.formatDate(info.getNextInspectDate())).append(split)
                    .append(info.getNextMaintenanceDate() == null ? StringUtils.EMPTY : DateUtils.formatDate(info.getNextMaintenanceDate())).append(split)
                    .append(info.getNextOperationDate() == null ? StringUtils.EMPTY : DateUtils.formatDate(info.getNextOperationDate())).append(split)
                    .append(info.getCarPurchaseDate() == null ? StringUtils.EMPTY : DateUtils.formatDate(info.getCarPurchaseDate()));
            csvData.add(sb.toString());
        }
    }





    /**
     * 获取所有的燃料类型
     *
     * @Param: []
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/12
     */
    @RequestMapping(value = "/queryFuel", method = RequestMethod.GET)
    public AjaxResponse queryFuel() {
        List<Map<String, String>> allFuel = EnumFuel.getAllFuel();
        return AjaxResponse.success(allFuel);
    }


    private String readCellValue(Cell cell) {
        if (cell == null) {
            return StringUtils.EMPTY;
        }
        int cellType = cell.getCellType();
        String value = null;
        switch (cellType) {
            case Cell.CELL_TYPE_NUMERIC:
                short format = cell.getCellStyle().getDataFormat();
                SimpleDateFormat sdf = null;
                if (format == 14 || format == 31 || format == 57 || format == 58) {
                    //日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date val = cell.getDateCellValue();
                    value = sdf.format(val);
                } else if (format == 20 || format == 32) {
                    //时间
                    sdf = new SimpleDateFormat("HH:mm");
                    Date val = cell.getDateCellValue();
                    value = sdf.format(val);
                } else { // 纯数字 只保留整数部分
                    DecimalFormat df = new DecimalFormat("########");
                    value = df.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            default:
                value = StringUtils.EMPTY;
                break;
        }
        return value;
    }

    private void saveErrorMsg(List<ErrorReason> reasons, int row, int col, String reason) {
        ErrorReason errorReason = new ErrorReason();
        errorReason.setRow(row+1);
        errorReason.setCol(col + 1);
        errorReason.setReason(reason);
        reasons.add(errorReason);
    }

    private boolean checkTableHead(Row row, String[] head) {
        boolean templateFlag = true;
        for (int colIdx = 0; colIdx < row.getLastCellNum(); colIdx++) {
            // 获取列对象
            Cell cell = row.getCell(colIdx);
            if (cell == null) {
                templateFlag = false;
                break;
            } else {
                String stringValue = cell.getStringCellValue();
                if (!stringValue.equals(head[colIdx])) {
                    templateFlag = false;
                    break;
                }
            }
        }
        return templateFlag;
    }
}
