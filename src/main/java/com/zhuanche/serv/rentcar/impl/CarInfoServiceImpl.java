package com.zhuanche.serv.rentcar.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.driver.DriverBrand;
import com.zhuanche.entity.driver.DriverVehicle;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.authc.UserManagementService;
import com.zhuanche.serv.financial.DriverBrandService;
import com.zhuanche.serv.financial.DriverVehicleService;
import com.zhuanche.serv.rentcar.CarInfoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import mapper.rentcar.CarSysDictionaryMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizModelExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import mapper.rentcar.ex.CarInfoExMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarInfoServiceImpl implements CarInfoService {
    private static Logger log = LoggerFactory.getLogger(CarInfoServiceImpl.class);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static String REGULAR_NUMBER = "[0-9]*";
    private static String REGULAR_FRAME = "(^[a-zA-Z0-9\\-]{13}$)|(^[a-zA-Z0-9\\-]{17}$)";


    @Autowired
    private CarInfoExMapper carInfoExMapper;

//    @Autowired
//    @Qualifier("carApiTemplate")
//    private MyRestTemplate carApiTemplate;

    @Value("${mp.restapi.url}")
    private String mpReatApiUrl;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    @Autowired
    private CarBizCityExMapper cityDao;

    @Autowired
    private CarBizModelExMapper carBizModelExMapper;
        //car_sys_dictionary

    @Autowired
    private CarSysDictionaryMapper carSysDictionaryMapper;

    @Autowired
    private UserManagementService userManagementService;


    @Autowired
    private DriverVehicleService driverVehicleService;

    @Autowired
    private DriverBrandService driverBrandService;


    @Override
    public List<CarInfoVo> selectcarnum(Map<String, Object> map) {
        return carInfoExMapper.selectcarnum(map);
    }

    @Override
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public PageInfo<CarInfo> findPageByCarInfo(CarInfo params,int pageNo,int pageSize){
        PageHelper.startPage(pageNo, pageSize, true);
        List<CarInfo> list =   carInfoExMapper.selectList(params);
        PageInfo<CarInfo> pageInfo = new PageInfo<>(list);

        if(pageInfo.getList() != null){
            for(CarInfo entity :pageInfo.getList()){
                packageEntity(entity);
            }
        }
        return pageInfo;
    }

    public void packageEntity(CarInfo entity){
        if(entity.getCarModelId() != null){
            DriverVehicle driverVehicle = driverVehicleService.queryByModelId(entity.getCarModelId());
            if(driverVehicle != null){
                Long brandId =   driverVehicle.getBrandId();
                entity.setNewBrandId(brandId);
                if(brandId != null){
                    DriverBrand driverBrand = driverBrandService.getDriverBrandByPrimaryKey(brandId);
                    if(driverBrand != null){
                        entity.setNewBrandName(driverBrand.getBrandName());
                    }
                }
            }
        }
    }



    @Override
    public CarInfo selectCarInfoByLicensePlates(
            CarInfo carInfoEntity) {
        CarInfo params = carInfoExMapper
                .selectCarInfoByLicensePlates(carInfoEntity);
        // ??????
        int inspectFlag = 0;
        int MaintenanceFlag = 0;
        Calendar now = Calendar.getInstance();
        // long monthNow= now.getTimeInMillis()/(1000*60*60*24*30);
        long dayNow = now.getTimeInMillis() / (1000 * 60 * 60 * 24);

        long monthNow = dayNow / 30;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// ?????????mm??????????????????
        try {
            Date purchaseDate = sdf.parse(params.getPurchaseDate());
            now.setTime(purchaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long purchaseMonth = now.getTimeInMillis() / (1000 * 60 * 60 * 24);
        purchaseMonth = purchaseMonth / 30;
        long ageYear = (monthNow - purchaseMonth) / 12;
        long ageMonth = (monthNow - purchaseMonth) % 12;
        String age = ageYear + "???" + ageMonth + "???";
        params.setCarAge(age);
        // ????????????????????????
        try {
            Date nextInspectDate = sdf.parse(params.getNextInspectDate());
            now.setTime(nextInspectDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long nextInspectDay = now.getTimeInMillis() / (1000 * 60 * 60 * 24);
        long InspectDay = nextInspectDay - dayNow + 1;
        // ????????????????????????
        try {
            Date nextMaintenanceDate = sdf.parse(params
                    .getNextMaintenanceDate());
            now.setTime(nextMaintenanceDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long nextMaintenanceDay = now.getTimeInMillis() / (1000 * 60 * 60 * 24);
        long MaintenanceDay = nextMaintenanceDay - dayNow + 1;
        // ??????????????????
        if (InspectDay <= 30 && InspectDay >= 0) {
            if (InspectDay == 0) {
                params.setTimeOfnextInspect("?????????");
            } else {
                params.setTimeOfnextInspect("??????" + InspectDay + "???");
            }
            inspectFlag = 1;
        }
        if (MaintenanceDay <= 30 && MaintenanceDay >= 0) {
            if (MaintenanceDay == 0) {
                params.setTimeOfnextMaintenance("?????????");
            } else {
                params.setTimeOfnextMaintenance("??????" + MaintenanceDay + "???");
            }
            MaintenanceFlag = 1;
        }
        params.setInspectFlag(inspectFlag);
        params.setMaintenanceFlag(MaintenanceFlag);
        return params;
    }

    @Override
    public AjaxResponse importCarInfo(MultipartFile fileName, HttpServletRequest request) {
        String resultError1 = "-1";// ????????????
        String resultErrorMag1 = "????????????????????????!";

        String licensePlates = "";
//        Map<String, Object> result = new HashMap<String, Object>();
        List<CarImportExceptionEntity> listException = new ArrayList<CarImportExceptionEntity>();
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        List<CarInfo> carList = new ArrayList<CarInfo>();
//        String path  = Common.getPath(request);
//        String dirPath = path + fileName;
//        File DRIVERINFO = new File(dirPath);
        try {
//            InputStream is = new FileInputStream(DRIVERINFO);
            Workbook workbook = null;
            String name = fileName.getOriginalFilename();
            String fileType = fileName.getOriginalFilename().split("\\.")[1];
            if (fileType.equals("xls")) {
                workbook = new HSSFWorkbook(fileName.getInputStream());
            } else if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(fileName.getInputStream());
            }
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            // ????????????????????????
            Row row1 = sheet.getRow(0);
            if(row1==null){
                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, resultErrorMag1);
            }
            for (int colIx = 0; colIx < 43; colIx++) {
                Cell cell = row1.getCell(colIx); // ???????????????
                CellValue cellValue = evaluator.evaluate(cell); // ???????????????
                if (cell == null || cellValue == null) {
                    return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, resultErrorMag1);
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("??????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "1?????????????????????");
                            }
                            break;
                        case 2:
                            if (!cellValue.getStringValue().contains("?????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "2????????????????????????");
                            }
                            break;
                        case 3:
                            if (!cellValue.getStringValue().contains("??????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "3?????????????????????");
                            }
                            break;
                        case 4:
                            if (!cellValue.getStringValue().contains("??????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "4?????????????????????");
                            }
                            break;
                        case 5:
                            if (!cellValue.getStringValue().contains("?????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "5????????????????????????");
                            }
                            break;
                        case 6:
                            if (!cellValue.getStringValue().contains("??????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "6?????????????????????");
                            }
                            break;
                        case 7:
                            if (!cellValue.getStringValue().contains("????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "7???????????????????????????");
                            }
                            break;
                        case 8:
                            if (!cellValue.getStringValue().contains("????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "8:????????????????????????");
                            }
                            break;
                        case 9:
                            if (!cellValue.getStringValue().contains("??????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "9?????????????????????");
                            }
                            break;
                        case 10:
                            if (!cellValue.getStringValue().contains("????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "10???????????????????????????");
                            }
                            break;
                        case 11:
                            if (!cellValue.getStringValue().contains("?????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "11????????????????????????");
                            }
                            break;
                        case 12:
                            if (!cellValue.getStringValue().contains("??????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "12?????????????????????????????????");
                            }
                            break;
                        case 13:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "13???????????????????????????????????????");
                            }
                            break;
                        case 14:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "14???????????????????????????????????????");
                            }
                            break;
                        case 15:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "15???????????????????????????????????????");
                            }
                            break;
                        case 16:
                            if (!cellValue.getStringValue().contains("??????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "16?????????????????????????????????");
                            }
                            break;
                        case 17:
                            if (!cellValue.getStringValue().contains("??????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "17?????????????????????????????????");
                            }
                            break;
                        case 18:
                            if (!cellValue.getStringValue().contains("??????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "18?????????????????????????????????");
                            }
                            break;
                        case 19:
                            if (!cellValue.getStringValue().contains("???????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "19??????????????????????????????");
                            }
                            break;
                        case 20:
                            if (!cellValue.getStringValue().contains("????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "20???????????????????????????");
                            }
                            break;
                        case 21:
                            if (!cellValue.getStringValue().contains("????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "21???????????????????????????");
                            }
                            break;
                        case 22:
                            if (!cellValue.getStringValue().contains("??????VIN???")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "22?????????VIN???????????????");
                            }
                            break;
                        case 23:
                            if (!cellValue.getStringValue().contains("??????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "23?????????????????????????????????");
                            }
                            break;
                        case 24:
                            if (!cellValue.getStringValue().contains("??????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "24?????????????????????????????????");
                            }
                            break;
                        case 25:
                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "25??????????????????????????????????????????");
                            }
                            break;
                        case 26:
                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "26??????????????????????????????????????????");
                            }
                            break;
                        case 27:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "27???????????????????????????????????????");
                            }
                            break;
                        case 28:
                            if (!cellValue.getStringValue().contains("???????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "28??????????????????????????????");
                            }
                            break;
                        case 29:
                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "29??????????????????????????????????????????");
                            }
                            break;
                        case 30:
                            if (!cellValue.getStringValue().contains("??????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "30?????????????????????????????????");
                            }
                            break;
                        case 31:
                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "31??????????????????????????????????????????");
                            }
                            break;
                        case 32:
                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "32??????????????????????????????????????????");
                            }
                            break;
                        case 33:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "33???????????????????????????????????????");
                            }
                            break;
                        case 34:
                            if (!cellValue.getStringValue().contains("??????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "34?????????????????????????????????");
                            }
                            break;
                        case 35:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "35???????????????????????????????????????");
                            }
                            break;
                        case 36:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "36???????????????????????????????????????");
                            }
                            break;
                        case 37:
                            if (!cellValue.getStringValue().contains("???????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "37??????????????????????????????????????????");
                            }
                            break;
                        case 38:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "38???????????????????????????????????????");
                            }
                            break;
                        case 39:
                            if (!cellValue.getStringValue().contains("????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "39???????????????????????????????????????");
                            }
                            break;
                        case 40:
                            if (!cellValue.getStringValue().contains("??????????????????IMEI???")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "40?????????????????????IMEI???????????????");
                            }
                            break;
                        case 41:
                            if (!cellValue.getStringValue().contains("??????????????????????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "41?????????????????????????????????????????????");
                            }
                            break;
                        case 42:
                            if (!cellValue.getStringValue().contains("????????????")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "42???????????????????????????");
                            }
                            break;
                        case 43:
                            if (!cellValue.getStringValue().contains("????????????(???????????????????????????)")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "43??????????????????????????????????????????");
                            }
                            break;
                    }
                }
            }

            int minRowIx = 1;// ????????????????????????????????????????????????
            int maxRowIx = sheet.getLastRowNum(); // ???????????????????????????
             int successCount = 0;// ??????????????????

            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // ???????????????
                if (row == null) {
                    continue;
                }
                CarInfo carBizCarInfo = new CarInfo();
                boolean isTrue = true;// ???????????????????????????
                // String cityName = "";
                // ????????????????????????
//				carBizCarInfo.setStatus(1);
                carBizCarInfo.setCreateBy(currentLoginUser.getId());
                carBizCarInfo.setUpdateBy(currentLoginUser.getId());
                Integer cityId = null;
                // ????????????????????????41 ???
                for (int colIx = 0; colIx < 43; colIx++) {
                    Cell cell = row.getCell(colIx); // ???????????????
                    CellValue cellValue = evaluator.evaluate(cell); // ???????????????
                    if((colIx + 1)==2){
                        if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                        }else{
                            licensePlates = cellValue.getStringValue();
                        }
                    }
                    switch ((colIx + 1)) {
                        // ?????????
                        case 1:
                            //log.info("?????????"+cellValue.getStringValue());
                            break;
                        // ?????????
                        case 2:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String license = cellValue.getStringValue();
                                license = Common.replaceBlank(license);
                                // TODO ??????????????? ????????????
                                if (license.length() == 7||license.length() == 8) {
                                    CarSysDictionary orderDictionaryEntity1 = carSysDictionaryMapper.selectByPrimaryKey(17);
                                    String strValue = orderDictionaryEntity1.getDicValue();
                                    Integer intValue = Integer.valueOf(strValue.replace(".00", ""));
                                    log.info("???????????????????????????--17--?????????????????????????????????????????????--1 ?????? 2?????????==?????????=="+license);
                                    if(intValue!=null&&intValue.equals(2)){
                                        int n = carInfoExMapper.checkLicensePlates(license);
                                        if (n > 0) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setLicensePlates(licensePlates);
                                            returnVO.setReson("???" + (rowIx + 1)
                                                    + "???????????????" + (colIx + 1)
                                                    + "??? ????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            carBizCarInfo.setLicensePlates(license);
                                            carBizCarInfo.setLicensePlates1(license);
                                        }
                                    }else{
                                        carBizCarInfo.setLicensePlates(license);
                                        carBizCarInfo.setLicensePlates1(license);
                                    }
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ???????????????????????? ?????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ??????
                        case 3:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // ???????????? ????????????
                                cityId = cityDao.queryCityByCityName(cellValue.getStringValue());
                                if (cityId == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1)
                                            + "???????????????" + (colIx + 1)
                                            + "??? ??????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    carBizCarInfo.setCityId(cityId);
                                }
                            }
                            break;
                        // ??????
                        case 4:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // ????????????
                                if ("??????".equals(cellValue.getStringValue())||"??????".equals(cellValue.getStringValue())){
                                    if ("??????".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setStatus(1);
                                    } else {
                                        carBizCarInfo.setStatus(0);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ?????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ?????????
                        case 5:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // ??????????????? ????????????
                                Integer supplierId = carBizSupplierExMapper.querySupplierBySupplierName(cellValue.getStringValue());
                                if (supplierId == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1)
                                            + "???????????????" + (colIx + 1)
                                            + "??? ?????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    if (cityId == null) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???" + (rowIx + 1)
                                                + "???????????????" + (colIx + 1)
                                                + "??? ??????????????????");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        Set<Integer> cityIds = Sets.newHashSet();
                                        cityIds.add(cityId);
                                        List<CarBizSupplier> supplierList = carBizSupplierExMapper.querySuppliers(cityIds, null);
                                        Boolean fal = false;
                                        if(supplierList!=null&&supplierList.size()>0){
                                            for(int i=0;i<supplierList.size();i++){
                                                if(!fal){
                                                    if(supplierId.equals(supplierList.get(i).getSupplierId())){
                                                        fal = true;
                                                    }
                                                }
                                            }
                                        }
                                        if(fal){
                                            carBizCarInfo.setSupplierId(supplierId);
                                        }else{
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setLicensePlates(licensePlates);
                                            returnVO.setReson("???" + (rowIx + 1)
                                                    + "???????????????" + (colIx + 1)
                                                    + "??? ??????????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        }
                                    }
                                }
                            }
                            break;
                        // ??????
                        case 6:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                Integer carModelId = carBizModelExMapper.queryCarModelByCarModelName(cellValue.getStringValue());
                                if (carModelId == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1)
                                            + "???????????????" + (colIx + 1)
                                            + "??? ??????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    carBizCarInfo.setCarModelId(carModelId);
                                }
                            }
                            break;
                        // ????????????
                        case 7:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String modelDetail = cellValue.getStringValue();
                                modelDetail = Common.replaceBlank(modelDetail);
                                carBizCarInfo.setModelDetail(modelDetail);
                            }
                            break;
                        // ????????????
                        case 8:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() > sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ??????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setPurchaseDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ???????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ???????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ??????
                        case 9:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(cellValue.getStringValue().matches("[\u4e00-\u9fa5]*")){
                                    carBizCarInfo.setColor(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ???????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ????????????
                        case 10:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
//							if(isRegular(cellValue.getStringValue(),REGULAR_ENGINE)){
                                String engineNo = cellValue.getStringValue();
                                engineNo = Common.replaceBlank(engineNo);
                                carBizCarInfo.setEngineNo(engineNo);
//							}else{
//								CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//								returnVO.setLicensePlates(licensePlates);
//								returnVO.setReson("???" + (rowIx + 1) + "???????????????"
//										+ (colIx + 1) + "??? ?????????????????????8?????????????????????");
//								listException.add(returnVO);
//								isTrue = false;
//							}
                            }
                            break;
                        // ?????????
                        case 11:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isRegular(cellValue.getStringValue(),REGULAR_FRAME)){
                                    carBizCarInfo.setFrameNo(cellValue.getStringValue());
                                    carBizCarInfo.setVehicleVINCode(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ??????????????????13???17?????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ??????????????????
                        case 12:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextMaintenanceDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ????????????????????????
                        case 13:
//						if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
//							CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//							returnVO.setLicensePlates(licensePlates);
//							returnVO.setReson("???" + (rowIx + 1) + "???????????????"
//									+ (colIx + 1)
//									+ "??? ???????????????????????????????????????????????????????????????????????????");
//							listException.add(returnVO);
//							isTrue = false;
//						} else {
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ??????????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextClassDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ???????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ????????????????????????????????????????????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ???????????????????????????
                        case 14:
//						if (cellValue == null
//								|| StringUtils.isEmpty(cellValue
//										.getStringValue())) {
//							CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//							returnVO.setLicensePlates(licensePlates);
//							returnVO.setReson("???" + (rowIx + 1) + "???????????????"
//									+ (colIx + 1)
//									+ "??? ??????????????????????????????????????????????????????????????????????????????");
//							listException.add(returnVO);
//							isTrue = false;
//						} else {
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ?????????????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextOperationDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ???????????????????????????????????????????????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ???????????????????????????
                        case 15:
//						if (cellValue == null
//								|| StringUtils.isEmpty(cellValue
//										.getStringValue())) {
//							CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//							returnVO.setLicensePlates(licensePlates);
//							returnVO.setReson("???" + (rowIx + 1) + "???????????????"
//									+ (colIx + 1)
//									+ "??? ??????????????????????????????????????????????????????????????????????????????");
//							listException.add(returnVO);
//							isTrue = false;
//						} else {
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ?????????????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextSecurityDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ???????????????????????????????????????????????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ??????????????????
                        case 16:
//						if (cellValue == null
//								|| StringUtils.isEmpty(cellValue
//										.getStringValue())) {
//							CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//							returnVO.setLicensePlates(licensePlates);
//							returnVO.setReson("???" + (rowIx + 1) + "???????????????"
//									+ (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????");
//							listException.add(returnVO);
//							isTrue = false;
//						} else {
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setTwoLevelMaintenanceDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ??????????????????????????????????????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ??????????????????
                        case 17:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setRentalExpireDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ??????????????????
                        case 18:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextInspectDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // ???????????????
                        case 19:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ??????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isRegular(cellValue.getStringValue(),REGULAR_NUMBER)){
                                    carBizCarInfo.setCarryPassengers(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ????????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //????????????
                        case 20:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String vehicleBrand = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setVehicleBrand(vehicleBrand);
                            }
                            break;
                        //????????????
                        case 21:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(cellValue.getStringValue().matches("[\u4e00-\u9fa5]*")){
                                    carBizCarInfo.setClicensePlatesColor(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ?????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }

                            }
                            break;
                        //??????VIN???
                        case 22:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????VIN???????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isRegular(cellValue.getStringValue(),REGULAR_FRAME)){
                                    //carBizCarInfo.setVehicleVINCode(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ?????????VIN???????????????????????????13???17?????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //??????????????????
                        case 23:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() > sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("???" +  (rowIx+1) + "???????????????"
                                                    + (colIx + 1) + "??? ????????????????????????????????????????????????");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setVehicleRegistrationDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ?????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //??????????????????
                        case 24:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String fuelType = Common.replaceBlank(cellValue.getStringValue());
                                // ??????????????????
                                if ("??????".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(1);
                                }else if ("??????".equals(fuelType)){
                                    carBizCarInfo.setFuelType(2);
                                }else if ("???".equals(fuelType)){
                                    carBizCarInfo.setFuelType(3);
                                }else if ("?????????".equals(fuelType)){
                                    carBizCarInfo.setFuelType(4);
                                }else if ("?????????".equals(fuelType)){
                                    carBizCarInfo.setFuelType(5);
                                }else if ("???????????????".equals(fuelType)){
                                    carBizCarInfo.setFuelType(6);
                                }else if ("??????".equals(fuelType)){
                                    carBizCarInfo.setFuelType(7);
                                }else if ("??????".equals(fuelType)){
                                    carBizCarInfo.setFuelType(8);
                                }else if ("?????????".equals(fuelType)){
                                    carBizCarInfo.setFuelType(9);
                                }else if ("????????????".equals(fuelType)){
                                    carBizCarInfo.setFuelType(10);
                                }else if("???".equals(fuelType)){
                                    carBizCarInfo.setFuelType(11);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ?????????????????????????????????|??????|???|?????????|?????????|???????????????|??????|??????|?????????|????????????|???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //?????????????????????
                        case 25:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isNumeric(cellValue.getStringValue())){
                                    if(Integer.valueOf(cellValue.getStringValue())>=700&&Integer.valueOf(cellValue.getStringValue())<=5000){
                                        carBizCarInfo.setVehicleEngineDisplacement(cellValue.getStringValue());
                                    }else{
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                                + (colIx + 1) + "??? ????????????????????????????????????????????????700???5000??????");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ??????????????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //???????????????
                        case 26:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ??????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isNumeric(cellValue.getStringValue())){
                                    if(Integer.valueOf(cellValue.getStringValue())>=50&&Integer.valueOf(cellValue.getStringValue())<=600){
                                        carBizCarInfo.setVehicleEnginePower(cellValue.getStringValue());
                                    }else{
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                                + (colIx + 1) + "??? ??????????????????????????????????????????50???600??????");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ????????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //????????????
                        case 27:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isNumeric(cellValue.getStringValue())){
                                    if(Integer.valueOf(cellValue.getStringValue())>=2000&&Integer.valueOf(cellValue.getStringValue())<=6000){
                                        carBizCarInfo.setVehicleEngineWheelbase(cellValue.getStringValue());
                                    }else{
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                                + (colIx + 1) + "??? ???????????????????????????????????????2000???6000??????");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ?????????????????????????????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //???????????????
                        case 28:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ??????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(cellValue.getStringValue().matches("[\u4e00-\u9fa5]*?????????[\u4e00-\u9fa5]{1,2}???\\d{4,6}\\s\\d{4,6}???$")){
                                    carBizCarInfo.setTransportNumber(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ?????????????????????????????????????????????????????????123456 123456???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //???????????????????????????
                        case 29:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ??????????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String certificationAuthority = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setCertificationAuthority(certificationAuthority);
                            }
                            break;
                        //??????????????????
                        case 30:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String operatingRegion = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setOperatingRegion(operatingRegion);
                            }
                            break;
                        //???????????????????????????
                        case 31:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ??????????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizCarInfo.setTransportNumberDateStart(d);
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //???????????????????????????
                        case 32:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ??????????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizCarInfo.setTransportNumberDateEnd(d);
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ??????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //????????????????????????
                        case 33:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizCarInfo.setFirstDate(d);
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ???????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ???????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //??????????????????
                        case 34:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if ("?????????".equals(cellValue.getStringValue())||"?????????".equals(cellValue.getStringValue())||"??????".equals(cellValue.getStringValue())){
                                    // ??????????????????
                                    if ("?????????".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setOverHaulStatus(1);
                                    } else if ("??????".equals(cellValue.getStringValue())){
                                        carBizCarInfo.setOverHaulStatus(2);
                                    }else{
                                        carBizCarInfo.setOverHaulStatus(0);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ????????????????????????????????????|?????????|??????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //????????????????????????
                        case 35:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if ("?????????".equals(cellValue.getStringValue())||"??????".equals(cellValue.getStringValue())||"?????????".equals(cellValue.getStringValue())){
                                    // ????????????????????????
                                    if ("??????".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setAuditingStatus(1);
                                    } else if ("?????????".equals(cellValue.getStringValue())){
                                        carBizCarInfo.setAuditingStatus(2);
                                    }else{
                                        carBizCarInfo.setAuditingStatus(0);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                            + (colIx + 1) + "??? ??????????????????????????????????????????|??????|?????????");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //????????????????????????
                        case 36:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizCarInfo.setAuditingDate(d);
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ???????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ???????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //???????????????????????????
                        case 37:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ??????????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String equipmentNumber = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setEquipmentNumber(equipmentNumber);
                            }
                            break;
                        //????????????????????????
                        case 38:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String gpsBrand = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setGpsBrand(gpsBrand);
                            }
                            break;
                        //????????????????????????
                        case 39:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String gpsType = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setGpsType(gpsType);
                            }
                            break;
                        //??????????????????IMEI???
                        case 40:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????IMEI???????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String gpsImei = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setGpsImei(gpsImei);
                            }
                            break;
                        //??????????????????????????????
                        case 41:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ?????????????????????????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("???", "-").replace("???", "-")
                                        .replace("???", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        carBizCarInfo.setGpsDate(d);
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("???"
                                                + (rowIx + 1)
                                                + "???????????????"
                                                + (colIx + 1)
                                                + "??? ?????????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???"
                                            + (rowIx + 1)
                                            + "???????????????"
                                            + (colIx + 1)
                                            + "??? ?????????????????????????????????????????????????????????????????????xxxx-xx-xx ??? xxxx???xx???xx???");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //????????????
                        case 42:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String vehicleOwner = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setVehicleOwner(vehicleOwner);
                            }
                            break;
                        //????????????(???????????????????????????)
                        case 43:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"
                                        + (colIx + 1) + "??? ???????????????(???????????????????????????)????????????????????????????????????????????????");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String vehicleType = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setVehicleType(vehicleType);
                            }
                            break;
                    }// switch end

                }// ???????????????
                if (isTrue && carBizCarInfo != null) {
                    carList.add(carBizCarInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String download = "";
        boolean flag = false;
        if ("".equals(carList) || carList == null || carList.size() == 0) {
//            result.put("result", "0");
//            result.put("msg", "???????????????");
        } else {
            List<CarInfo> carList2 = new ArrayList<CarInfo>();
            String licensePlatess = "";
            for(int i=0;i<carList.size();i++){
                CarInfo vo = carList.get(i);
                String yulicensePlates = vo.getLicensePlates();
                if(i==0){
                    licensePlatess = yulicensePlates;
                    carList2.add(vo);
                }else{
                    if(licensePlatess.contains(yulicensePlates)){
                        log.info("??????????????????????????????????????????????????????licensePlates="+yulicensePlates);
                    }else{
                        licensePlatess += yulicensePlates;
                        carList2.add(vo);
                    }
                }
            }
            // ??????????????????
            Map<String, Object> paramMap = new HashMap<String, Object>();
            JSONArray jsonarray = JSONArray.fromObject(carList2);
            String cars = jsonarray.toString();
            paramMap.put("carList", cars);
//            String url = "/webservice/carManage/batchInputCarInfo";
//            JSONObject jsonobject = carApiTemplate.postForObject(url,
//                    JSONObject.class, paramMap);
            String url = mpReatApiUrl + Common.BATHINPUTCARINFO;
            String jsonObjectStr = null;
            try {
                jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
            } catch (HttpException e) {
                log.info("???????????????????????? paramMap={},error={}", paramMap, e);
            }
            log.info("?????????????????????????????? paramMap={}, result={}", paramMap, jsonObjectStr);
            com.alibaba.fastjson.JSONObject jsonobject = JSON.parseObject(jsonObjectStr);

            // ?????????0 ==========?????????
            if ((int) jsonobject.get("result") == 0) {
                log.info("???????????????0 ==========???????????????");
                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                returnVO.setReson((String) jsonobject.get("exception"));
                listException.add(returnVO);
//                result.put("result", "0");
//                result.put("msg", "???????????????");
            } else {
                // ?????????1 ==========??????
                JSONObject json = (JSONObject) jsonobject.get("jsonStr");
                // String driverIds = (String)json.get("driverIds");
                String arrayDriverName = (String) json.get("error");
                if (!"".equals(arrayDriverName) && arrayDriverName != null) {
                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                    returnVO.setLicensePlates(licensePlates);
                    returnVO.setReson(arrayDriverName + "???????????????");
                    listException.add(returnVO);
                }
               flag = true;
            }
        }

        if(flag){
            return AjaxResponse.success(listException);
        } else {
            StringBuilder errorMsg = new StringBuilder();
            if (listException.size() > 0) {

                for (CarImportExceptionEntity entity : listException) {
                    errorMsg.append(entity.getReson()).append(";");
                }
            }
            return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, errorMsg);
        }
    }

    private boolean isValidDate(String str) {
        boolean convertSuccess = true;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public Workbook exportExcel(String path,
                                List<CarImportExceptionEntity> exportList) throws Exception {
        FileInputStream io = new FileInputStream(path);
        // ?????? excel
        Workbook wb = new XSSFWorkbook(io);
        List<CarImportExceptionEntity> list = exportList;
        if (list != null && list.size() > 0) {
            Sheet sheet = wb.getSheetAt(0);
            Cell cell = null;
            int i = 0;
            for (CarImportExceptionEntity s : list) {
                Row row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(s.getReson());
                i++;
            }
        }
        return wb;
    }

    /**
     * ????????????Excel???????????????????????????
     *
     * @param request
     * @param wb
     * @param fileName
     * @return
     * @throws Exception
     */
    public String exportExcelFromTempletToLoacl(HttpServletRequest request,
                                                Workbook wb, String fileName) throws Exception {
        if (StringUtils.isEmpty(fileName)) {
            fileName = "exportExcel";
        }
        // ????????????????????????
        String webPath = request.getServletContext().getRealPath("/");

        // ?????????????????????????????????
        // ????????????????????????
        String fileURI = "/template/error/" + fileName + buildRandom(2) + "_"
                + System.currentTimeMillis() + ".xlsx";

        FileOutputStream fos = new FileOutputStream(webPath + fileURI);
        wb.write(fos);
        fos.close();
        return fileURI;
    }

    public int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

    @Override
    public boolean checkLicensePlates(CarInfo params) {
        Map<String, Object> result = new HashMap<String, Object>();
        int n = carInfoExMapper.checkLicensePlates(params.getLicensePlates());
        if (n > 0) {
            return false;
        }else{
            return true;
        }
    }

    @Override
    public Map<String, Object> saveCarInfo(CarInfo params) {
        Map<String,Object> result = new HashMap<String,Object>();
        //??????????????????
        Map<String,Object> paramMap = new HashMap<String,Object>();
        JSONObject json = JSONObject.fromObject(params);
        paramMap.put("carInfo", json);
//        String url = "/webservice/carManage/saveCar";
//        result = carApiTemplate.postForObject(url,JSONObject.class,paramMap);
        String url = mpReatApiUrl + Common.SAVE_CAR;
        String jsonObjectStr = null;
        try {
            jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
        } catch (HttpException e) {
            log.info("???????????????????????? paramMap={},error={}", paramMap, e);
        }
        log.info("?????????????????????????????? paramMap={}, result={}", paramMap, jsonObjectStr);
        com.alibaba.fastjson.JSONObject jsonobject = JSON.parseObject(jsonObjectStr);
        result = jsonobject;
        return result;
    }

//    @Override
//    public Workbook exportExcel(CarInfo params, String path)
//            throws Exception {
//        FileInputStream io = new FileInputStream(path);
//        Workbook wb = new XSSFWorkbook(io);
//        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
//        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(), ",");
//        String teamIds = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(), ",");
//        String carModelIds = "";
//        if(!"".equals(params.getCities())&&params.getCities()!=null&&!"null".equals(params.getCities())){
//            cities = params.getCities().replace(";", ",");
//        }
//        if(!"".equals(params.getSupplierIds())&&params.getSupplierIds()!=null&&!"null".equals(params.getSupplierIds())){
//            suppliers = params.getSupplierIds().replace(";", ",");
//        }
//        if(!"".equals(params.getCarModelIds())&&params.getCarModelIds()!=null&&!"null".equals(params.getCarModelIds())){
//            carModelIds = params.getCarModelIds().replace(";", ",");
//        }
//        params.setCarModelIds(carModelIds);
//        params.setSupplierIds(suppliers);
//        params.setCities(cities);
//        params.setTeamIds(teamIds);
//        List<CarInfo> list = carInfoExMapper.selectListNoPage(params);
//
//        if(list != null && list.size()>0){
//            Sheet sheet = wb.getSheetAt(0);
//            Cell cell;
//            int i=0;
//            List<Integer> createIds = new ArrayList<>();
//            List<Integer> updateIds = new ArrayList<>();
//            for(CarInfo s:list){
//                if(s.getCreateBy()!=null && StringUtils.isBlank(s.getCreateName())){
//                    createIds.add(s.getCreateBy());
//                }
//                if(s.getUpdateBy()!=null && StringUtils.isBlank(s.getUpdateName())){
//                    updateIds.add(s.getUpdateBy());
//                }
//            }
//            List<CarAdmUser>  creator = userManagementService.getUsersByIdList(createIds);
//            List<CarAdmUser>  updator = userManagementService.getUsersByIdList(updateIds);
//            for (CarInfo s : list){
//                if (s.getCreateBy()!=null && StringUtils.isBlank(s.getCreateName())){
//                    for (CarAdmUser user : creator){
//                        if ((s.getCreateBy()).equals(user.getUserId())){
//                            s.setCreateName(user.getUserName());
//                            break;
//                        }
//                    }
//                }
//                if (s.getUpdateBy()!=null && StringUtils.isBlank(s.getUpdateName())){
//                    for (CarAdmUser user : updator){
//                        if ((s.getUpdateBy()).equals(user.getUserId())){
//                            s.setUpdateName(user.getUserName());
//                            break;
//                        }
//                    }
//                }
//                Row row = sheet.createRow(i + 1);
//                cell = row.createCell(0);
//                cell.setCellValue(s.getLicensePlates());
//
//                cell = row.createCell(1);
//                cell.setCellValue(s.getCityName());
//
//                cell = row.createCell(2);
//                cell.setCellValue(s.getStatus()==1?"??????":"??????");
//
//                cell = row.createCell(3);
//                cell.setCellValue(s.getSupplierName());
//
//                cell = row.createCell(4);
//                cell.setCellValue(s.getModeName());
//
//                cell = row.createCell(5);
//                cell.setCellValue(s.getModelDetail());
//
//                cell = row.createCell(6);
//                cell.setCellValue(s.getPurchaseDate());
//
//                cell = row.createCell(7);
//                cell.setCellValue(s.getColor());
//
//                cell = row.createCell(8);
//                cell.setCellValue(s.getEngineNo());
//
//                cell = row.createCell(9);
//                cell.setCellValue(s.getFrameNo());
//
//                cell = row.createCell(10);
//                cell.setCellValue(s.getNextInspectDate());
//
//                cell = row.createCell(11);
//                cell.setCellValue(s.getNextMaintenanceDate());
//
//                cell = row.createCell(12);
//                cell.setCellValue(s.getRentalExpireDate());
//
//                cell = row.createCell(13);
//                cell.setCellValue(s.getNextClassDate());
//
//                cell = row.createCell(14);
//                cell.setCellValue(s.getNextOperationDate());
//
//                cell = row.createCell(15);
//                cell.setCellValue(s.getNextSecurityDate());
//
//                cell = row.createCell(16);
//                cell.setCellValue(s.getTwoLevelMaintenanceDate());
//
//                cell = row.createCell(17);
//                cell.setCellValue(s.getCarryPassengers());
//
//                cell = row.createCell(18);
//                cell.setCellValue(s.getVehicleBrand());
//
//                cell = row.createCell(19);
//                cell.setCellValue(s.getClicensePlatesColor());
//
//                cell = row.createCell(20);
//                cell.setCellValue(s.getVehicleVINCode());
//
//                cell = row.createCell(21);
//                cell.setCellValue(s.getVehicleRegistrationDate());
//
//                cell = row.createCell(22);
//                Integer fuelType = s.getOverHaulStatus();
//                String  fuelTypeStr = "???";
//                if(null!=fuelType && fuelType==1){
//                    fuelTypeStr = "??????";
//                }else if(null!=fuelType && fuelType==2){
//                    fuelTypeStr = "??????";
//                }else if(null!=fuelType && fuelType==3){
//                    fuelTypeStr = "???";
//                }else if(null!=fuelType && fuelType==4){
//                    fuelTypeStr = "?????????";
//                }else if(null!=fuelType && fuelType==5){
//                    fuelTypeStr = "?????????";
//                }else if(null!=fuelType && fuelType==6){
//                    fuelTypeStr = "???????????????";
//                }else if(null!=fuelType && fuelType==7){
//                    fuelTypeStr = "??????";
//                }else if(null!=fuelType && fuelType==8){
//                    fuelTypeStr = "??????";
//                }else if(null!=fuelType && fuelType==9){
//                    fuelTypeStr = "?????????";
//                }else if(null!=fuelType && fuelType==10){
//                    fuelTypeStr = "????????????";
//                }
//                cell.setCellValue(fuelTypeStr);
//
//                cell = row.createCell(23);
//                cell.setCellValue(s.getVehicleEngineDisplacement());
//
//                cell = row.createCell(24);
//                cell.setCellValue(s.getVehicleEnginePower());
//
//                cell = row.createCell(25);
//                cell.setCellValue(s.getVehicleEngineWheelbase());
//
//                cell = row.createCell(26);
//                cell.setCellValue(s.getTransportNumber());
//
//                cell = row.createCell(27);
//                cell.setCellValue(s.getCertificationAuthority());
//
//                cell = row.createCell(28);
//                cell.setCellValue(s.getOperatingRegion());
//
//                cell = row.createCell(29);
//                cell.setCellValue(s.getTransportNumberDateStart());
//
//                cell = row.createCell(30);
//                cell.setCellValue(s.getTransportNumberDateEnd());
//
//                cell = row.createCell(31);
//                cell.setCellValue(s.getFirstDate());
//
//                cell = row.createCell(32);
//                Integer overHaulStatus = s.getOverHaulStatus();
//                String  statusStr = "??????";
//                if(null!=overHaulStatus && overHaulStatus==1){
//                    statusStr = "?????????";
//                }else if(null!=overHaulStatus && overHaulStatus==0){
//                    statusStr = "?????????";
//                }
//                cell.setCellValue(statusStr);
//                cell = row.createCell(33);
//                Integer auditingStatus = s.getAuditingStatus();
//                String  auditingStatusStr = "?????????";
//                if(null!=auditingStatus && auditingStatus==1){
//                    auditingStatusStr = "??????";
//                }else if(null!=auditingStatus && auditingStatus==2){
//                    auditingStatusStr = "?????????";
//                }
//                cell.setCellValue(auditingStatusStr);
//                cell = row.createCell(34);
//                cell.setCellValue(s.getAuditingDate());
//
//                cell = row.createCell(35);
//                cell.setCellValue(s.getEquipmentNumber());
//
//                cell = row.createCell(36);
//                cell.setCellValue(s.getGpsBrand());
//
//                cell = row.createCell(37);
//                cell.setCellValue(s.getGpsType());
//
//                cell = row.createCell(38);
//                cell.setCellValue(s.getGpsImei());
//
//                cell = row.createCell(39);
//                cell.setCellValue(s.getGpsDate());
//
//                cell = row.createCell(40);
//                cell.setCellValue(s.getCreateName());
//
//                cell = row.createCell(41);
//                cell.setCellValue(s.getCreateDate());
//
//                cell = row.createCell(42);
//                cell.setCellValue(s.getUpdateName());
//
//                cell = row.createCell(43);
//                cell.setCellValue(s.getUpdateDate());
//
//                cell = row.createCell(44);
//                cell.setCellValue(s.getMemo());
//
//                cell = row.createCell(45);
//                cell.setCellValue(s.getDriverName());
//
//                cell = row.createCell(46);
//                cell.setCellValue(s.getVehicleOwner());
//
//                cell = row.createCell(47);
//                cell.setCellValue(s.getVehicleType());
//
//                i++;
//            }
//        }
//        return wb;
//    }

    @Override
    public CarInfo selectCarInfoByCarId(CarInfo params) {
        CarInfo  entity =  carInfoExMapper.selectCarInfoByCarId(params);
        packageEntity(entity);
        return entity;
    }

    @Override
    public String selectCarByCarId(Integer params) {
        return carInfoExMapper.selectCarByCarId(params);
    }

    //??????
    public boolean isChineseLanguage(String str){
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    //???????????????
    public boolean isTransportNumber(String str){
        Pattern pattern = Pattern.compile("/^(^[\u4e00-\u9fa5]?????????[\u4e00-\u9fa5]{1,2}???\\d{4,6}\\s\\d{4,6}???$)$/");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    //??????
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static boolean isRegular(String str,String regular){
        Pattern pattern = Pattern.compile(regular);
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    @Override
    public Map<String,Object> importDeleteCarInfo(CarInfo params,HttpServletRequest request){
        String resultError1 = "-1";// ????????????
        String resultErrorMag1 = "????????????????????????!";

        Map<String, Object> result = new HashMap<String, Object>();
        List<CarImportExceptionEntity> listException = new ArrayList<CarImportExceptionEntity>();
        String fileName = params.getFileName();
        String path  = Common.getPath(request);
        String dirPath = path+params.getFileName();
        File DRIVERINFO = new File(dirPath);
        try {
            InputStream is = new FileInputStream(DRIVERINFO);
            Workbook workbook = null;
            String fileType = fileName.split("\\.")[1];
            if (fileType.equals("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(is);
            }
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // ????????????????????????
            Row row1 = sheet.getRow(0);
            if(row1==null){
                result.put("result", resultError1);
                result.put("msg", resultErrorMag1);
                return result;
            }
            for (int colIx = 0; colIx < 1; colIx++) {
                Cell cell = row1.getCell(colIx); // ???????????????
                CellValue cellValue = evaluator.evaluate(cell); // ???????????????
                if (cell == null || cellValue == null) {
                    result.put("result", resultError1);
                    result.put("msg", resultErrorMag1);
                    return result;
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("?????????")) {
                                result.put("result", resultError1);
                                result.put("msg", resultErrorMag1);
                                return result;
                            }
                            break;
                    }
                }
            }
            int minRowIx = 1;// ????????????????????????????????????????????????
            int maxRowIx = sheet.getLastRowNum(); // ???????????????????????????
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // ???????????????
                if (row == null) {
                    continue;
                }
                // ??????????????????????????????1 ???
                for (int colIx = 0; colIx < 1; colIx++) {
                    Cell cell = row.getCell(colIx); // ???????????????
                    CellValue cellValue = evaluator.evaluate(cell); // ???????????????
                    switch ((colIx + 1)) {
                        // ?????????
                        case 1:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(null);
                                returnVO.setReson("???" + (rowIx + 1) + "???????????????"+ (colIx + 1) + "??? ????????????????????????????????????????????????????????????");
                                listException.add(returnVO);
                                continue;
                            } else {
                                String licensePlates = Common.replaceBlank(cellValue.getStringValue());
                                CarInfo carInfo = new CarInfo();
                                carInfo.setLicensePlates(licensePlates);
                                CarInfo carInfoEntity = carInfoExMapper.selectCarInfoByCarId(carInfo);
                                if(carInfoEntity==null){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1)+ "???????????????" + (colIx + 1)+ "??? ????????????????????????");
                                    listException.add(returnVO);
                                    continue;
                                }else if(carInfoEntity.getIsFree()==1){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("???" + (rowIx + 1)+ "???????????????" + (colIx + 1)+ "??? ?????????????????????????????????????????????");
                                    listException.add(returnVO);
                                    continue;
                                }else{
                                    carInfoEntity.setStatus(2);
                                    carInfoEntity.setLicensePlates1(licensePlates);
                                    params.setUpdateBy(currentLoginUser.getId());
                                    result = this.saveCarInfo(carInfoEntity);
                                }
                            }
                            break;
                    }// switch end
                }// ???????????????
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String download = "";
        try {
            // ?????????????????????
            if (listException.size() > 0) {
                Workbook wb = exportExcel(request.getServletContext().getRealPath("/")+ "template" + File.separator + "car_exception.xlsx", listException);
                download = exportExcelFromTempletToLoacl(request, wb,new String("ERROR".getBytes("utf-8"), "iso8859-1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"".equals(download) && download != null) {
            result.put("download", download);
        } else {
            result.put("download", "");
        }
        return result;
    }

    @Override
    public String selectModelNameByLicensePlates(String params){
        return carInfoExMapper.selectModelNameByLicensePlates(params);
    }



//    @Override
//    public void getExportExcel(CarInfo params, List<String> datas) {
//        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
//        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(), ",");
//        String teamIds = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(), ",");
//        String carModelIds = "";
//        if(!"".equals(params.getCities())&&params.getCities()!=null&&!"null".equals(params.getCities())){
//            cities = params.getCities().replace(";", ",");
//        }
//        if(!"".equals(params.getSupplierIds())&&params.getSupplierIds()!=null&&!"null".equals(params.getSupplierIds())){
//            suppliers = params.getSupplierIds().replace(";", ",");
//        }
//        if(!"".equals(params.getCarModelIds())&&params.getCarModelIds()!=null&&!"null".equals(params.getCarModelIds())){
//            carModelIds = params.getCarModelIds().replace(";", ",");
//        }
//        params.setCarModelIds(carModelIds);
//        params.setSupplierIds(suppliers);
//        params.setCities(cities);
//        params.setTeamIds(teamIds);
//        List<CarInfo> list = carInfoExMapper.selectListNoPage(params);
//        StringBuilder builder = new StringBuilder();
//        if(list != null && list.size()>0){
//            List<Integer> createIds = new ArrayList<>();
//            List<Integer> updateIds = new ArrayList<>();
//            for(CarInfo carInfo:list){
//                if(carInfo.getCreateBy()!=null && StringUtils.isBlank(carInfo.getCreateName())){
//                    createIds.add(carInfo.getCreateBy());
//                }
//                if(carInfo.getUpdateBy()!=null && StringUtils.isBlank(carInfo.getUpdateName())){
//                    updateIds.add(carInfo.getUpdateBy());
//                }
//            }
//            List<CarAdmUser>  creator = userManagementService.getUsersByIdList(createIds);
//            List<CarAdmUser>  updator = userManagementService.getUsersByIdList(updateIds);
//            for (CarInfo carInfo : list){
//                if (carInfo.getCreateBy()!=null && StringUtils.isBlank(carInfo.getCreateName())){
//                    for (CarAdmUser user : creator){
//                        if ((carInfo.getCreateBy()).equals(user.getUserId())){
//                            carInfo.setCreateName(user.getUserName());
//                            break;
//                        }
//                    }
//                }
//                if (carInfo.getUpdateBy()!=null && StringUtils.isBlank(carInfo.getUpdateName())){
//                    for (CarAdmUser user : updator){
//                        if ((carInfo.getUpdateBy()).equals(user.getUserId())){
//                            carInfo.setUpdateName(user.getUserName());
//                            break;
//                        }
//                    }
//                }
//
//                builder.append(carInfo.getLicensePlates() == null ? "" : carInfo.getLicensePlates()).append(",");
//                builder.append(carInfo.getCityName() == null ? "" : carInfo.getCityName()).append(",");
//                builder.append(carInfo.getStatus()==1?"??????":"??????").append(",");
//                builder.append(carInfo.getSupplierName() == null ? "" : carInfo.getSupplierName()).append(",");
//                builder.append(carInfo.getModeName() == null ? "" : carInfo.getModeName()).append(",");
//                builder.append(carInfo.getModelDetail() == null ? "" : carInfo.getModelDetail()).append(",");
//                builder.append(carInfo.getPurchaseDate() == null ? "" : "\t" + carInfo.getPurchaseDate()).append(",");
//                builder.append(carInfo.getColor() == null ? "" : carInfo.getColor()).append(",");
//                builder.append(carInfo.getEngineNo() == null ? "" : "\t" + carInfo.getEngineNo()).append(",");
//                builder.append(carInfo.getFrameNo() == null ? "" : "\t" + carInfo.getFrameNo()).append(",");
//                builder.append(carInfo.getNextInspectDate() == null ? "" : "\t" + carInfo.getNextInspectDate()).append(",");
//                builder.append(carInfo.getNextMaintenanceDate() == null ? "" : "\t" + carInfo.getNextMaintenanceDate()).append(",");
//                builder.append(carInfo.getRentalExpireDate() == null ? "" : "\t" + carInfo.getRentalExpireDate()).append(",");
//                builder.append(carInfo.getNextClassDate() == null ? "" : "\t" + carInfo.getNextClassDate()).append(",");
//                builder.append(carInfo.getNextOperationDate() == null ? "" : "\t" + carInfo.getNextOperationDate()).append(",");
//                builder.append(carInfo.getNextSecurityDate() == null ? "" : "\t" + carInfo.getNextSecurityDate()).append(",");
//                builder.append(carInfo.getTwoLevelMaintenanceDate() == null ? "" : "\t" + carInfo.getTwoLevelMaintenanceDate()).append(",");
//                builder.append(carInfo.getCarryPassengers() == null ? "" : carInfo.getCarryPassengers()).append(",");
//                builder.append(carInfo.getVehicleBrand() == null ? "" : carInfo.getVehicleBrand()).append(",");
//                builder.append(carInfo.getClicensePlatesColor() == null ? "" : carInfo.getClicensePlatesColor()).append(",");
//                builder.append(carInfo.getVehicleVINCode() == null ? "" : "\t" + carInfo.getVehicleVINCode()).append(",");
//                builder.append(carInfo.getVehicleRegistrationDate() == null ? "" : "\t" + carInfo.getVehicleRegistrationDate()).append(",");
//                Integer fuelType = carInfo.getOverHaulStatus();
//                String  fuelTypeStr = "???";
//                if(null!=fuelType && fuelType==1){
//                    fuelTypeStr = "??????";
//                }else if(null!=fuelType && fuelType==2){
//                    fuelTypeStr = "??????";
//                }else if(null!=fuelType && fuelType==3){
//                    fuelTypeStr = "???";
//                }else if(null!=fuelType && fuelType==4){
//                    fuelTypeStr = "?????????";
//                }else if(null!=fuelType && fuelType==5){
//                    fuelTypeStr = "?????????";
//                }else if(null!=fuelType && fuelType==6){
//                    fuelTypeStr = "???????????????";
//                }else if(null!=fuelType && fuelType==7){
//                    fuelTypeStr = "??????";
//                }else if(null!=fuelType && fuelType==8){
//                    fuelTypeStr = "??????";
//                }else if(null!=fuelType && fuelType==9){
//                    fuelTypeStr = "?????????";
//                }else if(null!=fuelType && fuelType==10){
//                    fuelTypeStr = "????????????";
//                }
//                builder.append(fuelTypeStr).append(",");
//                builder.append(carInfo.getVehicleEngineDisplacement() == null ? "" : carInfo.getVehicleEngineDisplacement()).append(",");
//                builder.append(carInfo.getVehicleEnginePower() == null ? "" : carInfo.getVehicleEnginePower()).append(",");
//                builder.append(carInfo.getVehicleEngineWheelbase() == null ? "" : carInfo.getVehicleEngineWheelbase()).append(",");
//                builder.append(carInfo.getTransportNumber() == null ? "" : "\t" + carInfo.getTransportNumber()).append(",");
//                builder.append(carInfo.getCertificationAuthority() == null ? "" : carInfo.getCertificationAuthority()).append(",");
//                builder.append(carInfo.getOperatingRegion() == null ? "" : carInfo.getOperatingRegion()).append(",");
//                builder.append(carInfo.getTransportNumberDateStart() == null ? "" : "\t" + carInfo.getTransportNumberDateStart()).append(",");
//                builder.append(carInfo.getTransportNumberDateEnd() == null ? "" : "\t" + carInfo.getTransportNumberDateEnd()).append(",");
//                builder.append(carInfo.getFirstDate() == null ? "" : "\t" + carInfo.getFirstDate()).append(",");
//                Integer overHaulStatus = carInfo.getOverHaulStatus();
//                String  statusStr = "??????";
//                if(null!=overHaulStatus && overHaulStatus==1){
//                    statusStr = "?????????";
//                }else if(null!=overHaulStatus && overHaulStatus==0){
//                    statusStr = "?????????";
//                }
//                builder.append(statusStr).append(",");
//                Integer auditingStatus = carInfo.getAuditingStatus();
//                String  auditingStatusStr = "?????????";
//                if(null!=auditingStatus && auditingStatus==1){
//                    auditingStatusStr = "??????";
//                }else if(null!=auditingStatus && auditingStatus==2){
//                    auditingStatusStr = "?????????";
//                }
//                builder.append(auditingStatusStr).append(",");
//                builder.append(carInfo.getAuditingDate() == null ? "" : "\t" + carInfo.getAuditingDate()).append(",");
//                builder.append(carInfo.getEquipmentNumber() == null ? "" : "\t" + carInfo.getEquipmentNumber()).append(",");
//                builder.append(carInfo.getGpsBrand() == null ? "" : carInfo.getGpsBrand()).append(",");
//                builder.append(carInfo.getGpsType() == null ? "" : carInfo.getGpsType()).append(",");
//                builder.append(carInfo.getGpsImei() == null ? "" : "\t" + carInfo.getGpsImei()).append(",");
//                builder.append(carInfo.getGpsDate() == null ? "" : "\t" + carInfo.getGpsDate()).append(",");
//                builder.append(carInfo.getCreateName() == null ? "" : carInfo.getCreateName()).append(",");
//                builder.append(carInfo.getCreateDate() == null ? "" : "\t" + carInfo.getCreateDate()).append(",");
//                builder.append(carInfo.getUpdateName() == null ? "" : carInfo.getUpdateName()).append(",");
//                builder.append(carInfo.getUpdateDate() == null ? "" : "\t" + carInfo.getUpdateDate()).append(",");
//                builder.append(carInfo.getMemo() == null ? "" : carInfo.getMemo()).append(",");
//                builder.append(carInfo.getDriverName() == null ? "" : carInfo.getDriverName()).append(",");
//                builder.append(carInfo.getVehicleOwner() == null ? "" : carInfo.getVehicleOwner()).append(",");
//                builder.append(carInfo.getVehicleType() == null ? "" : carInfo.getVehicleType());
//                datas.add(builder.toString());
//                builder.setLength(0);
//            }
//        }
//    }

//    @Override
//    public PageInfo<CarInfo> findCarInfo(CarInfo params) {
//        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
//        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(), ",");
//        String teamIds = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(), ",");
//        String carModelIds = "";
//        if(!"".equals(params.getCities())&&params.getCities()!=null&&!"null".equals(params.getCities())){
//            cities = params.getCities().replace(";", ",");
//        }
//        if(!"".equals(params.getSupplierIds())&&params.getSupplierIds()!=null&&!"null".equals(params.getSupplierIds())){
//            suppliers = params.getSupplierIds().replace(";", ",");
//        }
//        if(!"".equals(params.getCarModelIds())&&params.getCarModelIds()!=null&&!"null".equals(params.getCarModelIds())){
//            carModelIds = params.getCarModelIds().replace(";", ",");
//        }
//        params.setCarModelIds(carModelIds);
//        params.setSupplierIds(suppliers);
//        params.setCities(cities);
//        params.setTeamIds(teamIds);
//
//        PageHelper.startPage(params.getPage(), params.getPagesize(),true);
//        List<CarInfo> list = carInfoExMapper.selectListNoPage(params);
//        PageInfo<CarInfo> pageInfo = new PageInfo<>(list);
//        return pageInfo;
//    }

    @Override
    public void transContent(List<CarInfo> list) {
        List<Integer> createIds = new ArrayList<>();
        List<Integer> updateIds = new ArrayList<>();
        for(CarInfo carInfo:list){
            if(carInfo.getCreateBy()!=null && StringUtils.isBlank(carInfo.getCreateName())){
                createIds.add(carInfo.getCreateBy());
            }
            if(carInfo.getUpdateBy()!=null && StringUtils.isBlank(carInfo.getUpdateName())){
                updateIds.add(carInfo.getUpdateBy());
            }
        }
        List<CarAdmUser> creator = null;
        if ( createIds.size() > 0){
            creator = userManagementService.getUsersByIdList(createIds);
        }

        List<CarAdmUser>  updator = null;
        if (updateIds.size() > 0){
            updator  = userManagementService.getUsersByIdList(updateIds);
        }

        Map<String,CarAdmUser> createByMap = new HashMap<>();
        Map<String,CarAdmUser> updateByMap = new HashMap<>();

        if(creator != null){
            for (CarAdmUser user : creator){
                createByMap.put("t_"+user.getUserId(),user);
            }
        }
        if(updator != null){
            for (CarAdmUser user : updator){
                updateByMap.put("t_"+user.getUserId(),user);
            }
        }
        CarAdmUser item;
        for (CarInfo carInfo : list){
            if (carInfo.getCreateBy()!=null && StringUtils.isEmpty(carInfo.getCreateName())){
                item = createByMap.get("t_"+carInfo.getCreateBy());
                if(item != null){
                    carInfo.setCreateName(item.getUserName());
                }
            }
            if (carInfo.getUpdateBy()!=null && StringUtils.isBlank(carInfo.getUpdateName())){
                item = updateByMap.get("t_"+carInfo.getUpdateBy());
                if(item != null){
                    carInfo.setUpdateName(item.getUserName());
                }
            }
        }
    }

    @Override
    public void doTrans4Csv(List<String> casDataList, List<CarInfo> carInfoList) {
        if(carInfoList != null && carInfoList.size()>0){
            transContent(carInfoList);
            for (CarInfo carInfo : carInfoList){
                StringBuilder builder = new StringBuilder();
                builder.append(carInfo.getLicensePlates() == null ? "" : carInfo.getLicensePlates()).append(",");
                builder.append(carInfo.getCityName() == null ? "" : carInfo.getCityName()).append(",");
                builder.append(carInfo.getStatus()==1?"??????":"??????").append(",");
                builder.append(carInfo.getSupplierName() == null ? "" : carInfo.getSupplierName()).append(",");
                builder.append(carInfo.getModeName() == null ? "" : carInfo.getModeName()).append(",");
                builder.append(carInfo.getModelDetail() == null ? "" : carInfo.getModelDetail()).append(",");
                builder.append(carInfo.getPurchaseDate() == null ? "" : "\t" + carInfo.getPurchaseDate()).append(",");
                builder.append(carInfo.getColor() == null ? "" : carInfo.getColor()).append(",");
                builder.append(carInfo.getEngineNo() == null ? "" : "\t" + carInfo.getEngineNo()).append(",");
                builder.append(carInfo.getFrameNo() == null ? "" : "\t" + carInfo.getFrameNo()).append(",");
                builder.append(carInfo.getNextInspectDate() == null ? "" : "\t" + carInfo.getNextInspectDate()).append(",");
                builder.append(carInfo.getNextMaintenanceDate() == null ? "" : "\t" + carInfo.getNextMaintenanceDate()).append(",");
                builder.append(carInfo.getRentalExpireDate() == null ? "" : "\t" + carInfo.getRentalExpireDate()).append(",");
                builder.append(carInfo.getNextClassDate() == null ? "" : "\t" + carInfo.getNextClassDate()).append(",");
                builder.append(carInfo.getNextOperationDate() == null ? "" : "\t" + carInfo.getNextOperationDate()).append(",");
                builder.append(carInfo.getNextSecurityDate() == null ? "" : "\t" + carInfo.getNextSecurityDate()).append(",");
                builder.append(carInfo.getTwoLevelMaintenanceDate() == null ? "" : "\t" + carInfo.getTwoLevelMaintenanceDate()).append(",");
                builder.append(carInfo.getCarryPassengers() == null ? "" : carInfo.getCarryPassengers()).append(",");
                builder.append(carInfo.getVehicleBrand() == null ? "" : carInfo.getVehicleBrand()).append(",");
                builder.append(carInfo.getClicensePlatesColor() == null ? "" : carInfo.getClicensePlatesColor()).append(",");
                builder.append(carInfo.getVehicleVINCode() == null ? "" : "\t" + carInfo.getVehicleVINCode()).append(",");
                builder.append(carInfo.getVehicleRegistrationDate() == null ? "" : "\t" + carInfo.getVehicleRegistrationDate()).append(",");
                Integer fuelType = carInfo.getOverHaulStatus();
                String  fuelTypeStr = "???";
                if(null!=fuelType && fuelType==1){
                    fuelTypeStr = "??????";
                }else if(null!=fuelType && fuelType==2){
                    fuelTypeStr = "??????";
                }else if(null!=fuelType && fuelType==3){
                    fuelTypeStr = "???";
                }else if(null!=fuelType && fuelType==4){
                    fuelTypeStr = "?????????";
                }else if(null!=fuelType && fuelType==5){
                    fuelTypeStr = "?????????";
                }else if(null!=fuelType && fuelType==6){
                    fuelTypeStr = "???????????????";
                }else if(null!=fuelType && fuelType==7){
                    fuelTypeStr = "??????";
                }else if(null!=fuelType && fuelType==8){
                    fuelTypeStr = "??????";
                }else if(null!=fuelType && fuelType==9){
                    fuelTypeStr = "?????????";
                }else if(null!=fuelType && fuelType==10){
                    fuelTypeStr = "????????????";
                }
                builder.append(fuelTypeStr).append(",");
                builder.append(carInfo.getVehicleEngineDisplacement() == null ? "" : carInfo.getVehicleEngineDisplacement()).append(",");
                builder.append(carInfo.getVehicleEnginePower() == null ? "" : carInfo.getVehicleEnginePower()).append(",");
                builder.append(carInfo.getVehicleEngineWheelbase() == null ? "" : carInfo.getVehicleEngineWheelbase()).append(",");
                builder.append(carInfo.getTransportNumber() == null ? "" : "\t" + carInfo.getTransportNumber()).append(",");
                builder.append(carInfo.getCertificationAuthority() == null ? "" : carInfo.getCertificationAuthority()).append(",");
                builder.append(carInfo.getOperatingRegion() == null ? "" : carInfo.getOperatingRegion()).append(",");
                builder.append(carInfo.getTransportNumberDateStart() == null ? "" : "\t" + carInfo.getTransportNumberDateStart()).append(",");
                builder.append(carInfo.getTransportNumberDateEnd() == null ? "" : "\t" + carInfo.getTransportNumberDateEnd()).append(",");
                builder.append(carInfo.getFirstDate() == null ? "" : "\t" + carInfo.getFirstDate()).append(",");
                Integer overHaulStatus = carInfo.getOverHaulStatus();
                String  statusStr = "??????";
                if(null!=overHaulStatus && overHaulStatus==1){
                    statusStr = "?????????";
                }else if(null!=overHaulStatus && overHaulStatus==0){
                    statusStr = "?????????";
                }
                builder.append(statusStr).append(",");
                Integer auditingStatus = carInfo.getAuditingStatus();
                String  auditingStatusStr = "?????????";
                if(null!=auditingStatus && auditingStatus==1){
                    auditingStatusStr = "??????";
                }else if(null!=auditingStatus && auditingStatus==2){
                    auditingStatusStr = "?????????";
                }
                builder.append(auditingStatusStr).append(",");
                builder.append(carInfo.getAuditingDate() == null ? "" : "\t" + carInfo.getAuditingDate()).append(",");
                builder.append(carInfo.getEquipmentNumber() == null ? "" : "\t" + carInfo.getEquipmentNumber()).append(",");
                builder.append(carInfo.getGpsBrand() == null ? "" : carInfo.getGpsBrand()).append(",");
                builder.append(carInfo.getGpsType() == null ? "" : carInfo.getGpsType()).append(",");
                builder.append(carInfo.getGpsImei() == null ? "" : "\t" + carInfo.getGpsImei()).append(",");
                builder.append(carInfo.getGpsDate() == null ? "" : "\t" + carInfo.getGpsDate()).append(",");
                builder.append(carInfo.getCreateName() == null ? "" : carInfo.getCreateName()).append(",");
                builder.append(carInfo.getCreateDate() == null ? "" : "\t" + carInfo.getCreateDate()).append(",");
                builder.append(carInfo.getUpdateName() == null ? "" : carInfo.getUpdateName()).append(",");
                builder.append(carInfo.getUpdateDate() == null ? "" : "\t" + carInfo.getUpdateDate()).append(",");
                builder.append(carInfo.getMemo() == null ? "" : carInfo.getMemo()).append(",");
                builder.append(carInfo.getDriverName() == null ? "" : carInfo.getDriverName()).append(",");
                builder.append(carInfo.getVehicleOwner() == null ? "" : carInfo.getVehicleOwner()).append(",");
                builder.append(carInfo.getVehicleType() == null ? "" : carInfo.getVehicleType()).append(",");

                Integer taxiInvoicePrint = carInfo.getTaxiInvoicePrint();
                String  taxiInvoicePrintName = "??????";
                if(null!=taxiInvoicePrint && taxiInvoicePrint==1){
                    taxiInvoicePrintName = "???";
                }else if(null!=taxiInvoicePrint && taxiInvoicePrint==0){
                    taxiInvoicePrintName = "???";
                }
                builder.append(taxiInvoicePrintName).append(",");

                casDataList.add(builder.toString());
            }
        }
    }


}
