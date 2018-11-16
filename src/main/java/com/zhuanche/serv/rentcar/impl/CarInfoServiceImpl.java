package com.zhuanche.serv.rentcar.impl;

import com.google.common.collect.Sets;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.serv.authc.UserManagementService;
import com.zhuanche.serv.rentcar.CarInfoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MyRestTemplate;
import mapper.rentcar.CarSysDictionaryMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizModelExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import mapper.rentcar.ex.CarInfoExMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("carApiTemplate")
    private MyRestTemplate carApiTemplate;



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




    @Override
    public List<CarInfoVo> selectcarnum(Map<String, Object> map) {
        return carInfoExMapper.selectcarnum(map);
    }

    @Override
    public List<CarInfo> selectList(CarInfo params) {
        return carInfoExMapper.selectList(params);
    }

    @Override
    public int selectListCount(CarInfo params) {
        return carInfoExMapper.selectListCount(params);
    }

    @Override
    public CarInfo selectCarInfoByLicensePlates(
            CarInfo carInfoEntity) {
        CarInfo params = carInfoExMapper
                .selectCarInfoByLicensePlates(carInfoEntity);
        // 车龄
        int inspectFlag = 0;
        int MaintenanceFlag = 0;
        Calendar now = Calendar.getInstance();
        // long monthNow= now.getTimeInMillis()/(1000*60*60*24*30);
        long dayNow = now.getTimeInMillis() / (1000 * 60 * 60 * 24);

        long monthNow = dayNow / 30;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 小写的mm表示的是分钟
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
        String age = ageYear + "年" + ageMonth + "月";
        params.setCarAge(age);
        // 距离下次车检时间
        try {
            Date nextInspectDate = sdf.parse(params.getNextInspectDate());
            now.setTime(nextInspectDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long nextInspectDay = now.getTimeInMillis() / (1000 * 60 * 60 * 24);
        long InspectDay = nextInspectDay - dayNow + 1;
        // 距离下次维保时间
        try {
            Date nextMaintenanceDate = sdf.parse(params
                    .getNextMaintenanceDate());
            now.setTime(nextMaintenanceDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long nextMaintenanceDay = now.getTimeInMillis() / (1000 * 60 * 60 * 24);
        long MaintenanceDay = nextMaintenanceDay - dayNow + 1;
        // 判断是否提示
        if (InspectDay <= 30 && InspectDay >= 0) {
            if (InspectDay == 0) {
                params.setTimeOfnextInspect("是今天");
            } else {
                params.setTimeOfnextInspect("还有" + InspectDay + "天");
            }
            inspectFlag = 1;
        }
        if (MaintenanceDay <= 30 && MaintenanceDay >= 0) {
            if (MaintenanceDay == 0) {
                params.setTimeOfnextMaintenance("是今天");
            } else {
                params.setTimeOfnextMaintenance("还有" + MaintenanceDay + "天");
            }
            MaintenanceFlag = 1;
        }
        params.setInspectFlag(inspectFlag);
        params.setMaintenanceFlag(MaintenanceFlag);
        return params;
    }

    @Override
    public AjaxResponse importCarInfo(MultipartFile fileName, HttpServletRequest request) {
        String resultError1 = "-1";// 模板错误
        String resultErrorMag1 = "导入模板格式错误!";

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
            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            if(row1==null){
                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, resultErrorMag1);
            }
            for (int colIx = 0; colIx < 43; colIx++) {
                Cell cell = row1.getCell(colIx); // 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, resultErrorMag1);
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("序号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "1：序号列不正确");
                            }
                            break;
                        case 2:
                            if (!cellValue.getStringValue().contains("车牌号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "2：车牌号列不正确");
                            }
                            break;
                        case 3:
                            if (!cellValue.getStringValue().contains("城市")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "3：城市列不正确");
                            }
                            break;
                        case 4:
                            if (!cellValue.getStringValue().contains("状态")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "4：状态列不正确");
                            }
                            break;
                        case 5:
                            if (!cellValue.getStringValue().contains("供应商")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "5：供应商列不正确");
                            }
                            break;
                        case 6:
                            if (!cellValue.getStringValue().contains("车型")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "6：车型列不正确");
                            }
                            break;
                        case 7:
                            if (!cellValue.getStringValue().contains("具体车型")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "7：具体车型列不正确");
                            }
                            break;
                        case 8:
                            if (!cellValue.getStringValue().contains("购买日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "8:购买日期列不正确");
                            }
                            break;
                        case 9:
                            if (!cellValue.getStringValue().contains("颜色")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "9：颜色列不正确");
                            }
                            break;
                        case 10:
                            if (!cellValue.getStringValue().contains("发动机号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "10：发动机号列不正确");
                            }
                            break;
                        case 11:
                            if (!cellValue.getStringValue().contains("车架号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "11：车架号列不正确");
                            }
                            break;
                        case 12:
                            if (!cellValue.getStringValue().contains("下次维保时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "12：下次维保时间列不正确");
                            }
                            break;
                        case 13:
                            if (!cellValue.getStringValue().contains("下次等级鉴定时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "13：下次等级坚定时间列不正确");
                            }
                            break;
                        case 14:
                            if (!cellValue.getStringValue().contains("下次检营运证时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "14：下次检营运证时间列不正确");
                            }
                            break;
                        case 15:
                            if (!cellValue.getStringValue().contains("下次检治安证时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "15：下次检治安证时间列不正确");
                            }
                            break;
                        case 16:
                            if (!cellValue.getStringValue().contains("二次维护时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "16：二次维护时间列不正确");
                            }
                            break;
                        case 17:
                            if (!cellValue.getStringValue().contains("租赁到期时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "17：租赁到期时间列不正确");
                            }
                            break;
                        case 18:
                            if (!cellValue.getStringValue().contains("下次车检时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "18：下次车检时间列不正确");
                            }
                            break;
                        case 19:
                            if (!cellValue.getStringValue().contains("核定载客位")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "19：核定载客位列不正确");
                            }
                            break;
                        case 20:
                            if (!cellValue.getStringValue().contains("车辆厂牌")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "20：车辆厂牌列不正确");
                            }
                            break;
                        case 21:
                            if (!cellValue.getStringValue().contains("车牌颜色")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "21：车牌颜色列不正确");
                            }
                            break;
                        case 22:
                            if (!cellValue.getStringValue().contains("车辆VIN码")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "22：车辆VIN码列不正确");
                            }
                            break;
                        case 23:
                            if (!cellValue.getStringValue().contains("车辆注册日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "23：车辆注册日期列不正确");
                            }
                            break;
                        case 24:
                            if (!cellValue.getStringValue().contains("车辆燃料类型")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "24：车辆燃料类型列不正确");
                            }
                            break;
                        case 25:
                            if (!cellValue.getStringValue().contains("发动机排量（毫升）")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "25：发动机排量（毫升）列不正确");
                            }
                            break;
                        case 26:
                            if (!cellValue.getStringValue().contains("发动机功率（千瓦）")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "26：发动机功率（千瓦）列不正确");
                            }
                            break;
                        case 27:
                            if (!cellValue.getStringValue().contains("车辆轴距（毫米）")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "27：车辆轴距（毫米）列不正确");
                            }
                            break;
                        case 28:
                            if (!cellValue.getStringValue().contains("运输证字号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "28：运输证字号列不正确");
                            }
                            break;
                        case 29:
                            if (!cellValue.getStringValue().contains("车辆运输证发证机构")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "29：车辆运输证发证机构列不正确");
                            }
                            break;
                        case 30:
                            if (!cellValue.getStringValue().contains("车辆经营区域")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "30：车辆经营区域列不正确");
                            }
                            break;
                        case 31:
                            if (!cellValue.getStringValue().contains("车辆运输证有效期起")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "31：车辆运输证有效期起列不正确");
                            }
                            break;
                        case 32:
                            if (!cellValue.getStringValue().contains("车辆运输证有效期止")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "32：车辆运输证有效期止列不正确");
                            }
                            break;
                        case 33:
                            if (!cellValue.getStringValue().contains("车辆初次登记日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "33：车辆初次登记日期列不正确");
                            }
                            break;
                        case 34:
                            if (!cellValue.getStringValue().contains("车辆检修状态")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "34：车辆检修状态列不正确");
                            }
                            break;
                        case 35:
                            if (!cellValue.getStringValue().contains("车辆年度审验状态")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "35：车辆年度审验状态列不正确");
                            }
                            break;
                        case 36:
                            if (!cellValue.getStringValue().contains("车辆年度审验日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "36：车辆年度审验日期列不正确");
                            }
                            break;
                        case 37:
                            if (!cellValue.getStringValue().contains("发票打印设备序列号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "37：发票打印设备序列号列不正确");
                            }
                            break;
                        case 38:
                            if (!cellValue.getStringValue().contains("卫星定位装置品牌")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "38：卫星定位装置品牌列不正确");
                            }
                            break;
                        case 39:
                            if (!cellValue.getStringValue().contains("卫星定位装置型号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "39：卫星定位装置型号列不正确");
                            }
                            break;
                        case 40:
                            if (!cellValue.getStringValue().contains("卫星定位装置IMEI号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "40：卫星定位装置IMEI号列不正确");
                            }
                            break;
                        case 41:
                            if (!cellValue.getStringValue().contains("卫星定位设备安装日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "41：卫星定位设备安装日期列不正确");
                            }
                            break;
                        case 42:
                            if (!cellValue.getStringValue().contains("所属车主")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "42：所属车主列不正确");
                            }
                            break;
                        case 43:
                            if (!cellValue.getStringValue().contains("车辆类型(以机动车行驶证为主)")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "43：以机动车行驶证为主列不正确");
                            }
                            break;
                    }
                }
            }

            int minRowIx = 1;// 过滤掉标题，从第一行开始导入数据
            int maxRowIx = sheet.getLastRowNum(); // 要导入数据的总条数
             int successCount = 0;// 成功导入条数

            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // 获取行对象
                if (row == null) {
                    continue;
                }
                CarInfo carBizCarInfo = new CarInfo();
                boolean isTrue = true;// 标识是否为有效数据
                // String cityName = "";
                // 车辆状态，创建人
//				carBizCarInfo.setStatus(1);
                carBizCarInfo.setCreateBy(currentLoginUser.getId());
                carBizCarInfo.setUpdateBy(currentLoginUser.getId());
                Integer cityId = null;
                // 车辆导入模板总共41 列
                for (int colIx = 0; colIx < 43; colIx++) {
                    Cell cell = row.getCell(colIx); // 获取列对象
                    CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                    if((colIx + 1)==2){
                        if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                        }else{
                            licensePlates = cellValue.getStringValue();
                        }
                    }
                    switch ((colIx + 1)) {
                        // 车牌号
                        case 1:
                            //log.info("序号："+cellValue.getStringValue());
                            break;
                        // 车牌号
                        case 2:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车牌号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String license = cellValue.getStringValue();
                                license = Common.replaceBlank(license);
                                // TODO 判断车牌号 是否存在
                                if (license.length() == 7||license.length() == 8) {
                                    CarSysDictionary orderDictionaryEntity1 = carSysDictionaryMapper.selectByPrimaryKey(17);
                                    String strValue = orderDictionaryEntity1.getDicValue();
                                    Integer intValue = Integer.valueOf(strValue.replace(".00", ""));
                                    log.info("通过车管后台的参数--17--判断是否执行更新已有车牌号信息--1 更新 2不更新==车牌号=="+license);
                                    if(intValue!=null&&intValue.equals(2)){
                                        int n = carInfoExMapper.checkLicensePlates(license);
                                        if (n > 0) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setLicensePlates(licensePlates);
                                            returnVO.setReson("第" + (rowIx + 1)
                                                    + "行数据，第" + (colIx + 1)
                                                    + "列 【车牌号】已存在");
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
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车牌号】不规整 请检查");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 城市
                        case 3:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【城市】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // 判断城市 是否存在
                                cityId = cityDao.queryCityByCityName(cellValue.getStringValue());
                                if (cityId == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 【城市】无效");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    carBizCarInfo.setCityId(cityId);
                                }
                            }
                            break;
                        // 状态
                        case 4:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【状态】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // 判断状态
                                if ("有效".equals(cellValue.getStringValue())||"无效".equals(cellValue.getStringValue())){
                                    if ("有效".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setStatus(1);
                                    } else {
                                        carBizCarInfo.setStatus(0);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【状态】为有效或者无效");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 供应商
                        case 5:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【供应商】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // 判断供应商 是否存在
                                Integer supplierId = carBizSupplierExMapper.querySupplierBySupplierName(cellValue.getStringValue());
                                if (supplierId == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 【供应商】无效");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    if (cityId == null) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第" + (rowIx + 1)
                                                + "行数据，第" + (colIx + 1)
                                                + "列 【城市】无效");
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
                                            returnVO.setReson("第" + (rowIx + 1)
                                                    + "行数据，第" + (colIx + 1)
                                                    + "列 【供应商】不在相应【城市】或无权操作");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        }
                                    }
                                }
                            }
                            break;
                        // 车型
                        case 6:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车型】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                Integer carModelId = carBizModelExMapper.queryCarModelByCarModelName(cellValue.getStringValue());
                                if (carModelId == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 【车型】无效");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    carBizCarInfo.setCarModelId(carModelId);
                                }
                            }
                            break;
                        // 具体车型
                        case 7:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【具体车型】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String modelDetail = cellValue.getStringValue();
                                modelDetail = Common.replaceBlank(modelDetail);
                                carBizCarInfo.setModelDetail(modelDetail);
                            }
                            break;
                        // 购买日期
                        case 8:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【购买日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() > sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【购买日期】应该小于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setPurchaseDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【购买日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【购买日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 颜色
                        case 9:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【颜色】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(cellValue.getStringValue().matches("[\u4e00-\u9fa5]*")){
                                    carBizCarInfo.setColor(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【颜色】只能为汉字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 发动机号
                        case 10:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【发动机号】不能为空且单元格格式必须为文本");
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
//								returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
//										+ (colIx + 1) + "列 【发动机号】为8位数字字母组合");
//								listException.add(returnVO);
//								isTrue = false;
//							}
                            }
                            break;
                        // 车架号
                        case 11:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车架号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isRegular(cellValue.getStringValue(),REGULAR_FRAME)){
                                    carBizCarInfo.setFrameNo(cellValue.getStringValue());
                                    carBizCarInfo.setVehicleVINCode(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车架号】为13或17位数字字母组合");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 下次维保时间
                        case 12:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【下次维保时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【下次维保时间】应该大于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextMaintenanceDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【下次维保时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【下次维保时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 下次等级鉴定时间
                        case 13:
//						if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
//							CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//							returnVO.setLicensePlates(licensePlates);
//							returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
//									+ (colIx + 1)
//									+ "列 【下次等级鉴定时间】不能为空且单元格格式必须为文本");
//							listException.add(returnVO);
//							isTrue = false;
//						} else {
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【下次等级鉴定时间】应该大于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextClassDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【下次等级鉴定时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【下次等级鉴定时间】如没有填写，请右击清空单元格");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 下次运营证检测时间
                        case 14:
//						if (cellValue == null
//								|| StringUtils.isEmpty(cellValue
//										.getStringValue())) {
//							CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//							returnVO.setLicensePlates(licensePlates);
//							returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
//									+ (colIx + 1)
//									+ "列 【下次运营证检测时间】不能为空且单元格格式必须为文本");
//							listException.add(returnVO);
//							isTrue = false;
//						} else {
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【下次运营证检测时间】应该大于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextOperationDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【下次运营证检测时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【下次运营证检测时间】如没有填写，请右击清空单元格");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 下次治安证检测时间
                        case 15:
//						if (cellValue == null
//								|| StringUtils.isEmpty(cellValue
//										.getStringValue())) {
//							CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//							returnVO.setLicensePlates(licensePlates);
//							returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
//									+ (colIx + 1)
//									+ "列 【下次治安证检测时间】不能为空且单元格格式必须为文本");
//							listException.add(returnVO);
//							isTrue = false;
//						} else {
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【下次治安证检测时间】应该大于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextSecurityDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【下次治安证检测时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【下次治安证检测时间】如没有填写，请右击清空单元格");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 二次维护时间
                        case 16:
//						if (cellValue == null
//								|| StringUtils.isEmpty(cellValue
//										.getStringValue())) {
//							CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
//							returnVO.setLicensePlates(licensePlates);
//							returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
//									+ (colIx + 1) + "列 【二次维护时间】不能为空且单元格格式必须为文本");
//							listException.add(returnVO);
//							isTrue = false;
//						} else {
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【二次维护时间】应该大于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setTwoLevelMaintenanceDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【二次维护时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【二次维护时间】如没有填写，请右击清空单元格");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 租赁到期时间
                        case 17:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【租赁到期时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【租赁到期时间】应该大于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setRentalExpireDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【租赁到期时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【租赁到期时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 下次车检时间
                        case 18:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【下次车检时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【下次车检时间】应该大于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setNextInspectDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【下次车检时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【下次车检时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 核定载客位
                        case 19:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【核定载客位】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isRegular(cellValue.getStringValue(),REGULAR_NUMBER)){
                                    carBizCarInfo.setCarryPassengers(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【核定载客位】只能为数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆厂牌
                        case 20:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆厂牌】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String vehicleBrand = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setVehicleBrand(vehicleBrand);
                            }
                            break;
                        //车牌颜色
                        case 21:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车牌颜色】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(cellValue.getStringValue().matches("[\u4e00-\u9fa5]*")){
                                    carBizCarInfo.setClicensePlatesColor(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车牌颜色】只能为汉字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }

                            }
                            break;
                        //车辆VIN码
                        case 22:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆VIN码】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isRegular(cellValue.getStringValue(),REGULAR_FRAME)){
                                    //carBizCarInfo.setVehicleVINCode(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车辆VIN码】与车架号一致为13或17位数字字母组合");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆注册日期
                        case 23:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆注册日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

                                if(StringUtils.isNotEmpty(d)&&d.length()>2){
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() > sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                    + (colIx + 1) + "列 【车辆注册日期】应该小于当前时间");
                                            listException.add(returnVO);
                                            isTrue = false;
                                        } else {
                                            d = dateFormat.format(dateFormat.parse(d));
                                            carBizCarInfo.setVehicleRegistrationDate(d);
                                        }
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【车辆注册日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【车辆注册日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆燃料类型
                        case 24:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆燃料类型】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String fuelType = Common.replaceBlank(cellValue.getStringValue());
                                // 车辆燃料类型
                                if ("汽油".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(1);
                                }else if ("柴油".equals(fuelType)){
                                    carBizCarInfo.setFuelType(2);
                                }else if ("电".equals(fuelType)){
                                    carBizCarInfo.setFuelType(3);
                                }else if ("混合油".equals(fuelType)){
                                    carBizCarInfo.setFuelType(4);
                                }else if ("天然气".equals(fuelType)){
                                    carBizCarInfo.setFuelType(5);
                                }else if ("液化石油气".equals(fuelType)){
                                    carBizCarInfo.setFuelType(6);
                                }else if ("甲醇".equals(fuelType)){
                                    carBizCarInfo.setFuelType(7);
                                }else if ("乙醇".equals(fuelType)){
                                    carBizCarInfo.setFuelType(8);
                                }else if ("太阳能".equals(fuelType)){
                                    carBizCarInfo.setFuelType(9);
                                }else if ("混合动力".equals(fuelType)){
                                    carBizCarInfo.setFuelType(10);
                                }else if("无".equals(fuelType)){
                                    carBizCarInfo.setFuelType(11);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车辆燃料类型】为汽油|柴油|电|混合油|天然气|液化石油气|甲醇|乙醇|太阳能|混合动力|无");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆发动机排量
                        case 25:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆发动机排量】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isNumeric(cellValue.getStringValue())){
                                    if(Integer.valueOf(cellValue.getStringValue())>=700&&Integer.valueOf(cellValue.getStringValue())<=5000){
                                        carBizCarInfo.setVehicleEngineDisplacement(cellValue.getStringValue());
                                    }else{
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【车辆发动机排量】只能有数字且在700到5000之间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车辆发动机排量】只能有数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //发动机功率
                        case 26:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【发动机功率】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isNumeric(cellValue.getStringValue())){
                                    if(Integer.valueOf(cellValue.getStringValue())>=50&&Integer.valueOf(cellValue.getStringValue())<=600){
                                        carBizCarInfo.setVehicleEnginePower(cellValue.getStringValue());
                                    }else{
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【发动机功率】只能有数字且在50到600之间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【发动机功率】只能有数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆轴距
                        case 27:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆轴距】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(isNumeric(cellValue.getStringValue())){
                                    if(Integer.valueOf(cellValue.getStringValue())>=2000&&Integer.valueOf(cellValue.getStringValue())<=6000){
                                        carBizCarInfo.setVehicleEngineWheelbase(cellValue.getStringValue());
                                    }else{
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【车辆轴距】只能有数字且在2000到6000之间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车辆轴距】只能有数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //运输证字号
                        case 28:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【运输证字号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if(cellValue.getStringValue().matches("[\u4e00-\u9fa5]*交运管[\u4e00-\u9fa5]{1,2}字\\d{6}\\s\\d{6}号$")){
                                    carBizCarInfo.setTransportNumber(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【运输证字号】如下形式：京交运管朝阳字123456 123456号");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆运输证发证机构
                        case 29:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆运输证发证机构】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String certificationAuthority = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setCertificationAuthority(certificationAuthority);
                            }
                            break;
                        //车辆经营区域
                        case 30:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆经营区域】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String operatingRegion = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setOperatingRegion(operatingRegion);
                            }
                            break;
                        //车辆运输证有效期起
                        case 31:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆运输证有效期起】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

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
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【车辆运输证有效期起】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【车辆运输证有效期起】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆运输证有效期止
                        case 32:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆运输证有效期止】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

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
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【车辆运输证有效期止】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【车辆运输证有效期止】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆初次登记日期
                        case 33:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆初次登记日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

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
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【车辆初次登记日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【车辆初次登记日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆检修状态
                        case 34:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆检修状态】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if ("未检修".equals(cellValue.getStringValue())||"已检修".equals(cellValue.getStringValue())||"未知".equals(cellValue.getStringValue())){
                                    // 车辆检修状态
                                    if ("已检修".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setOverHaulStatus(1);
                                    } else if ("未知".equals(cellValue.getStringValue())){
                                        carBizCarInfo.setOverHaulStatus(2);
                                    }else{
                                        carBizCarInfo.setOverHaulStatus(0);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车辆检修状态】为未检修|已检修|未知");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆年度审验状态
                        case 35:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆年度审验状态】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if ("未审验".equals(cellValue.getStringValue())||"合格".equals(cellValue.getStringValue())||"不合格".equals(cellValue.getStringValue())){
                                    // 车辆年度审验状态
                                    if ("合格".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setAuditingStatus(1);
                                    } else if ("不合格".equals(cellValue.getStringValue())){
                                        carBizCarInfo.setAuditingStatus(2);
                                    }else{
                                        carBizCarInfo.setAuditingStatus(0);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车辆年度审验状态】为未审验|合格|不合格");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆年度审验日期
                        case 36:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆年度审验日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

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
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【车辆年度审验日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【车辆年度审验日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //发票打印设备序列号
                        case 37:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【发票打印设备序列号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String equipmentNumber = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setEquipmentNumber(equipmentNumber);
                            }
                            break;
                        //卫星定位装置品牌
                        case 38:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【卫星定位装置品牌】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String gpsBrand = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setGpsBrand(gpsBrand);
                            }
                            break;
                        //卫星定位装置型号
                        case 39:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【卫星定位装置型号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String gpsType = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setGpsType(gpsType);
                            }
                            break;
                        //卫星定位装置IMEI号
                        case 40:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【卫星定位装置IMEI号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String gpsImei = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setGpsImei(gpsImei);
                            }
                            break;
                        //卫星定位设备安装日期
                        case 41:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【卫星定位设备安装日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();

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
                                        returnVO.setReson("第"
                                                + (rowIx + 1)
                                                + "行数据，第"
                                                + (colIx + 1)
                                                + "列 【卫星定位设备安装日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第"
                                            + (rowIx + 1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【卫星定位设备安装日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //所属车主
                        case 42:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【所属车主】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String vehicleOwner = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setVehicleOwner(vehicleOwner);
                            }
                            break;
                        //车辆类型(以机动车行驶证为主)
                        case 43:
                            if (cellValue == null
                                    || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车辆类型(以机动车行驶证为主)】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String vehicleType = Common.replaceBlank(cellValue.getStringValue());
                                carBizCarInfo.setVehicleType(vehicleType);
                            }
                            break;
                    }// switch end

                }// 循环列结束
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
//            result.put("msg", "导入失败！");
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
                        log.info("导入车辆，有重复车牌号，去除，车牌号licensePlates="+yulicensePlates);
                    }else{
                        licensePlatess += yulicensePlates;
                        carList2.add(vo);
                    }
                }
            }
            // 调接口，保存
            Map<String, Object> paramMap = new HashMap<String, Object>();
            JSONArray jsonarray = JSONArray.fromObject(carList2);
            String cars = jsonarray.toString();
            paramMap.put("carList", cars);
            String url = "/webservice/carManage/batchInputCarInfo";
            JSONObject jsonobject = carApiTemplate.postForObject(url,
                    JSONObject.class, paramMap);
            // 返回为0 ==========不成功
            if ((int) jsonobject.get("result") == 0) {
                log.info("接口返回为0 ==========导入不成功");
                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                returnVO.setReson((String) jsonobject.get("exception"));
                listException.add(returnVO);
//                result.put("result", "0");
//                result.put("msg", "导入失败！");
            } else {
                // 返回为1 ==========成功
                JSONObject json = (JSONObject) jsonobject.get("jsonStr");
                // String driverIds = (String)json.get("driverIds");
                String arrayDriverName = (String) json.get("error");
                if (!"".equals(arrayDriverName) && arrayDriverName != null) {
                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                    returnVO.setLicensePlates(licensePlates);
                    returnVO.setReson(arrayDriverName + "导入失败！");
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
        // 创建 excel
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
     * 车辆导出Excel文件导出到本地目录
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
        // 获取本地绝对路径
        String webPath = request.getServletContext().getRealPath("/");

        // 获取配置文件上下文地址
        // 生成文件相对路径
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
        //调接口，保存
        Map<String,Object> paramMap = new HashMap<String,Object>();
        JSONObject json = JSONObject.fromObject(params);
        paramMap.put("carInfo", json);
        String url = "/webservice/carManage/saveCar";
        result = carApiTemplate.postForObject(url,JSONObject.class,paramMap);
        return result;
    }

    @Override
    public Workbook exportExcel(CarInfo params, String path)
            throws Exception {
        FileInputStream io = new FileInputStream(path);
        Workbook wb = new XSSFWorkbook(io);
        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(), ",");
        String teamIds = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(), ",");
        String carModelIds = "";
        if(!"".equals(params.getCities())&&params.getCities()!=null&&!"null".equals(params.getCities())){
            cities = params.getCities().replace(";", ",");
        }
        if(!"".equals(params.getSupplierIds())&&params.getSupplierIds()!=null&&!"null".equals(params.getSupplierIds())){
            suppliers = params.getSupplierIds().replace(";", ",");
        }
        if(!"".equals(params.getCarModelIds())&&params.getCarModelIds()!=null&&!"null".equals(params.getCarModelIds())){
            carModelIds = params.getCarModelIds().replace(";", ",");
        }
        params.setCarModelIds(carModelIds);
        params.setSupplierIds(suppliers);
        params.setCities(cities);
        params.setTeamIds(teamIds);
        List<CarInfo> list = carInfoExMapper.selectListNoPage(params);

        if(list != null && list.size()>0){
            Sheet sheet = wb.getSheetAt(0);
            Cell cell;
            int i=0;
            List<Integer> createIds = new ArrayList<>();
            List<Integer> updateIds = new ArrayList<>();
            for(CarInfo s:list){
                if(s.getCreateBy()!=null && StringUtils.isBlank(s.getCreateName())){
                    createIds.add(s.getCreateBy());
                }
                if(s.getUpdateBy()!=null && StringUtils.isBlank(s.getUpdateName())){
                    updateIds.add(s.getUpdateBy());
                }
            }
            List<CarAdmUser>  creator = userManagementService.getUsersByIdList(createIds);
            List<CarAdmUser>  updator = userManagementService.getUsersByIdList(updateIds);
            for (CarInfo s : list){
                if (s.getCreateBy()!=null && StringUtils.isBlank(s.getCreateName())){
                    for (CarAdmUser user : creator){
                        if ((s.getCreateBy()).equals(user.getUserId())){
                            s.setCreateName(user.getUserName());
                            break;
                        }
                    }
                }
                if (s.getUpdateBy()!=null && StringUtils.isBlank(s.getUpdateName())){
                    for (CarAdmUser user : updator){
                        if ((s.getUpdateBy()).equals(user.getUserId())){
                            s.setUpdateName(user.getUserName());
                            break;
                        }
                    }
                }
                Row row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(s.getLicensePlates());

                cell = row.createCell(1);
                cell.setCellValue(s.getCityName());

                cell = row.createCell(2);
                cell.setCellValue(s.getStatus()==1?"有效":"无效");

                cell = row.createCell(3);
                cell.setCellValue(s.getSupplierName());

                cell = row.createCell(4);
                cell.setCellValue(s.getModeName());

                cell = row.createCell(5);
                cell.setCellValue(s.getModelDetail());

                cell = row.createCell(6);
                cell.setCellValue(s.getPurchaseDate());

                cell = row.createCell(7);
                cell.setCellValue(s.getColor());

                cell = row.createCell(8);
                cell.setCellValue(s.getEngineNo());

                cell = row.createCell(9);
                cell.setCellValue(s.getFrameNo());

                cell = row.createCell(10);
                cell.setCellValue(s.getNextInspectDate());

                cell = row.createCell(11);
                cell.setCellValue(s.getNextMaintenanceDate());

                cell = row.createCell(12);
                cell.setCellValue(s.getRentalExpireDate());

                cell = row.createCell(13);
                cell.setCellValue(s.getNextClassDate());

                cell = row.createCell(14);
                cell.setCellValue(s.getNextOperationDate());

                cell = row.createCell(15);
                cell.setCellValue(s.getNextSecurityDate());

                cell = row.createCell(16);
                cell.setCellValue(s.getTwoLevelMaintenanceDate());

                cell = row.createCell(17);
                cell.setCellValue(s.getCarryPassengers());

                cell = row.createCell(18);
                cell.setCellValue(s.getVehicleBrand());

                cell = row.createCell(19);
                cell.setCellValue(s.getClicensePlatesColor());

                cell = row.createCell(20);
                cell.setCellValue(s.getVehicleVINCode());

                cell = row.createCell(21);
                cell.setCellValue(s.getVehicleRegistrationDate());

                cell = row.createCell(22);
                Integer fuelType = s.getOverHaulStatus();
                String  fuelTypeStr = "无";
                if(null!=fuelType && fuelType==1){
                    fuelTypeStr = "汽油";
                }else if(null!=fuelType && fuelType==2){
                    fuelTypeStr = "柴油";
                }else if(null!=fuelType && fuelType==3){
                    fuelTypeStr = "电";
                }else if(null!=fuelType && fuelType==4){
                    fuelTypeStr = "混合油";
                }else if(null!=fuelType && fuelType==5){
                    fuelTypeStr = "天然气";
                }else if(null!=fuelType && fuelType==6){
                    fuelTypeStr = "液化石油气";
                }else if(null!=fuelType && fuelType==7){
                    fuelTypeStr = "甲醇";
                }else if(null!=fuelType && fuelType==8){
                    fuelTypeStr = "乙醇";
                }else if(null!=fuelType && fuelType==9){
                    fuelTypeStr = "太阳能";
                }else if(null!=fuelType && fuelType==10){
                    fuelTypeStr = "混合动力";
                }
                cell.setCellValue(fuelTypeStr);

                cell = row.createCell(23);
                cell.setCellValue(s.getVehicleEngineDisplacement());

                cell = row.createCell(24);
                cell.setCellValue(s.getVehicleEnginePower());

                cell = row.createCell(25);
                cell.setCellValue(s.getVehicleEngineWheelbase());

                cell = row.createCell(26);
                cell.setCellValue(s.getTransportNumber());

                cell = row.createCell(27);
                cell.setCellValue(s.getCertificationAuthority());

                cell = row.createCell(28);
                cell.setCellValue(s.getOperatingRegion());

                cell = row.createCell(29);
                cell.setCellValue(s.getTransportNumberDateStart());

                cell = row.createCell(30);
                cell.setCellValue(s.getTransportNumberDateEnd());

                cell = row.createCell(31);
                cell.setCellValue(s.getFirstDate());

                cell = row.createCell(32);
                Integer overHaulStatus = s.getOverHaulStatus();
                String  statusStr = "未知";
                if(null!=overHaulStatus && overHaulStatus==1){
                    statusStr = "已检修";
                }else if(null!=overHaulStatus && overHaulStatus==0){
                    statusStr = "未检修";
                }
                cell.setCellValue(statusStr);
                cell = row.createCell(33);
                Integer auditingStatus = s.getAuditingStatus();
                String  auditingStatusStr = "未审验";
                if(null!=auditingStatus && auditingStatus==1){
                    auditingStatusStr = "合格";
                }else if(null!=auditingStatus && auditingStatus==2){
                    auditingStatusStr = "不合格";
                }
                cell.setCellValue(auditingStatusStr);
                cell = row.createCell(34);
                cell.setCellValue(s.getAuditingDate());

                cell = row.createCell(35);
                cell.setCellValue(s.getEquipmentNumber());

                cell = row.createCell(36);
                cell.setCellValue(s.getGpsBrand());

                cell = row.createCell(37);
                cell.setCellValue(s.getGpsType());

                cell = row.createCell(38);
                cell.setCellValue(s.getGpsImei());

                cell = row.createCell(39);
                cell.setCellValue(s.getGpsDate());

                cell = row.createCell(40);
                cell.setCellValue(s.getCreateName());

                cell = row.createCell(41);
                cell.setCellValue(s.getCreateDate());

                cell = row.createCell(42);
                cell.setCellValue(s.getUpdateName());

                cell = row.createCell(43);
                cell.setCellValue(s.getUpdateDate());

                cell = row.createCell(44);
                cell.setCellValue(s.getMemo());

                cell = row.createCell(45);
                cell.setCellValue(s.getDriverName());

                cell = row.createCell(46);
                cell.setCellValue(s.getVehicleOwner());

                cell = row.createCell(47);
                cell.setCellValue(s.getVehicleType());

                i++;
            }
        }
        return wb;
    }

    @Override
    public CarInfo selectCarInfoByCarId(CarInfo params) {
        return carInfoExMapper.selectCarInfoByCarId(params);
    }

    @Override
    public String selectCarByCarId(Integer params) {
        return carInfoExMapper.selectCarByCarId(params);
    }

    //汉字
    public boolean isChineseLanguage(String str){
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    //运输证字号
    public boolean isTransportNumber(String str){
        Pattern pattern = Pattern.compile("/^(^[\u4e00-\u9fa5]交运管[\u4e00-\u9fa5]{1,2}字\\d{6}\\s\\d{6}号$)$/");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    //数字
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
        String resultError1 = "-1";// 模板错误
        String resultErrorMag1 = "导入模板格式错误!";

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
            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            if(row1==null){
                result.put("result", resultError1);
                result.put("msg", resultErrorMag1);
                return result;
            }
            for (int colIx = 0; colIx < 1; colIx++) {
                Cell cell = row1.getCell(colIx); // 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    result.put("result", resultError1);
                    result.put("msg", resultErrorMag1);
                    return result;
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("车牌号")) {
                                result.put("result", resultError1);
                                result.put("msg", resultErrorMag1);
                                return result;
                            }
                            break;
                    }
                }
            }
            int minRowIx = 1;// 过滤掉标题，从第一行开始导入数据
            int maxRowIx = sheet.getLastRowNum(); // 要导入数据的总条数
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // 获取行对象
                if (row == null) {
                    continue;
                }
                // 车辆导入删除模板总共1 列
                for (int colIx = 0; colIx < 1; colIx++) {
                    Cell cell = row.getCell(colIx); // 获取列对象
                    CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                    switch ((colIx + 1)) {
                        // 车牌号
                        case 1:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(null);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"+ (colIx + 1) + "列 【车牌号】不能为空且单元格格式必须为文本");
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
                                    returnVO.setReson("第" + (rowIx + 1)+ "行数据，第" + (colIx + 1)+ "列 【车牌号】不存在");
                                    listException.add(returnVO);
                                    continue;
                                }else if(carInfoEntity.getIsFree()==1){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1)+ "行数据，第" + (colIx + 1)+ "列 【车牌号】正在运营中，不可删除");
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
                }// 循环列结束
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String download = "";
        try {
            // 将错误列表导出
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

    @Override
    public void getExportExcel(CarInfo params, List<String> datas) {
        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(), ",");
        String teamIds = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(), ",");
        String carModelIds = "";
        if(!"".equals(params.getCities())&&params.getCities()!=null&&!"null".equals(params.getCities())){
            cities = params.getCities().replace(";", ",");
        }
        if(!"".equals(params.getSupplierIds())&&params.getSupplierIds()!=null&&!"null".equals(params.getSupplierIds())){
            suppliers = params.getSupplierIds().replace(";", ",");
        }
        if(!"".equals(params.getCarModelIds())&&params.getCarModelIds()!=null&&!"null".equals(params.getCarModelIds())){
            carModelIds = params.getCarModelIds().replace(";", ",");
        }
        params.setCarModelIds(carModelIds);
        params.setSupplierIds(suppliers);
        params.setCities(cities);
        params.setTeamIds(teamIds);
        List<CarInfo> list = carInfoExMapper.selectListNoPage(params);
        StringBuilder builder = new StringBuilder();
        if(list != null && list.size()>0){
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
            List<CarAdmUser>  creator = userManagementService.getUsersByIdList(createIds);
            List<CarAdmUser>  updator = userManagementService.getUsersByIdList(updateIds);
            for (CarInfo carInfo : list){
                if (carInfo.getCreateBy()!=null && StringUtils.isBlank(carInfo.getCreateName())){
                    for (CarAdmUser user : creator){
                        if ((carInfo.getCreateBy()).equals(user.getUserId())){
                            carInfo.setCreateName(user.getUserName());
                            break;
                        }
                    }
                }
                if (carInfo.getUpdateBy()!=null && StringUtils.isBlank(carInfo.getUpdateName())){
                    for (CarAdmUser user : updator){
                        if ((carInfo.getUpdateBy()).equals(user.getUserId())){
                            carInfo.setUpdateName(user.getUserName());
                            break;
                        }
                    }
                }
                builder.append(carInfo.getLicensePlates() == null ? "" : carInfo.getLicensePlates()).append(",");
                builder.append(carInfo.getCityName() == null ? "" : carInfo.getCityName()).append(",");
                builder.append(carInfo.getStatus()==1?"有效":"无效").append(",");
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
                String  fuelTypeStr = "无";
                if(null!=fuelType && fuelType==1){
                    fuelTypeStr = "汽油";
                }else if(null!=fuelType && fuelType==2){
                    fuelTypeStr = "柴油";
                }else if(null!=fuelType && fuelType==3){
                    fuelTypeStr = "电";
                }else if(null!=fuelType && fuelType==4){
                    fuelTypeStr = "混合油";
                }else if(null!=fuelType && fuelType==5){
                    fuelTypeStr = "天然气";
                }else if(null!=fuelType && fuelType==6){
                    fuelTypeStr = "液化石油气";
                }else if(null!=fuelType && fuelType==7){
                    fuelTypeStr = "甲醇";
                }else if(null!=fuelType && fuelType==8){
                    fuelTypeStr = "乙醇";
                }else if(null!=fuelType && fuelType==9){
                    fuelTypeStr = "太阳能";
                }else if(null!=fuelType && fuelType==10){
                    fuelTypeStr = "混合动力";
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
                String  statusStr = "未知";
                if(null!=overHaulStatus && overHaulStatus==1){
                    statusStr = "已检修";
                }else if(null!=overHaulStatus && overHaulStatus==0){
                    statusStr = "未检修";
                }
                builder.append(statusStr).append(",");
                Integer auditingStatus = carInfo.getAuditingStatus();
                String  auditingStatusStr = "未审验";
                if(null!=auditingStatus && auditingStatus==1){
                    auditingStatusStr = "合格";
                }else if(null!=auditingStatus && auditingStatus==2){
                    auditingStatusStr = "不合格";
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
                builder.append(carInfo.getVehicleType() == null ? "" : carInfo.getVehicleType());
                datas.add(builder.toString());
                builder.setLength(0);
            }
        }
    }
}
