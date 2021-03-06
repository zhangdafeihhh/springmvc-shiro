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
 * @description: ??????????????????
 * @author: niuzilian
 * @create: 2018-11-22 14:22
 **/
@RestController
@RequestMapping("/bus/busInfo")
public class BusInfoController {
    private static Logger logger = LoggerFactory.getLogger(BusInfoController.class);
    private static String LOG_PRE = "??????????????????";
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

    private static final String licensePlateTemplate = "???A12345";

    /**
     * @Description:????????????????????????
     * @Param: [busDTO]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/23
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse queryList(BusInfoDTO busDTO) {
        //???session???????????????
        busDTO.setAuthOfCity(WebSessionUtil.getCurrentLoginUser().getCityIds());
        busDTO.setAuthOfSupplier(WebSessionUtil.getCurrentLoginUser().getSupplierIds());
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(busDTO));
        PageInfo<BusInfoVO> pageInfo = busInfoService.queryList(busDTO);
        return AjaxResponse.success(new PageDTO(busDTO.getPageNum(), busDTO.getPageSize(), Integer.parseInt(pageInfo.getTotal() + ""), pageInfo.getList()));
    }

    @RequestMapping("queryAuditList")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE))
    public AjaxResponse queryAuditList(BusInfoDTO busDTO){
        logger.info(LOG_PRE + "??????????????????????????????=" + JSON.toJSONString(busDTO));
        //??????????????????
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
            logger.error(LOG_PRE + "??????????????????????????????=" + JSON.toJSONString(busDTO)+" ?????????e???{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping("/auditCar")

    public AjaxResponse audit(@Verify(param="ids",rule="required")  String ids){
        logger.info(LOG_PRE + " ??????????????????????????????" + ids );
        String repeatCommitKey = "AUDIT_BUS_CAR_KEY" + ids;
        Long incr = RedisCacheUtil.incr(repeatCommitKey);
        RedisCacheUtil.expire(repeatCommitKey,10);
        try {
            if(incr == 1){
                AjaxResponse audit = busInfoService.audit(ids);
                return audit;
            }else {
                return AjaxResponse.failMsg(RestErrorCode.BUS_COMMON_ERROR_CODE,"??????????????????");
            }
        } catch (Exception e) {
            logger.error(LOG_PRE + " ??????????????????????????????" + ids +" ?????????{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping("getAuditDetail")

    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE))
    public AjaxResponse getAuditDetail(@Verify(param="id",rule="required")String id){
        logger.info(LOG_PRE + " ??????????????????????????????????????????" + id );
        try {
            AjaxResponse auditDetail = busInfoService.getAuditDetail(id);
            return AjaxResponse.success(auditDetail);
        } catch (Exception e) {
            logger.error(LOG_PRE + " ??????????????????????????????????????????" + id+" ?????????{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
    @RequestMapping("updateAuditCar")
    public AjaxResponse updateAuditCar(BusCarSaveDTO saveDTO){
        logger.info(LOG_PRE+" ??????????????????????????????"+JSON.toJSONString(saveDTO));
        try {
            AjaxResponse response = busInfoService.updateAuditCar(saveDTO);
            return AjaxResponse.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LOG_PRE+" ??????????????????????????????"+JSON.toJSONString(saveDTO)+" ?????????{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
    /**
     * @Description: ??????????????????
     * @Param: [carId]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/11/23
     */
    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse getDetail(@Verify(param = "carId", rule = "required") Integer carId) {
        logger.info(LOG_PRE + "????????????????????????carId=" + carId);
        BusDetailVO detail = busInfoService.getDetail(carId);
        if (detail == null) {
            return AjaxResponse.failMsg(RestErrorCode.BUS_NOT_EXIST, "?????????????????????");
        }
        String fuelName = EnumFuel.getFuelNameByCode(detail.getFueltype());
        detail.setFuelName(fuelName);
        logger.info(LOG_PRE + "????????????????????????" + JSON.toJSONString(detail));
        return AjaxResponse.success(detail);
    }

    @RequestMapping(value = "/saveCar", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER))
    public AjaxResponse saveCar(@Validated BusCarSaveDTO busCarSaveDTO) {
        String repeatCommitKey = "SAVE_BUS_DRIVER_KEY" + busCarSaveDTO.getLicensePlates();
        Long incr = RedisCacheUtil.incr(repeatCommitKey);
        RedisCacheUtil.expire(repeatCommitKey,10);
        if(incr != 1){
            return AjaxResponse.failMsg(RestErrorCode.BUS_COMMON_ERROR_CODE,"??????????????????");
        }
        logger.info(LOG_PRE + "???????????????????????????=" + JSON.toJSONString(busCarSaveDTO));
        String fuelName = EnumFuel.getFuelNameByCode(busCarSaveDTO.getFueltype());
        if (fuelName == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "?????????????????????");
        }
        CarBizCarGroup group = carBizCarGroupService.selectByPrimaryKey(busCarSaveDTO.getGroupId());
        if (group == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "??????????????????");
        }
        if (busCarSaveDTO.getCarId() == null) {
            boolean b = busInfoService.licensePlatesIfExist(busCarSaveDTO.getLicensePlates());
            if (b) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "?????????????????????");
            }
            return busInfoService.saveCarToAuditCollect(busCarSaveDTO);
        } else {
            logger.info(LOG_PRE + "???????????????????????????=" + JSON.toJSONString(busCarSaveDTO));
            return busInfoService.updateCarById(busCarSaveDTO);
        }

    }

    /**
     * @Description: ????????????????????????
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
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "???????????????");
        }
        String supplierName = supplierService.getSupplierNameById(supplierId);
        if (StringUtils.isBlank(supplierName)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "??????????????????");
        }
        String cityName = cityService.queryNameById(cityId);
        if (StringUtils.isBlank(cityName)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "???????????????");
        }

        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        logger.info(LOG_PRE + "?????????????????????:{},?????????????????????:{}", fileName, suffixName);
        InputStream is = file.getInputStream();
        Workbook workbook = null;
        if (suffixName.equals(".xls")) {
            workbook = new HSSFWorkbook(is);
        } else if (suffixName.equals(".xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            logger.error(LOG_PRE + "???????????????????????????????????????");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        //????????????????????????sheet;
        Sheet sheet = workbook.getSheetAt(0);
        //???????????????????????????
        boolean roleBoolean = commonService.ifOperate();
        //??????????????????
        Row rowFirst = sheet.getRow(0);

        String[] heads = null;
        if (roleBoolean) {
            heads = CarConstant.TEMPLATE_HEAD;
        } else {
            heads = new String[CarConstant.TEMPLATE_HEAD.length - 2];
            System.arraycopy(CarConstant.TEMPLATE_HEAD, 2, heads, 0, CarConstant.TEMPLATE_HEAD.length - 2);
        }
        if (rowFirst == null || rowFirst.getLastCellNum() != heads.length) {
            logger.error(LOG_PRE + "??????????????????????????????????????????????????????");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }
        boolean templateFlag = checkTableHead(rowFirst, heads);
        if (!templateFlag) {
            logger.error(LOG_PRE + "??????????????????????????????????????????");
            return AjaxResponse.fail(RestErrorCode.FILE_TRMPLATE_ERROR);
        }

        // ?????????????????????????????????????????????????????????????????????????????????
        int listDataIdx = sheet.getLastRowNum();
        int total = listDataIdx;

        //????????????????????????????????????1??????????????????????????????????????????2??????????????????????????????????????????1????????????
        int licensePlateIdx = 0;
        for (int i = 0; i < heads.length; i++) {
            if (heads[i].equals("?????????")) {
                licensePlateIdx = i;
                break;
            }
        }
        //??????????????????????????????
        int start = 1;
        //???A12345 ???????????????????????????????????????
        Row rowDataFirst = sheet.getRow(1);
        Cell cellFirst = rowDataFirst.getCell(licensePlateIdx);
        String licenseFirst = readCellValue(cellFirst);
        if (licensePlateTemplate.equals(licenseFirst)) {
            start = 2;
            //????????????????????????????????????-1???
            total = total - 1;
        }
        if (total == 0) {
            logger.error(LOG_PRE + "????????????????????????????????????");
            return AjaxResponse.fail(RestErrorCode.BUS_NOT_EXIST);
        }
        // ??????????????????
        int successCount = 0;
        List<ErrorReason> errList = new ArrayList();
        logger.info(LOG_PRE + "??????????????????????????????=" + total);
        //?????????
        for (int rowIdx = start; rowIdx <= listDataIdx; rowIdx++) {
            // ???????????????
            Row row = sheet.getRow(rowIdx);
            if (row == null) {
                saveErrorMsg(errList, rowIdx, 0, "????????????");
                continue;
            }
            // BusCarInfo carInfo = new BusCarInfo();
            BusCarSaveDTO saveDTO = new BusCarSaveDTO();
            boolean validFlag = true;
            // ?????????
            for (int colIdx = 0; colIdx < heads.length; colIdx++) {
                Cell cell = row.getCell(colIdx);
                String value = readCellValue(cell);
                DateFormat df = new SimpleDateFormat("yyyy-M-d");
                //??????????????????
                String head = heads[colIdx];
                switch (head) {
                    //?????????
                    case "??????":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????");
                            validFlag = false;
                            break;
                        }
                        if (!value.equals(cityName)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????????????????????????????");
                            validFlag = false;
                            break;
                        }
                        break;
                    //????????????
                    case "?????????":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????");
                            validFlag = false;
                        }
                        if (!value.equals(supplierName)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????????????????????????????????????????");
                            validFlag = false;
                        }
                        break;
                    //?????????
                    case "?????????":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????");
                            validFlag = false;
                            break;
                        }
                        if (value.length() > 10) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????10");
                            validFlag = false;
                            break;
                        }
                        boolean checkResult = busInfoService.licensePlatesIfExist(value);
                        if (checkResult) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ????????????");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setLicensePlates(value);
                        break;
                    // ????????????
                    case "??????????????????":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????");
                            validFlag = false;
                            break;
                        }
                        CarBizCarGroup group = carBizCarGroupExMapper.queryGroupByGroupNameAndStatus(value,1);
                        if (group == null) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ?????????");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setGroupId(group.getGroupId());
                        break;
                    //????????????
                    case "????????????":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????");
                            validFlag = false;
                            break;
                        }
                        if (value.length() > 10) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????10");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setColor(value);
                        break;
                    //????????????
                    case "????????????":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????");
                            validFlag = false;
                            break;
                        }
                        String code = EnumFuel.getFuelCodeByName(value);
                        if (code == null) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ?????????");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setFueltype(code);
                        break;
                    //??????????????????
                    case "??????????????????":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????");
                            validFlag = false;
                            break;
                        }
                        if (value.length() > 50) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????50");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setTransportnumber(value);
                        break;
                    //????????????
                    case "????????????":
                        if (StringUtils.isBlank(value)) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ??????");
                            validFlag = false;
                            break;
                        }
                        if (value.length() > 50) {
                            saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????50");
                            validFlag = false;
                            break;
                        }
                        saveDTO.setVehicleBrand(value);
                        break;
                    //????????????
                    case "????????????(??????)":
                        if (StringUtils.isNotBlank(value)) {
                            if (value.length() > 50) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????50");
                                validFlag = false;
                                break;
                            }
                            saveDTO.setModelDetail(value);
                        }
                        break;
                    //??????????????????
                    case "??????????????????(??????)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextInspectDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????????????????2018-01-01");
                            }
                        }
                        break;
                    //??????????????????
                    case "??????????????????(??????)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextMaintenanceDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????????????????2018-01-01");
                            }
                        }
                        break;
                    //????????????????????????
                    case "???????????????????????????(??????)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setNextOperationDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????????????????2018-01-01");
                            }
                        }
                        break;
                    //????????????
                    case "????????????(??????)":
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                saveDTO.setCarPurchaseDate(df.parse(transfTime(value)));
                            } catch (ParseException e) {
                                saveErrorMsg(errList, rowIdx, colIdx, heads[colIdx] + " ???????????????????????????2018-01-01");
                            }
                        }
                        break;
                    default:
                        break;
                }// switch end

            }// ???????????????
            if (validFlag) {
                saveDTO.setCityId(cityId);
                saveDTO.setSupplierId(supplierId);
                //????????????
                saveDTO.setStatus(1);
                AjaxResponse response = busInfoService.saveCarToAuditCollect(saveDTO);
                if (response.isSuccess()) {
                    successCount++;
                }
            }
        }
        ImportErrorVO errorVO = new ImportErrorVO(total, successCount, (total - successCount), errList);
        //????????????????????????????????????redis???????????????
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
            array.add("???" + o.getRow() + "??????" + "???" + o.getCol() + "???  " + o.getReason());
        });
        RedisCacheUtil.set(key, array, BusConstant.ERROR_IMPORT_KEY_EXPIRE);
    }

    /**
     * @Description: ??????????????????
     * @Param: [request, response]
     * @return: void
     * @Date: 2018/11/28
     */
    @RequestMapping("/exportTemplate")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = "????????????????????????";
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

        //??????????????????
        List contentList = new ArrayList();
        BusCarImportTemplateVO templateVO = new BusCarImportTemplateVO();
        templateVO.setCityName("??????");
        templateVO.setSupplierName("????????????01");
        templateVO.setLicensePlates(licensePlateTemplate);
        templateVO.setGroupName("??????18???");
        templateVO.setColor("??????");
        templateVO.setFuelName("??????");
        templateVO.setVehicleBrand("??????");
        templateVO.setModelDetail("??????ZK6908H9");
        templateVO.setTransportnumber("?????????????????????123456 123456???");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        templateVO.setNextInspectDate(sf.parse("2008-01-01"));
        templateVO.setNextMaintenanceDate(sf.parse("2019-01-01"));
        templateVO.setNextOperationDate(sf.parse("2019-01-01"));
        templateVO.setCarPurchaseDate(sf.parse("2011-01-01"));
        //???????????????????????????
        boolean roleBoolean = commonService.ifOperate();
        //????????????
        String[] head = null;
        //?????????????????????
        String[] downRows = new String[2];
        //???????????????????????????,???????????????????????????????????????
        if (roleBoolean) {
            head = CarConstant.TEMPLATE_HEAD;
            downRows[0] = "3";
            downRows[1] = "5";
            contentList.add(templateVO);
        } else {
            //?????????????????????????????????????????????????????????
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
     * @Description: ??????????????????
     * @Param: [busDTO, request, response]
     * @return: void
     * @Date: 2018/11/28
     */
    @RequestMapping("/exportBusInfo")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public void exportCarInfo(BusInfoDTO busDTO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //???session???????????????
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        busDTO.setAuthOfCity(user.getCityIds());
        busDTO.setAuthOfSupplier(user.getSupplierIds());
        //???????????????????????????????????????
         busDTO.setPageSize(CsvUtils.downPerSize);
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(busDTO));
        PageInfo<BusInfoVO> pageInfo = busInfoService.queryList(busDTO);
        //????????????
        List<String> headerList = new ArrayList<>();
        String head = StringUtils.join(CarConstant.TEMPLATE_HEAD, ",");
        headerList.add(head);
        String  fileName = BusConstant.buidFileName(request, CarConstant.FILE_NAME);
        List<String> csvDataList = new ArrayList<>();
        CsvUtils utilEntity = new CsvUtils();
        long total = pageInfo.getTotal();
        if (total == 0) {
            csvDataList.add("???????????????????????????");
            utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
            return;
        }
        buidCSVdata(pageInfo.getList(), csvDataList);
        //?????????
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
            //        String[] TEMPLATE_HEAD={"??????","?????????","?????????","??????????????????","????????????","????????????","???????????????","????????????","????????????????????????","??????????????????????????????","??????????????????????????????","???????????????????????????????????????","????????????????????????"};

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
     * ???????????????????????????
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
                    //??????
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date val = cell.getDateCellValue();
                    value = sdf.format(val);
                } else if (format == 20 || format == 32) {
                    //??????
                    sdf = new SimpleDateFormat("HH:mm");
                    Date val = cell.getDateCellValue();
                    value = sdf.format(val);
                } else { // ????????? ?????????????????????
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
            // ???????????????
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
