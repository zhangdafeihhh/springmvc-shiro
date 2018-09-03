package com.zhuanche.serv.deiver;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import com.zhuanche.entity.mdbcarmanage.DriverVoEntity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.CarImportExceptionEntity;
import com.zhuanche.shiro.constants.BusConstant;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import com.zhuanche.util.Common;
import mapper.mdbcarmanage.CarBizCarInfoTempMapper;
import mapper.mdbcarmanage.ex.CarBizCarInfoTempExMapper;
import mapper.mdbcarmanage.ex.CarBizDriverInfoTempExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizModelExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wzq
 */
@Service
public class CarBizCarInfoTempService {

    private static Log log =  LogFactory.getLog(CarBizCarInfoTempService.class);

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static String REGULAR_NUMBER = "[0-9]*";
    private static String REGULAR_FRAME = "(^[a-zA-Z0-9\\-]{13}$)|(^[a-zA-Z0-9\\-]{17}$)";

    @Autowired
    private CarBizCarInfoTempExMapper carBizCarInfoTempExMapper;

    @Autowired
    private CarBizCarInfoTempMapper carBizCarInfoTempMapper;

    @Autowired
    private CarBizDriverInfoTempExMapper carBizDriverInfoTempExMapper;

    @Autowired
    private CarBizModelExMapper carBizModelExMapper;

    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;
    /**
     * 分页查询
     * @param params
     * @return
     */
    public List<CarBizCarInfoTemp> queryForPageObject(Map<String,Object> params){
        return carBizCarInfoTempExMapper.queryForPageObject(params);
    }

    /**
     * 查询详情
     * @param params
     * @return
     */
    public CarBizCarInfoTemp queryForObject(Map<String,Object> params){
        return carBizCarInfoTempExMapper.queryForObject(params);
    }

    /**
     * 车辆删除
     * @param carIds 车辆Id
     */
    public int delete(String carIds) {
        int code  = 0;
        String[] carIdsArray = carIds.split(",");
        for (int i = 0; i < carIdsArray.length; i++) {
            code  = carBizCarInfoTempMapper.deleteByPrimaryKey(Integer.parseInt(carIdsArray[i]));
            log.info("车辆删除Id:"+carIdsArray[i]+"返回code:"+code);
        }
        return code;
    }

    /**
     * 新增
     * @param carBizCarInfoTemp
     * @return
     */
    public int add(CarBizCarInfoTemp carBizCarInfoTemp) {
        String license = carBizCarInfoTemp.getLicensePlates();
        license = Check.replaceBlank(license);
        carBizCarInfoTemp.setLicensePlates(license);
        return carBizCarInfoTempMapper.insertSelective(carBizCarInfoTemp);
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    public AjaxResponse update(CarBizCarInfoTemp entity) {
        try{
            carBizCarInfoTempMapper.updateByPrimaryKeySelective(entity);
            DriverVoEntity carDriver = carBizDriverInfoTempExMapper.getDriverByLincesePlates(entity.getLicensePlates1());
            if (carDriver != null) {
                int had = 0;
                // 车辆是否更改城市，更改，则修改司机
                if (entity.getOldCity() != null && !"".equals(entity.getOldCity()) && !entity.getCityId().equals(entity.getOldCity())) {
                    had = 1;
                    carDriver.setServiceCity(entity.getCities());
                    carDriver.setSupplierId(entity.getSupplierId());
                    carDriver.setTeamid("");
                    carDriver.setGroupIds("");
                }
                if (entity.getOldSupplierId() != null && !"".equals(entity.getOldSupplierId()) && !entity.getSupplierId().equals(entity.getOldSupplierId())) {
                    // 车辆是否更改供应商，更改，则修改司机
                    had = 1;
                    carDriver.setSupplierId(entity.getSupplierId());
                    carDriver.setTeamid("");
                    carDriver.setGroupIds("");
                }
                if (!entity.getLicensePlates().equals(entity.getLicensePlates1())) {
                    had = 1;
                    carDriver.setLicensePlates(entity.getLicensePlates());
                }
                if (had == 1) {
                    SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
                    carDriver.setUpdateBy(user.getId());
                    carBizDriverInfoTempExMapper.update(carDriver);
                }
            }
            return AjaxResponse.success(RestErrorCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            log.error("修改出现异常,修改Id:"+entity.getCarId());
            return AjaxResponse.success(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    public AjaxResponse importCarInfo(CarBizCarInfoTemp params,HttpServletRequest request) {
        String resultErrorMag1 = "导入模板格式错误!";

        String licensePlates = "";
        Map<String, Object> result = new HashMap<String, Object>();
        List<CarImportExceptionEntity> listException = new ArrayList<CarImportExceptionEntity>();
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        String fileName = params.getFileName();
        String path = Common.getPath(request);
        String dirPath = path + params.getFileName();
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
            FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            if (row1 == null) {
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
            }
            for (int colIx = 0; colIx < 43; colIx++) {
                Cell cell = row1.getCell(colIx); // 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("序号")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 2:
                            if (!cellValue.getStringValue().contains("车牌号")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 3:
                            if (!cellValue.getStringValue().contains("城市")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 4:
                            if (!cellValue.getStringValue().contains("状态")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 5:
                            if (!cellValue.getStringValue().contains("供应商")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 6:
                            if (!cellValue.getStringValue().contains("车型")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 7:
                            if (!cellValue.getStringValue().contains("具体车型")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 8:
                            if (!cellValue.getStringValue().contains("购买日期")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 9:
                            if (!cellValue.getStringValue().contains("颜色")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 10:
                            if (!cellValue.getStringValue().contains("发动机号")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 11:
                            if (!cellValue.getStringValue().contains("车架号")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 12:
                            if (!cellValue.getStringValue().contains("下次维保时间")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 13:
                            if (!cellValue.getStringValue().contains("下次等级鉴定时间")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 14:
                            if (!cellValue.getStringValue().contains("下次检营运证时间")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 15:
                            if (!cellValue.getStringValue().contains("下次检治安证时间")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 16:
                            if (!cellValue.getStringValue().contains("二次维护时间")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 17:
                            if (!cellValue.getStringValue().contains("租赁到期时间")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 18:
                            if (!cellValue.getStringValue().contains("下次车检时间")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 19:
                            if (!cellValue.getStringValue().contains("核定载客位")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 20:
                            if (!cellValue.getStringValue().contains("车辆厂牌")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 21:
                            if (!cellValue.getStringValue().contains("车牌颜色")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 22:
                            if (!cellValue.getStringValue().contains("车辆VIN码")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 23:
                            if (!cellValue.getStringValue().contains("车辆注册日期")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 24:
                            if (!cellValue.getStringValue().contains("车辆燃料类型")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 25:
                            if (!cellValue.getStringValue().contains("发动机排量（毫升）")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 26:
                            if (!cellValue.getStringValue().contains("发动机功率（千瓦）")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 27:
                            if (!cellValue.getStringValue().contains("车辆轴距（毫米）")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 28:
                            if (!cellValue.getStringValue().contains("运输证字号")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 29:
                            if (!cellValue.getStringValue().contains("车辆运输证发证机构")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 30:
                            if (!cellValue.getStringValue().contains("车辆经营区域")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 31:
                            if (!cellValue.getStringValue().contains("车辆运输证有效期起")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 32:
                            if (!cellValue.getStringValue().contains("车辆运输证有效期止")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 33:
                            if (!cellValue.getStringValue().contains("车辆初次登记日期")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 34:
                            if (!cellValue.getStringValue().contains("车辆检修状态")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 35:
                            if (!cellValue.getStringValue().contains("车辆年度审验状态")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 36:
                            if (!cellValue.getStringValue().contains("车辆年度审验日期")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 37:
                            if (!cellValue.getStringValue().contains("发票打印设备序列号")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 38:
                            if (!cellValue.getStringValue().contains("卫星定位装置品牌")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 39:
                            if (!cellValue.getStringValue().contains("卫星定位装置型号")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 40:
                            if (!cellValue.getStringValue().contains("卫星定位装置IMEI号")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 41:
                            if (!cellValue.getStringValue().contains("卫星定位设备安装日期")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 42:
                            if (!cellValue.getStringValue().contains("所属车主")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                        case 43:
                            if (!cellValue.getStringValue().contains("车辆类型(以机动车行驶证为主)")) {
                                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                            }
                            break;
                    }
                }
            }

            int minRowIx = 1;// 过滤掉标题，从第一行开始导入数据
            int maxRowIx = sheet.getLastRowNum(); // 要导入数据的总条数
            // int successCount = 0;// 成功导入条数

            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // 获取行对象
                if (row == null) {
                    continue;
                }
                CarBizCarInfoTemp carBizCarInfo = new CarBizCarInfoTemp();
                boolean isTrue = true;// 标识是否为有效数据
                // 车辆状态，创建人
                carBizCarInfo.setStatus(1);
                carBizCarInfo.setCreateBy(user.getId());
                carBizCarInfo.setUpdateBy(user.getId());
                carBizCarInfo.setCityId(params.getCityId());
                carBizCarInfo.setSupplierId(params.getSupplierId());
                // 车辆导入模板总共41 列
                for (int colIx = 0; colIx < 43; colIx++) {
                    Cell cell = row.getCell(colIx); // 获取列对象
                    CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                    if ((colIx + 1) == 2) {
                        if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                        } else {
                            licensePlates = cellValue.getStringValue();
                        }
                    }
                    switch ((colIx + 1)) {
                        // 车牌号
                        case 1:
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
                                // 判断车牌号 是否存在
                                if (license.length() == 7 || license.length() == 8) {
                                    Map<String, Object> resultCar = this.checkLicensePlates(license);
                                    if ((int) resultCar.get("result") == 0) {
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
                            break;
                        // 状态
                        case 4:
                            break;
                        // 供应商
                        case 5:
                            break;
                        // 车型
                        case 6:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车型】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // 判断车型是否存在
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
                                String modelDetail = Common.replaceBlank(cellValue.getStringValue());
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
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() > sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                                if (cellValue.getStringValue().matches("[\u4e00-\u9fa5]*")) {
                                    carBizCarInfo.setColor(cellValue.getStringValue());
                                } else {
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
                                String engineNo = Common.replaceBlank(cellValue.getStringValue());
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
                                if (Common.isRegular(cellValue.getStringValue(), REGULAR_FRAME)) {
                                    carBizCarInfo.setFrameNo(cellValue.getStringValue());
                                    carBizCarInfo.setVehicleVINCode(cellValue.getStringValue());
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车架号】为13位或17位数字字母组合");
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

                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                            }
                            break;
                        // 下次等级鉴定时间
                        case 13:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                                } else {
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
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                                } else {
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
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                                } else {
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
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-").replace("月", "-")
                                        .replace("日", "-").replace(".", "-").trim();
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                                } else {
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
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() < sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                                if (Common.isRegular(cellValue.getStringValue(), REGULAR_NUMBER)) {
                                    carBizCarInfo.setCarryPassengers(cellValue.getStringValue());
                                } else {
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
                                if (cellValue.getStringValue().matches("[\u4e00-\u9fa5]*")) {
                                    carBizCarInfo.setClicensePlatesColor(cellValue.getStringValue());
                                } else {
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
                                if (Common.isRegular(cellValue.getStringValue(), REGULAR_FRAME)) {
                                    //carBizCarInfo.setVehicleVINCode(cellValue.getStringValue());
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车辆VIN码】与车架号一致为13位或17位数字字母组合");
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
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String datetime = dateFormat.format(new Date());
                                        if (sdf.parse(d).getTime() > sdf.parse(datetime).getTime()) {
                                            CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                            returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
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
                                } else if ("柴油".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(2);
                                } else if ("电".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(3);
                                } else if ("混合油".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(4);
                                } else if ("天然气".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(5);
                                } else if ("液化石油气".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(6);
                                } else if ("甲醇".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(7);
                                } else if ("乙醇".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(8);
                                } else if ("太阳能".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(9);
                                } else if ("混合动力".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(10);
                                } else if ("无".equals(fuelType)) {
                                    carBizCarInfo.setFuelType(11);
                                } else {
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
                                if (Common.isNumeric(cellValue.getStringValue())) {
                                    if (Integer.valueOf(cellValue.getStringValue()) >= 700 && Integer.valueOf(cellValue.getStringValue()) <= 5000) {
                                        carBizCarInfo.setVehicleEngineDisplacement(cellValue.getStringValue());
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【车辆发动机排量】只能有数字且在700到5000之间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                } else {
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
                                if (Common.isNumeric(cellValue.getStringValue())) {
                                    if (Integer.valueOf(cellValue.getStringValue()) >= 50 && Integer.valueOf(cellValue.getStringValue()) <= 600) {
                                        carBizCarInfo.setVehicleEnginePower(cellValue.getStringValue());
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【发动机功率】只能有数字且在50到600之间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                } else {
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
                                if (Common.isNumeric(cellValue.getStringValue())) {
                                    if (Integer.valueOf(cellValue.getStringValue()) >= 2000 && Integer.valueOf(cellValue.getStringValue()) <= 6000) {
                                        carBizCarInfo.setVehicleEngineWheelbase(cellValue.getStringValue());
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                                + (colIx + 1) + "列 【车辆轴距】只能有数字且在2000到6000之间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                } else {
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
                                if (cellValue.getStringValue().matches("[\u4e00-\u9fa5]*交运管[\u4e00-\u9fa5]{1,2}字\\d{6}\\s\\d{6}号$")) {
                                    carBizCarInfo.setTransportNumber(cellValue.getStringValue());
                                } else {
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
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
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
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
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
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
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
                                if ("未检修".equals(cellValue.getStringValue()) || "已检修".equals(cellValue.getStringValue()) || "未知".equals(cellValue.getStringValue())) {
                                    // 车辆检修状态
                                    if ("已检修".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setOverHaulStatus(1);
                                    } else if ("未知".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setOverHaulStatus(2);
                                    } else {
                                        carBizCarInfo.setOverHaulStatus(0);
                                    }
                                } else {
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
                                if ("未审验".equals(cellValue.getStringValue()) || "合格".equals(cellValue.getStringValue()) || "不合格".equals(cellValue.getStringValue())) {
                                    // 车辆年度审验状态
                                    if ("合格".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setAuditingStatus(1);
                                    } else if ("不合格".equals(cellValue.getStringValue())) {
                                        carBizCarInfo.setAuditingStatus(2);
                                    } else {
                                        carBizCarInfo.setAuditingStatus(0);
                                    }
                                } else {
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
                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
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

                                if (StringUtils.isNotEmpty(d) && d.length() > 2) {
                                    if (d.substring(d.length() - 1).equals("-")) {
                                        d = d.substring(0, d.length() - 1);
                                    }
                                    if (Common.isValidDate(d)) {
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
                    try {
                        carBizCarInfoTempMapper.insertSelective(carBizCarInfo);
                        return AjaxResponse.success(RestErrorCode.SUCCESS);
                    } catch (Exception e) {
                        log.info("导入车辆保存  error：" + e);
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setLicensePlates(licensePlates);
                        returnVO.setReson(carBizCarInfo.getLicensePlates() + "导入失败！");
                        listException.add(returnVO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String download = "";
        try {
            // 将错误列表导出
            if (listException.size() > 0) {
                Workbook wb = Common.exportExcel(request.getServletContext().getRealPath("/") + "template" + File.separator + "car_exception.xlsx", listException);
                download = Common.exportExcelFromTempletToLoacl(request, wb,
                        new String("ERROR".getBytes("utf-8"), "iso8859-1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"".equals(download) && download != null) {
            result.put("download", download);
        } else {
            result.put("download", "");
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,download);
    }

    /**
     * 判断车牌号是否存在
     * @param params
     * @return
     */
    private Map<String, Object> checkLicensePlates(String params) {
        Map<String, Object> result = new HashMap<String, Object>();
        int n = carBizCarInfoExMapper.checkLicensePlates(params);
        if (n > 0) {
            result.put("result", 0);
            result.put("msg", "车牌号已存在");
        } else {
            n = carBizDriverInfoTempExMapper.checkLicensePlates(params);
            if (n > 0) {
                result.put("result", 0);
                result.put("msg", "车牌号已存在");
            } else {
                result.put("result", 1);
                result.put("msg", "车牌号可以使用");
            }
        }
        return result;
    }

    public AjaxResponse importCarInfo4Bus(CarBizCarInfoTemp params, HttpServletRequest request) {
        log.info("车辆（巴士）信息导入");
        String resultErrorMag1 = "导入模板格式错误!";
        String licensePlates = "";
        Map<String, Object> result = new HashMap<String, Object>();
        List<CarImportExceptionEntity> listException = new ArrayList<CarImportExceptionEntity>();
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        String fileName = params.getFileName();
        String path = Common.getPath(request);
        String dirPath = path + params.getFileName();
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
            FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            if (row1 == null) {
                log.error("车辆（巴士）信息导入，excel表头为空");
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
            }
            if (row1.getLastCellNum() != BusConstant.BusExcel.BUS_EXCLE_TITLE.length) {
                log.error("车辆（巴士）信息导入，excel表头个数错误");
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
            }
            for (int colIx = 0; colIx < row1.getLastCellNum(); colIx++) {
                Cell cell = row1.getCell(colIx); // 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                } else {
                    String stringValue = cellValue.getStringValue();
                    if (!stringValue.contains(BusConstant.BusExcel.BUS_EXCLE_TITLE[colIx])) {
                        log.error("车辆（巴士）信息导入，excel表头有错误，错误表头=" + stringValue);
                        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                    }
                }
            }
            int minRowIx = 1;// 过滤掉标题，从第一行开始导入数据
            int maxRowIx = sheet.getLastRowNum(); // 要导入数据的总条数
            // int successCount = 0;// 成功导入条数
            log.info("车辆（巴士）信息导入，总条数=" + maxRowIx);
            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // 获取行对象
                if (row == null) {
                    continue;
                }
                CarBizCarInfoTemp entity = new CarBizCarInfoTemp();
                boolean isTrue = true;// 标识是否为有效数据
                // 车辆状态，创建人
                entity.setStatus(1);
                entity.setCreateBy(user.getId());
                entity.setUpdateBy(user.getId());
                entity.setCityId(params.getCityId());
                entity.setSupplierId(params.getSupplierId());
                // 车辆导入模板总共41 列
                for (int colIx = 0; colIx < row1.getLastCellNum(); colIx++) {
                    Cell cell = row.getCell(colIx); // 获取列对象
                    CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                    if (colIx == 3) {
                        if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                        } else {
                            licensePlates = cellValue.getStringValue();
                        }
                    }
                    switch (colIx) {
                        // 车牌号
                        case 3:
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
                                // 判断车牌号 是否存在
                                if (license.length() == 7 || license.length() == 8) {
                                    Map<String, Object> resultCar = this.checkLicensePlates(license);
                                    if ((int) resultCar.get("result") == 0) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setLicensePlates(licensePlates);
                                        returnVO.setReson("第" + (rowIx + 1)
                                                + "行数据，第" + (colIx + 1)
                                                + "列 【车牌号】已存在");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        entity.setLicensePlates(license);
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
                        // 车辆厂牌
                        case 4:
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
                                entity.setVehicleBrand(vehicleBrand);
                            }
                            break;
                        //核定载客数
                        case 5:
                            //数字
                            String carryPassengers = null;
                            if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC && cell.getNumericCellValue() != 0) {
                                carryPassengers = String.valueOf((int) cell.getNumericCellValue());
                                //字符串
                            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                                carryPassengers = cell.getStringCellValue();
                            }
                            if (cellValue == null || StringUtils.isEmpty(carryPassengers)) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【核定载客位】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                if (Common.isRegular(carryPassengers, REGULAR_NUMBER)) {
                                    entity.setCarryPassengers(carryPassengers);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【核定载客位】只能为数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //颜色
                        case 6:
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
                                if (cellValue.getStringValue().matches("[\u4e00-\u9fa5]*")) {
                                    entity.setColor(cellValue.getStringValue());
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【颜色】只能为汉字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车辆燃料类型
                        case 7:
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
                                    entity.setFuelType(1);
                                } else if ("柴油".equals(fuelType)) {
                                    entity.setFuelType(2);
                                } else if ("电".equals(fuelType)) {
                                    entity.setFuelType(3);
                                } else if ("混合油".equals(fuelType)) {
                                    entity.setFuelType(4);
                                } else if ("天然气".equals(fuelType)) {
                                    entity.setFuelType(5);
                                } else if ("液化石油气".equals(fuelType)) {
                                    entity.setFuelType(6);
                                } else if ("甲醇".equals(fuelType)) {
                                    entity.setFuelType(7);
                                } else if ("乙醇".equals(fuelType)) {
                                    entity.setFuelType(8);
                                } else if ("太阳能".equals(fuelType)) {
                                    entity.setFuelType(9);
                                } else if ("混合动力".equals(fuelType)) {
                                    entity.setFuelType(10);
                                } else if ("无".equals(fuelType)) {
                                    entity.setFuelType(11);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【车辆燃料类型】为汽油|柴油|电|混合油|天然气|液化石油气|甲醇|乙醇|太阳能|混合动力|无");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //运输证字号
                        case 8:
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
                                if (cellValue.getStringValue().matches("[\u4e00-\u9fa5]*交运管[\u4e00-\u9fa5]{1,2}字\\d{6}\\s\\d{6}号$")) {
                                    entity.setTransportNumber(cellValue.getStringValue());
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【运输证字号】如下形式：京交运管朝阳字123456 123456号");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //车型
                        case 9:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setLicensePlates(licensePlates);
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【车型】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // 判断车型是否存在
                                System.out.println(cellValue.getStringValue());
                                Integer carModelId = carBizModelExMapper.queryCarModelByCarModelName(cellValue.getStringValue().trim());
                                if (carModelId == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setLicensePlates(licensePlates);
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 【车型】无效");
                                    listException.add(returnVO);
                                    isTrue = false;
                                } else {
                                    entity.setCarModelId(carModelId);
                                    entity.setModelDetail(cellValue.getStringValue());
                                }
                            }
                            break;
                        default:
                            break;
                    }// switch end

                }// 循环列结束
                if (isTrue && entity != null) {
//					carList.add(carBizCarInfo);
                    //车牌颜色(默认)
                    entity.setClicensePlatesColor("黑色");
                    //发动机号(默认)
                    entity.setEngineNo("L360A700059");
                    //发动机排量（默认）
                    entity.setVehicleEngineDisplacement(String.valueOf(5000));
                    //发动机功率(默认)
                    entity.setVehicleEnginePower("600");
                    //车架号(默认)
                    entity.setFrameNo("LA9LA2E35EBBFC582");
                    //车辆轴距（默认）
                    entity.setVehicleEngineWheelbase("6000");
                    //所属车主(供应商保持一直)
                    CarBizSupplier carBizSupplier = new CarBizSupplier();
                    carBizSupplier.setSupplierId(params.getSupplierId());
                    CarBizSupplier resultSupplier = carBizSupplierExMapper.queryForObject(carBizSupplier);
                    if (resultSupplier != null && resultSupplier.getSupplierFullName() != null) {
                        entity.setVehicleOwner(resultSupplier.getSupplierFullName());
                    }
                    //车辆类型（默认）
                    entity.setVehicleType("car54856656");
                    //下次车检时间（可以为空）
                    //下次维保时间(可以为空字段)
                    //下次运营检测时间（可以为空）
                    //下次治安证检测时间（可以为空）
                    //下次等级验证时间（可以为空）
                    //二级维护时间（可以为空）
                    //租赁到期时间（可以为空）
                    //购买时间（可以为空）
                    //车辆注册时间(填写默认值)
                    entity.setVehicleRegistrationDate("2012-5-10");
                    //车辆运输证发证机构
                    entity.setCertificationAuthority("道路运输管理局");
                    //车辆经营区域
                    entity.setOperatingRegion("全国");
                    //车辆运输证有效期起
                    entity.setTransportNumberDateStart("2016-6-1");
                    //车辆运输证有效期止
                    entity.setTransportNumberDateEnd("2026-6-1");
                    //车辆初次登记日期
                    entity.setFirstDate("2016-6-1");
                    //车辆检修状态(已检修)
                    entity.setOverHaulStatus(1);
                    //车辆年度审验状态（合格）
                    entity.setAuditingStatus(1);
                    //车辆年审验日期
                    entity.setAuditingDate("2016-6-1");
                    //发票打印设备序列号
                    entity.setEquipmentNumber("asf54asd8564688");
                    //卫星定位装置品牌
                    entity.setGpsBrand("brand");
                    //卫星定位装置型号
                    entity.setGpsType("vasmd");
                    //卫星定位装置IMEI号
                    entity.setGpsImei("imeiv2015");
                    //卫星定位设备安装日期
                    entity.setGpsDate("2016-6-1");
                    //默认有效
                    entity.setStatus(1);
                    //备注
                    try {
                        log.error("车辆（巴士）信息导入，保存信息 param=" + entity.toString());
                        carBizCarInfoTempMapper.insert(entity);
                        result.put("result", "1");
                        result.put("msg", "");
                    } catch (Exception e) {
                        log.error("车辆（巴士）信息导入，保存信息  error：" + e);
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setLicensePlates(licensePlates);
                        returnVO.setReson(entity.getLicensePlates() + "导入失败！");
                        listException.add(returnVO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String download = "";
        try {
            // 将错误列表导出
            if (listException.size() > 0) {
                Workbook wb = Common.exportExcel(request.getServletContext().getRealPath("/") + "template" + File.separator + "car_exception.xlsx", listException);
                download = Common.exportExcelFromTempletToLoacl(request, wb,
                        new String("ERROR".getBytes("utf-8"), "iso8859-1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"".equals(download) && download != null) {
            result.put("download", download);
        } else {
            result.put("download", "");
        }
        return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,download);
    }
}