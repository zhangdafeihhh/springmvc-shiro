package com.zhuanche.serv.mdbcarmanage;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.controller.driverteam.DriverInfoTemporaryController;
import com.zhuanche.entity.mdbcarmanage.AgreementCompany;
import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.CarImportExceptionEntity;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.ImportTempletUtils;
import com.zhuanche.util.ValidateUtils;
import com.zhuanche.util.excel.ExportExcelUtil;
import mapper.mdbcarmanage.AgreementCompanyMapper;
import mapper.mdbcarmanage.CarBizDriverInfoTempMapper;
import mapper.mdbcarmanage.ex.CarBizCarInfoTempExMapper;
import mapper.mdbcarmanage.ex.CarBizDriverInfoTempExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import com.zhuanche.util.DateUtil;


/**
 * @author wzq
 */
@Service
public class CarBizDriverInfoTempService {

    private static final Logger log =  LoggerFactory.getLogger(CarBizDriverInfoTempService.class);

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private CarBizDriverInfoTempExMapper carBizDriverInfoTempExMapper;

    @Autowired
    private CarBizCarInfoTempExMapper carBizCarInfoTempExMapper;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;

    @Autowired
    private AgreementCompanyMapper agreementCompanyMapper;

    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

    @Autowired
    private CarBizDriverInfoTempMapper carBizDriverInfoTempMapper;



    /**
     * 根据车牌号查询
     * @param licensePlates
     * @return
     */
    public CarBizDriverInfoTemp getDriverByLincesePlates(String licensePlates){
        return carBizDriverInfoTempExMapper.getDriverByLincesePlates(licensePlates);
    }

    /**
     * 更新
     * @param driverVoEntity
     * @return
     */
    public int update(CarBizDriverInfoTemp driverVoEntity){
        return carBizDriverInfoTempExMapper.update(driverVoEntity);
    }

    /**
     * 根据条件分页查询
     * @param driverVoEntity
     * @return
     */
    public List<CarBizDriverInfoTemp> queryForPageObject(CarBizDriverInfoTemp driverVoEntity){
        return carBizDriverInfoTempExMapper.queryForPageObject(driverVoEntity);
    }

    /**
     *加盟商司机删除
     * @param driverIds
     * @return
     */
    public AjaxResponse delete(String driverIds) {
        try {
            String[] driverIdsArray = driverIds.split(",");
            CarBizDriverInfoTemp entity =  new CarBizDriverInfoTemp();
            for(int i=0;i<driverIdsArray.length;i++){
                entity.setDriverId(driverIdsArray[i]);
                CarBizDriverInfoTemp driverInfoTemp = carBizDriverInfoTempExMapper.queryForObject(entity);
                if(driverInfoTemp!=null&&driverInfoTemp.getLicensePlates()!=null){
                    int had = carBizCarInfoTempExMapper.checkLicensePlates(driverInfoTemp.getLicensePlates());
                    if(had>0){
                        CarBizCarInfoTemp car = new CarBizCarInfoTemp();
                        car.setCarId(Integer.parseInt(driverInfoTemp.getLicensePlatesId()));
                        car.setLicensePlates(driverInfoTemp.getLicensePlates());
                        car.setDriverid(0);
                        carBizCarInfoTempExMapper.updateByLicensePlates(car);
                    }
                }
                carBizDriverInfoTempExMapper.delete(entity);
            }
            return AjaxResponse.success(RestErrorCode.HTTP_SYSTEM_ERROR);
        } catch (Exception e) {
            log.error("司机Id:"+driverIds+"删除异常:");
            e.printStackTrace();
            return  AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     *根据司机Id查询
     * @param entity
     * @return
     */
    public CarBizDriverInfoTemp queryForObject(CarBizDriverInfoTemp entity) {
        return carBizDriverInfoTempExMapper.queryForObject(entity);
    }

    /**
     * 导入司机
     * @param is
     * @param prefix 后缀名
     * @param cityId 城市Id
     * @param supplierId 供应商Id
     * @param teamId 车队Id
     * @param groupId 小组Id
     * @return
     */
    public AjaxResponse importDriverInfo(InputStream is, String prefix, Integer cityId, Integer supplierId,Integer teamId,Integer groupId,HttpServletResponse response) throws IOException {
        String resultErrorMag1 = "导入模板格式错误!";
        Map<String,Object> result = new HashMap<String,Object>();
        List<CarImportExceptionEntity> listException = new ArrayList<CarImportExceptionEntity>();
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        List<CarBizDriverInfoTemp> driverList = new ArrayList<CarBizDriverInfoTemp>();
        try {
            Workbook workbook = null;
            if (prefix.equals("xls")) {
                workbook = new HSSFWorkbook(is);
            }
            else if (prefix.equals("xlsx")) {
                workbook = new XSSFWorkbook(is);
            }
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            if(row1==null){
                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,resultErrorMag1);
            }
            for (int colIx = 0; colIx < 50; colIx++) {
                Cell cell = row1.getCell(colIx); // 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,resultErrorMag1);
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("车牌号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"车牌号不正确");
                            }
                            break;
                        case 2:
                            if (!cellValue.getStringValue().contains("机动车驾驶员姓名")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"机动车驾驶员姓名不正确");
                            }
                            break;
                        case 3:
                            if (!cellValue.getStringValue().contains("驾驶员身份证号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员身份证号不正确");
                            }
                            break;
                        case 4:
                            if (!cellValue.getStringValue().contains("驾驶员手机号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员手机号不正确");
                            }
                            break;
                        case 5:
                            if (!cellValue.getStringValue().contains("司机手机型号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"司机手机型号不正确");
                            }
                            break;
                        case 6:
                            if (!cellValue.getStringValue().contains("司机手机运营商")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"司机手机运营商不正确");
                            }
                            break;

                        case 7:
                            if (!cellValue.getStringValue().contains("驾驶员性别")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员性别不正确");
                            }
                            break;
                        case 8:
                            if (!cellValue.getStringValue().contains("出生日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"出生日期不正确");
                            }
                            break;
                        case 9:
                            if (!cellValue.getStringValue().contains("年龄")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"年龄不正确");
                            }
                            break;
                        case 10:
                            if (!cellValue.getStringValue().contains("服务监督号码")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"服务监督号码不正确");
                            }
                            break;
                        case 11:
                            if (!cellValue.getStringValue().contains("服务监督链接")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"服务监督链接不正确");
                            }
                            break;
                        case 12:
                            if (!cellValue.getStringValue().contains("车型类别")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"车型类别不正确");
                            }
                            break;
                        case 13:
                            if (!cellValue.getStringValue().contains("驾照类型")) {
                                return AjaxResponse.fail(500,"驾照类型不正确");
                            }
                            break;
                        case 14:
                            if (!cellValue.getStringValue().contains("驾照领证日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾照领证日期不正确");
                            }
                            break;
                        case 15:
                            if (!cellValue.getStringValue().contains("驾龄")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾龄不正确");
                            }
                            break;
                        case 16:
                            if (!cellValue.getStringValue().contains("驾照到期时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾照到期时间不正确");
                            }
                            break;
                        case 17:
                            if (!cellValue.getStringValue().contains("档案编号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"档案编号不正确");
                            }
                            break;
                        case 18:
                            if (!cellValue.getStringValue().contains("国籍")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"国籍不正确");
                            }
                            break;
                        case 19:
                            if (!cellValue.getStringValue().contains("驾驶员民族")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员民族不正确");
                            }
                            break;
                        case 20:
                            if (!cellValue.getStringValue().contains("驾驶员婚姻状况")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员婚姻状况不正确");
                            }
                            break;
                        case 21:
                            if (!cellValue.getStringValue().contains("驾驶员外语能力")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员外语能力不正确");
                            }
                            break;
                        case 22:
                            if (!cellValue.getStringValue().contains("驾驶员学历")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员学历不正确");
                            }
                            break;
                        case 23:
                            if (!cellValue.getStringValue().contains("户口登记机关名称")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"户口登记机关名称不正确");
                            }
                            break;
                        case 24:
                            if (!cellValue.getStringValue().contains("户口住址或长住地址")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"户口住址或长住地址不正确");
                            }
                            break;
                        case 25:
                            if (!cellValue.getStringValue().contains("驾驶员通信地址")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员通信地址不正确");
                            }
                            break;
                        case 26:
                            if (!cellValue.getStringValue().contains("驾驶员照片文件编号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员照片文件编号不正确");
                            }
                            break;
                        case 27:
                            if (!cellValue.getStringValue().contains("机动车驾驶证号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"机动车驾驶证号不正确");
                            }
                            break;
                        case 28:
                            if (!cellValue.getStringValue().contains("机动车驾驶证扫描件文件编号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"机动车驾驶证扫描件文件编号不正确");
                            }
                            break;
                        case 29:
                            if (!cellValue.getStringValue().contains("初次领取驾驶证日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"初次领取驾驶证日期不正确");
                            }
                            break;
                        case 30:
                            if (!cellValue.getStringValue().contains("是否巡游出租汽车驾驶员")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"是否巡游出租汽车驾驶员不正确");
                            }
                            break;
                        case 31:
                            if (!cellValue.getStringValue().contains("网络预约出租汽车驾驶员资格证号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"网络预约出租汽车驾驶员资格证号不正确");
                            }
                            break;
                        case 32:
                            if (!cellValue.getStringValue().contains("网络预约出租汽车驾驶员证初领日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"网络预约出租汽车驾驶员证初领日期不正确");
                            }
                            break;
                        case 33:
                            if (!cellValue.getStringValue().contains("巡游出租汽车驾驶员资格证号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"巡游出租汽车驾驶员资格证号不正确");
                            }
                            break;
                        case 34:
                            if (!cellValue.getStringValue().contains("网络预约出租汽车驾驶员证发证机构")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"网络预约出租汽车驾驶员证发证机构不正确");
                            }
                            break;
                        case 35:
                            if (!cellValue.getStringValue().contains("资格证发证日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"资格证发证日期不正确");
                            }
                            break;
                        case 36:
                            if (!cellValue.getStringValue().contains("初次领取资格证日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"初次领取资格证日期不正确");
                            }
                            break;
                        case 37:
                            if (!cellValue.getStringValue().contains("资格证有效起始日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"资格证有效起始日期不正确");
                            }
                            break;
                        case 38:
                            if (!cellValue.getStringValue().contains("资格证有效截止日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"资格证有效截止日期不正确");
                            }
                            break;
                        case 39:
                            if (!cellValue.getStringValue().contains("注册日期")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"注册日期不正确");
                            }
                            break;
                        case 40:
                            if (!cellValue.getStringValue().contains("是否专职驾驶员")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"是否专职驾驶员不正确");
                            }
                            break;
                        case 41:
                            if (!cellValue.getStringValue().contains("是否在驾驶员黑名单内")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"是否在驾驶员黑名单内不正确");
                            }
                            break;
                        case 42:
                            if (!cellValue.getStringValue().contains("驾驶员合同（或协议）签署公司")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"驾驶员合同（或协议）签署公司不正确");
                            }
                            break;
                        case 43:
                            if (!cellValue.getStringValue().contains("有效合同时间")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"有效合同时间不正确");
                            }
                            break;
                        case 44:
                            if (!cellValue.getStringValue().contains("合同（或协议）有效期起")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"合同（或协议）有效期起不正确");
                            }
                            break;
                        case 45:
                            if (!cellValue.getStringValue().contains("合同（或协议）有效期止")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"合同（或协议）有效期止不正确");
                            }
                            break;
                        case 46:
                            if (!cellValue.getStringValue().contains("紧急情况联系人")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"紧急情况联系人不正确");
                            }
                            break;
                        case 47:
                            if (!cellValue.getStringValue().contains("紧急情况联系人电话")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"紧急情况联系人电话不正确");
                            }
                            break;
                        case 48:
                            if (!cellValue.getStringValue().contains("紧急情况联系人通讯地址")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"紧急情况联系人通讯地址不正确");
                            }
                            break;
                        case 49:
                            if (!cellValue.getStringValue().contains("银行卡卡号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"银行卡卡号不正确");
                            }
                            break;
                        case 50:
                            if (!cellValue.getStringValue().contains("银行卡开户行")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,"银行卡开户行不正确");
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
                if(row==null){
                    continue;
                }
                CarBizDriverInfoTemp driver = new CarBizDriverInfoTemp();
                boolean isTrue = true;// 标识是否为有效数据
                String cityName = "";
                // 司机状态，创建人
                driver.setStatus(1);
                driver.setCreateBy(user.getId());
                driver.setUpdateBy(user.getId());
                //加入加盟类型
                Integer cooperationType = queryCooperationTypeBySupplierId(supplierId);
                String bankCardNumber = "";
                // 司机导入模板总共18列
                for (int colIx = 0; colIx < 50; colIx++) {
                    Cell cell = row.getCell(colIx); // 获取列对象
                    CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                    switch ((colIx + 1)){
                        // 车牌号
                        case 1:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【车牌号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // 根据车牌号查找对应车型
                                String licensePlates = cellValue.getStringValue();
                                licensePlates = Common.replaceBlank(licensePlates);
                                CarBizDriverInfoTemp driverEntity = new CarBizDriverInfoTemp();
                                driverEntity.setLicensePlates(licensePlates);
                                Map<String,Object> paramsMap = Maps.newHashMap();
                                paramsMap.put("licensePlates",licensePlates);
                                CarBizCarInfoTemp carBizCarInfo = new CarBizCarInfoTemp();
                                carBizCarInfo = carBizCarInfoTempExMapper.selectBylicensePlates(paramsMap);
                                if (carBizCarInfo == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                            + (colIx + 1) + "列 无效的【车牌号】");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }else if(carBizDriverInfoTempExMapper.validateLicensePlates(driverEntity) > 0){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                            + (colIx + 1) + "列 【车牌号】:"+cellValue.getStringValue()+"已被绑定");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                                driverEntity.setServiceCityId(cityId);
                                driverEntity.setSupplierId(supplierId);
                                if(carBizDriverInfoTempExMapper.validateCityAndSupplier(driverEntity) > 0){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                            + (colIx + 1) + "列 【车牌号】:"+cellValue.getStringValue()+"不在所选的城市或厂商");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }else {
                                    driver.setLicensePlates(licensePlates);
                                    driver.setCooperationType(cooperationType);
                                }
                            }
                            break;
                        // 机动车驾驶员姓名
                        case 2:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" + (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【机动车驾驶员姓名】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String name = Common.replaceBlank(cellValue.getStringValue());
                                driver.setName(name);
                            }

                            break;
                        // 驾驶员身份证号
                        case 3:
                            if (cellValue == null || StringUtils.isEmpty(cellValue
                                    .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员身份证号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);

                                isTrue = false;
                            }else{
                                String idCardNo = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                if("X".equals(idCardNo.substring(idCardNo.length()-1,idCardNo.length()))){
                                    idCardNo = idCardNo.toLowerCase();
                                }
                                String mess = "";
                                try{
                                    // 判断身份证号合法性
                                    mess = ValidateUtils.IDCardValidate(idCardNo);
                                }catch (Exception e) {
                                    log.info("导入司机判断身份证号码合法性 error:"+e);
                                }
                                if (mess!=null&&!"".equals(mess)) {
                                    if(!Common.validateIdCarNo(idCardNo)){
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                                + "列 【驾驶员身份证号】"+mess);
                                        listException.add(returnVO);
                                        colIx = 100;// 结束本行数据
                                        isTrue = false;
                                    }
                                }
                                CarBizDriverInfoTemp driverEntity = new CarBizDriverInfoTemp();
                                driverEntity.setIdCardNo(idCardNo);
                                Integer c = carBizDriverInfoExMapper.checkIdCardNoNew(driverEntity);
                                if (c != null && c.intValue() > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                            + (colIx + 1) + "列 已存在【驾驶员身份证号为："
                                            + idCardNo
                                            + "】的信息");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                                CarBizDriverInfoTemp carBizDriverInfoTemp = new CarBizDriverInfoTemp();
                                carBizDriverInfoTemp.setIdCardNo(idCardNo);
                                Integer s = carBizDriverInfoTempExMapper.checkIdCardNo(carBizDriverInfoTemp);
                                if (s != null && s.intValue() > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                            + (colIx + 1) + "列 已存在【驾驶员身份证号为："
                                            + idCardNo
                                            + "】的信息");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                                driver.setIdCardNo(idCardNo);
                            }
                            break;
                        // 驾驶员手机
                        case 4:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员手机】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }
                            // 判断手机号码合法性
                            else if (!ValidateUtils.validatePhone(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员手机】不合法");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                CarBizDriverInfo entity = new CarBizDriverInfo();
                                entity.setPhone(cellValue.getStringValue());
                                Integer c = carBizDriverInfoExMapper.selectCountForPhone(entity);
                                if (c != null && c.intValue() > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                            + (colIx + 1) + "列 已存在【手机号为："
                                            + cellValue.getStringValue()
                                            + "】的信息");
                                    listException.add(returnVO);
                                    colIx = 100;// 结束本行数据
                                    isTrue = false;
                                }else {
                                    CarBizDriverInfoTemp driverInfoTemp = new CarBizDriverInfoTemp();
                                    driverInfoTemp.setPhone(cellValue.getStringValue());
                                    c = carBizDriverInfoTempExMapper.selectCountForPhone(driverInfoTemp);
                                    if (c != null && c.intValue() > 0) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 已存在【手机号为："
                                                + cellValue.getStringValue()
                                                + "】的信息");
                                        listException.add(returnVO);
                                        colIx = 100;// 结束本行数据
                                        isTrue = false;
                                    }else {
                                        driver.setPhone(Common.replaceBlank(cellValue.getStringValue()));
                                    }
                                }
                            }
                            break;
                        // 司机手机型号
                        case 5:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【司机手机型号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else{
                                String phoneType = Common.replaceBlank(cellValue.getStringValue());
                                driver.setPhoneType(phoneType);
                            }
                            break;
                        // 司机手机运营商
                        case 6:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【司机手机运营商】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                if ("中国移动".equals(cellValue.getStringValue().trim())||
                                        "中国联通".equals(cellValue.getStringValue().trim())||
                                        "中国电信".equals(cellValue.getStringValue().trim())||
                                        "其他".equals(cellValue.getStringValue().trim())) {
                                    driver.setPhoneCorp(cellValue.getStringValue());
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【司机手机运营商】；中国移动、中国联通 或者 中国电信、其他");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 性别
                        case 7:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【性别】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }
                            else {
                                if ("男".equals(cellValue.getStringValue().trim())) {
                                    driver.setGender(1);
                                } else if ("女".equals(cellValue.getStringValue().trim())) {
                                    driver.setGender(0);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【性别】；男 或者 女");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 出生日期
                        case 8:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【出生日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setBirthDay(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【出生日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 年龄
                        case 9:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【年龄】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else{
                                if(Common.isNumeric(cellValue.getStringValue())){
                                    driver.setAge(Integer.valueOf(cellValue.getStringValue()));
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【年龄】只能有数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 服务监督号码
                        case 10:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【服务监督号码】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else{
                                String superintendNo = Common.replaceBlank(cellValue.getStringValue());
                                driver.setSuperintendNo(superintendNo);
                            }
                            break;
                        // 服务监督链接
                        case 11:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【服务监督链接】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else{
                                String superintendUrl = Common.replaceBlank(cellValue.getStringValue());
                                driver.setSuperintendUrl(superintendUrl);
                            }
                            break;
                        // 车型类别
                        case 12:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【车型类别】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                //司机groupId与车辆分离
                                String groupName= cellValue.getStringValue();
                                CarBizCarGroup group1 = new CarBizCarGroup();
                                group1.setGroupName(groupName);
                                CarBizCarGroup group = carBizCarGroupExMapper.queryForObjectByGroupName(group1);
                                if(group!=null && group.getGroupId()!=null){
                                    driver.setGroupid(group.getGroupId());
                                    driver.setCarGroupName(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                            + "列 【车型类别】没有"+groupName);
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾照类型
                        case 13:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾照类型】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else{
                                String driverType = "A1、A2、A3、B1、B2、C1、C2、N、P";
                                String drivingLicenseType = Common.replaceBlank(cellValue.getStringValue());
                                String yuandrivingTypeString = "C1";
                                if(driverType.indexOf(drivingLicenseType)>0){
                                    yuandrivingTypeString = drivingLicenseType;
                                }
                                driver.setDrivingLicenseType(yuandrivingTypeString);
                            }
                            break;
                        // 驾照领证日期
                        case 14:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾照领证时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setIssueDate(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【驾照领证时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾龄
                        case 15:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾龄】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                if(Common.isNumeric(cellValue.getStringValue())){
                                    driver.setDrivingYears(Integer.valueOf(cellValue.getStringValue()));
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                            + (colIx + 1) + "列 【驾龄】只能有数字");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾照到期时间
                        case 16:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾照到期时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            "yyyy-MM-dd");
                                    if (sdf.parse(d).getTime() < sdf.parse(
                                            datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 【驾照到期时间】应该大于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        driver.setExpireDate(d);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【驾照到期时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 档案编号
                        case 17:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【档案编号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String archivesNo = Common.replaceBlank(cellValue.getStringValue());
                                driver.setArchivesNo(archivesNo);
                            }
                            break;
                        // 国籍
                        case 18:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【国籍】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String nationAlity = Common.replaceBlank(cellValue.getStringValue());
                                driver.setNationAlity(nationAlity);
                            }
                            break;
                        // 驾驶员民族
                        case 19:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员民族】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String nation = Common.replaceBlank(cellValue.getStringValue());
                                driver.setNation(nation);
                            }
                            break;
                        // 驾驶员婚姻状况
                        case 20:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员婚姻状况】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                if ("已婚".equals(cellValue.getStringValue().trim())||
                                        "未婚".equals(cellValue.getStringValue().trim())||
                                        "离异".equals(cellValue.getStringValue().trim())) {
                                    driver.setMarriage(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                            + "列 【驾驶员婚姻状况】，请输入已婚、未婚、离异");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾驶员外语能力
                        case 21:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员外语能力】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                if ("英语".equals(cellValue.getStringValue().trim())){
                                    driver.setForeignLanguage("1");
                                }else if ("德语".equals(cellValue.getStringValue().trim())){
                                    driver.setForeignLanguage("2");
                                }else if ("法语".equals(cellValue.getStringValue().trim())){
                                    driver.setForeignLanguage("3");
                                }else if ("其他".equals(cellValue.getStringValue().trim())){
                                    driver.setForeignLanguage("4");
                                }else if ("无".equals(cellValue.getStringValue().trim())){
                                    driver.setForeignLanguage("0");
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                            + "列 【驾驶员外语能力】，请填写英语、德语、法语、其他或者无");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾驶员学历
                        case 22:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员学历】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                if ("研究生".equals(cellValue.getStringValue().trim())||
                                        "本科".equals(cellValue.getStringValue().trim())||
                                        "大专".equals(cellValue.getStringValue().trim())||
                                        "中专".equals(cellValue.getStringValue().trim())||
                                        "高中".equals(cellValue.getStringValue().trim())||
                                        "初中".equals(cellValue.getStringValue().trim())||
                                        "小学".equals(cellValue.getStringValue().trim())||
                                        "其他".equals(cellValue.getStringValue().trim())) {
                                    driver.setEducation(cellValue.getStringValue());
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                            + "列 【驾驶员学历】，请填写研究生、本科、大专、中专、高中、 初中、 中学、其他");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 户口登记机关名称
                        case 23:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【户口登记机关名称】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String houseHoldRegisterPermanent = Common.replaceBlank(cellValue.getStringValue());
                                driver.setHouseHoldRegisterPermanent(houseHoldRegisterPermanent);
                            }
                            break;
                        // 户口住址或长住地址
                        case 24:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【户口住址或长住地址】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String houseHoldRegister = Common.replaceBlank(cellValue.getStringValue());
                                driver.setHouseHoldRegister(houseHoldRegister);
                            }
                            break;
                        // 驾驶员通信地址
                        case 25:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员通信地址】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String currentAddress = Common.replaceBlank(cellValue.getStringValue());
                                driver.setCurrentAddress(currentAddress);
                            }
                            break;
                        // 驾驶员照片文件编号
                        case 26:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                if(cellValue.getStringValue().contains("http://pupload.01zhuanche.com/")){
                                    String photoSrc = Common.replaceBlank(cellValue.getStringValue());
                                    driver.setPhotoSrc(photoSrc);
                                }
                            }
                            break;
                        // 机动车驾驶证号
                        case 27:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【机动车驾驶证号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String driverLicenseNumber = Common.replaceBlank(cellValue.getStringValue());
                                driver.setDriverLicenseNumber(driverLicenseNumber);
                            }
                            break;
                        // 机动车驾驶证扫描件文件编号
                        case 28:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                if(cellValue.getStringValue().contains("http://pupload.01zhuanche.com/")){
                                    String drivingLicenseImg = Common.replaceBlank(cellValue.getStringValue());
                                    driver.setDrivingLicenseImg(drivingLicenseImg);
                                }
                            }
                            break;
                        //初次领取驾驶证日期
                        case 29:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【初次领取驾驶证日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            "yyyy-MM-dd");
                                    if (sdf.parse(d).getTime() > sdf.parse(
                                            datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 【初次领取驾驶证日期】应该小于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        driver.setFirstDrivingLicenseDate(d);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【初次领取驾驶证日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //是否巡游出租汽车驾驶员
                        case 30:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【是否巡游出租汽车驾驶员】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }
                            else {
                                if ("是".equals(cellValue.getStringValue().trim())) {
                                    driver.setIsXyDriver(1);
                                } else if ("否".equals(cellValue.getStringValue().trim())) {
                                    driver.setIsXyDriver(0);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【是否巡游出租汽车驾驶员】；是 或者 否");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //网络预约出租汽车驾驶员资格证号
                        case 31:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【网络预约出租汽车驾驶员资格证号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String driverLicenseIssuingNumber = Common.replaceBlank(cellValue.getStringValue());
                                driver.setDriverLicenseIssuingNumber(driverLicenseIssuingNumber);
                            }
                            break;
                        //网络预约出租汽车驾驶员证初领日期
                        case 32:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【网络预约出租汽车驾驶员证初领日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            "yyyy-MM-dd");
                                    if (sdf.parse(d).getTime() > sdf.parse(
                                            datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 【网络预约出租汽车驾驶员证初领日期】应该小于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        driver.setFirstMeshworkDrivingLicenseDate(d);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【网络预约出租汽车驾驶员证初领日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //巡游出租汽车驾驶员资格证号
                        case 33:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【巡游出租汽车驾驶员资格证号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String xyDriverNumber = Common.replaceBlank(cellValue.getStringValue());
                                driver.setXyDriverNumber(xyDriverNumber);
                            }
                            break;
                        //网络预约出租汽车驾驶员证发证机构
                        case 34:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【网络预约出租汽车驾驶员证发证机构】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String driverLicenseIssuingCorp = Common.replaceBlank(cellValue.getStringValue());
                                driver.setDriverLicenseIssuingCorp(driverLicenseIssuingCorp);
                            }
                            break;
                        //资格证发证日期
                        case 35:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【资格证发证日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setDriverLicenseIssuingGrantDate(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【资格证发证日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //初次领取资格证日期
                        case 36:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【初次领取资格证日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setDriverLicenseIssuingFirstDate(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【初次领取资格证日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //资格证有效起始日期
                        case 37:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【资格证有效起始日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setDriverLicenseIssuingDateStart(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【资格证有效起始日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //资格证有效截止日期
                        case 38:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【资格证有效截止日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setDriverLicenseIssuingDateEnd(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【资格证有效截止日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //注册日期
                        case 39:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【注册日期】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            "yyyy-MM-dd");
                                    if (sdf.parse(d).getTime() > sdf.parse(
                                            datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 【注册日期】应该小于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        driver.setDriverLicenseIssuingRegisterDate(d);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【注册日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //是否专职驾驶员
                        case 40:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【是否专职驾驶员】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                if ("是".equals(cellValue.getStringValue().trim())||
                                        "否".equals(cellValue.getStringValue().trim())) {
                                    driver.setPartTimeJobDri(cellValue.getStringValue());
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【是否专职驾驶员】；是 或者 否");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //是否在驾驶员黑名单内
                        case 41:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【是否在驾驶员黑名单内】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                if ("是".equals(cellValue.getStringValue().trim())||
                                        "否".equals(cellValue.getStringValue().trim())) {
                                    driver.setIsDriverBlack(cellValue.getStringValue());
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIx + 1)
                                            + "行数据，第" + (colIx + 1)
                                            + "列 请输入正确的【是否在驾驶员黑名单内】；是 或者 否");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //驾驶员合同（或协议）签署公司
                        case 42:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员合同（或协议）签署公司】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String corpType = Common.replaceBlank(cellValue.getStringValue());
                                if(cooperationType!=null&&cooperationType==5){
                                    int count = this.queryAgreementCompanyByName(corpType);
                                    if(count>0){
                                        driver.setCorpType(corpType);
                                    }else{
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                                + "列 【驾驶员合同（或协议）签署公司】在协议公司中不存在");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    driver.setCorpType(corpType);
                                }
                            }
                            break;
                        //有效合同时间
                        case 43:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【有效合同时间】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            "yyyy-MM-dd");
                                    if (sdf.parse(d).getTime() < sdf.parse(
                                            datetime).getTime()) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 【有效合同时间】应该大于当前时间");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        d = dateFormat.format(dateFormat.parse(d));
                                        driver.setContractDate(d);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【有效合同时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //合同（或协议）有效期起
                        case 44:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【合同（或协议）有效期起】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setSignDate(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【合同（或协议）有效期起】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        //合同（或协议）有效期止
                        case 45:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【合同（或协议）有效期止】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String datetime = dateFormat.format(new Date());
                                String d = cellValue.getStringValue()
                                        .replace("年", "-")
                                        .replace("月", "-")
                                        .replace("日", "-")
                                        .replace(".", "-").trim();
                                if(d.substring(d.length()-1).equals("-")){
                                    d=d.substring(0,d.length()-1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setSignDateEnd(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第"
                                            +  (rowIx+1)
                                            + "行数据，第"
                                            + (colIx + 1)
                                            + "列 【合同（或协议）有效期止】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 紧急联系人
                        case 46:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【紧急联系人】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String emergencyContactPerson = Common.replaceBlank(cellValue.getStringValue());
                                driver.setEmergencyContactPerson(emergencyContactPerson);
                            }
                            break;
                        // 紧急联系方式
                        case 47:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【紧急联系方式】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String emergencyContactNumber = Common.replaceBlank(cellValue.getStringValue());
                                driver.setEmergencyContactNumber(emergencyContactNumber.trim());
                            }
                            break;
                        // 紧急情况联系人通讯地址
                        case 48:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                        + "列 【紧急情况联系人通讯地址】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String emergencyContactAddr = Common.replaceBlank(cellValue.getStringValue());
                                driver.setEmergencyContactAddr(emergencyContactAddr);
                            }
                            break;
                        // 银行卡卡号
                        case 49:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                bankCardNumber = cellValue.getStringValue().replaceAll("\\s*", "");

                                if(!Common.isRegular(bankCardNumber,"(^[0-9]{16,19}$)")){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                            + "列 【银行卡卡号】为16~19位数字组合");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }else{
                                    CarBizDriverInfoTemp hffd = new CarBizDriverInfoTemp();
                                    hffd.setBankCardNumber(bankCardNumber);
                                    if(carBizDriverInfoTempExMapper.validateBankCardNumber(hffd) > 0){
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 【银行卡卡号】:"+cellValue.getStringValue()+"已被绑定");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }else if(carBizDriverInfoExMapper.validateBankCardNumber(hffd) > 0){
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 【银行卡卡号】:"+cellValue.getStringValue()+"已被绑定");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }else{
                                        driver.setBankCardNumber(bankCardNumber);
                                    }
                                }
                            }
                            break;
                        // 银行卡开户行
                        case 50:
                            if (cellValue != null && !StringUtils.isEmpty(cellValue.getStringValue())) {
                                String bankCardBank = cellValue.getStringValue().replaceAll("\\s*", "");
                                if(bankCardBank.length()>100){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                            + (colIx + 1) + "列 【银行卡卡号】:银行卡开户行不能超过100字，请检查后重新输入");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }else{
                                    if (StringUtils.isEmpty(bankCardNumber)) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                                + (colIx + 1) + "列 【银行卡卡号】:请填写银行卡信息");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }else{
                                        driver.setBankCardBank(bankCardBank);
                                    }
                                }
                            }else{
                                if (!StringUtils.isEmpty(bankCardNumber)) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第"
                                            + (colIx + 1) + "列 【银行卡开户行】:请填写银行卡开户行信息");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                    }// switch end

                }// 循环列结束
                if (isTrue && driver != null) {
                    driver.setServiceCityId(cityId);
                    driver.setSupplierId(supplierId);
                    driver.setTeamid(String.valueOf(teamId));
                    driver.setGroupIds(String.valueOf(groupId));
                    driverList.add(driver);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        if(listException.size() > 0){
            StringBuilder errorMsg = new StringBuilder();
            for (CarImportExceptionEntity entity:listException){
                errorMsg.append(entity.getReson()).append(";");
            }
            return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,errorMsg);
        }else{
            List<CarBizDriverInfoTemp> driverList2 = new ArrayList<CarBizDriverInfoTemp>();
            String phone = "";
            String licensePlates = "";
            String idCardNo = "";
            for(int i=0;i<driverList.size();i++){
                CarBizDriverInfoTemp vo = driverList.get(i);
                String yuphone = vo.getPhone();
                String yulicensePlates = vo.getLicensePlates();
                String yuIdCardNo = vo.getIdCardNo();
                if(i==0){
                    phone = yuphone;
                    licensePlates = yulicensePlates;
                    idCardNo = yuIdCardNo;
                    driverList2.add(vo);
                }else{
                    if(phone.contains(yuphone)){
                        log.info("导入司机，有重复手机号，去除，手机号phone="+yuphone);
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setReson("导入司机，有重复手机号，手机号phone="+yuphone);
                        listException.add(returnVO);
                    }else if(licensePlates.contains(yulicensePlates)){
                        log.info("导入司机，有重复车牌号，去除，车牌号licensePlates="+yulicensePlates);
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setReson("导入司机，有重复车牌号，车牌号licensePlates="+yulicensePlates);
                        listException.add(returnVO);
                    }else if(idCardNo.contains(yuIdCardNo)){
                        log.info("导入司机，有重复身份证号，去除，身份证号idCardNo="+yuIdCardNo);
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setReson("导入司机，有重复身份证号，去除，身份证号idCardNo="+yuIdCardNo);
                        listException.add(returnVO);
                    }else{
                        phone += yuphone;
                        licensePlates += yulicensePlates;
                        idCardNo += yuIdCardNo;
                        driverList2.add(vo);
                    }
                }
            }
            for(CarBizDriverInfoTemp asd: driverList2){
                try {
                    log.info("导入司机到临时表 导入司机后信息=:"+ JSON.toJSONString(asd));
                    carBizDriverInfoTempExMapper.save(asd);
                    log.info("导入司机到临时表 导入司机后司机id="+asd.getDriverId());
                    CarBizCarInfoTemp carInfoEntity = new CarBizCarInfoTemp();
                    carInfoEntity.setLicensePlates(asd.getLicensePlates());
                    carInfoEntity.setDriverid(Integer.valueOf(asd.getDriverId()));
                    log.info("导入司机到临时表 导入司机后司机id="+asd.getDriverId()+",更新车辆信息begin="+JSON.toJSONString(carInfoEntity));
                    carBizCarInfoTempExMapper.updateByLicensePlates(carInfoEntity);
                    log.info("导入司机到临时表 导入司机后司机id="+asd.getDriverId()+",更新车辆信息end="+JSON.toJSONString(carInfoEntity));
                } catch (Exception e) {
                    log.info("导入司机到临时表 error:"+e);
                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                    returnVO.setReson(asd.getName()+"导入失败！");
                    listException.add(returnVO);
                }
            }
        }
        try {
            //将错误列表导出
            if (listException.size() > 0) {
                StringBuilder errorMsg = new StringBuilder();
                for (CarImportExceptionEntity entity:listException){
                    errorMsg.append(entity.getReson()).append(";");
                }
                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR,errorMsg);
            }else{
                return AjaxResponse.success(RestErrorCode.SUCCESS);
            }
        }catch(Exception e){
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 导入巴士司机
     * @param is
     * @param prefix
     * @param cityId
     * @param supplierId
     * @return
     */
    public AjaxResponse importDriverInfo4Bus(InputStream is,String prefix,Integer cityId,Integer supplierId,Integer teamId,Integer groupId) {
        // 错误信息
        List<CarImportExceptionEntity> listException = new ArrayList<CarImportExceptionEntity>();
        // 处理结果
        Map<String,Object> result = new HashMap<String,Object>();
        String resultErrorMag1 = "导入模板格式错误!";
        // 司机信息集合
        List<CarBizDriverInfoTemp> driverList = new ArrayList<CarBizDriverInfoTemp>();
        try {
            Workbook workbook = null;
            if (prefix.equals("xls")) {
                workbook = new HSSFWorkbook(is);
            }
            else if (prefix.equals("xlsx")) {
                workbook = new XSSFWorkbook(is);
            }
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // 检查模板是否正确
            Row headRow = sheet.getRow(0);// 模板列表头字段
            if(headRow==null){
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
            }
            // 判断模板列是否缺少
            List<String> templetFields = ImportTempletUtils.getTempletFields(ImportTempletUtils.TempletType.BUS_DRIVER);
            for (int colIndex = 0; colIndex < templetFields.size(); colIndex++) {
                Cell cell = headRow.getCell(colIndex);// 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,resultErrorMag1);
                }
                String field = templetFields.get(colIndex);
                if (!cellValue.getStringValue().contains(field)) {
                    return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR,"模板缺少【" + field + "】列");
                }
            }


            // 当前登录人信息
            SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
            int startIndex = 1;// 过滤掉标题，从第一行开始导入数据
            int endIndex = sheet.getLastRowNum(); // 要导入数据的总条数
            for (int rowIndex = startIndex; rowIndex <= endIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex); // 获取行对象
                if (row == null){
                    continue;
                }
                // 标识是否为有效数据
                boolean isTrue = true;
                // 司机信息对象
                CarBizDriverInfoTemp driver = new CarBizDriverInfoTemp();
                // 司机状态，创建人
                driver.setStatus(1);
                driver.setCreateBy(user.getId());
                driver.setUpdateBy(user.getId());

                // 加入加盟类型
                Integer cooperationType = queryCooperationTypeBySupplierId(supplierId);
                // 供应商名称
                String supplierFullName = querySupplierNameBySupplierId(supplierId);


                // 司机导入模板总共12列
                for (int colIndex = 0; colIndex < 12; colIndex++) {
                    Cell cell = row.getCell(colIndex); // 获取列对象
                    CellValue cellValue = evaluator.evaluate(cell); // 获取列属性

                    switch ((colIndex + 1)) {
                        // 机动车驾驶员姓名
                        case 1:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" + (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【机动车驾驶员姓名】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String name = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                driver.setName(name);
                            }
                            break;
                        // 驾驶员身份证号
                        case 2:
                            if (cellValue == null || StringUtils.isEmpty(cellValue .getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾驶员身份证号】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            }else{
                                String idCardNo = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                if("X".equals(idCardNo.substring(idCardNo.length()-1,idCardNo.length()))){
                                    idCardNo = idCardNo.toLowerCase();
                                }
                                String mess = "";
                                try{
                                    // 判断身份证号合法性
                                    mess = ValidateUtils.IDCardValidate(idCardNo);
                                }catch (Exception e) {
                                    log.info("导入司机判断身份证号码合法性 error:"+e);
                                }
                                if (StringUtils.isNotBlank(mess)) {
                                    if(!Common.validateIdCarNo(idCardNo)){
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾驶员身份证号】"+mess);
                                        listException.add(returnVO);
                                        colIndex = 100;// 结束本行数据
                                        isTrue = false;
                                    }
                                }
                                CarBizDriverInfoTemp driverEntity = new CarBizDriverInfoTemp();
                                driverEntity.setIdCardNo(idCardNo);
                                Integer c = carBizDriverInfoExMapper.checkIdCardNoNew(driverEntity);
                                if (c != null && c.intValue() > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 已存在【驾驶员身份证号为：" + idCardNo + "】的信息");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                                Integer s = carBizDriverInfoTempExMapper.checkIdCardNo(driverEntity);
                                if (s != null && s.intValue() > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 已存在【驾驶员身份证号为：" + idCardNo + "】的信息");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                                driver.setIdCardNo(idCardNo);
                            }
                            break;
                        // 出生日期
                        case 3:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【出生日期】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue().replace("年", "-").replace("月", "-").replace("日", "-").replace(".", "-").trim();
                                if (d.substring(d.length() - 1).equals("-")) {
                                    d = d.substring(0, d.length() - 1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setBirthDay(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【出生日期】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾驶员性别
                        case 4:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾驶员性别】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String gender = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                if ("男".equals(gender)) {
                                    driver.setGender(1);
                                } else if ("女".equals(gender)) {
                                    driver.setGender(0);
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIndex + 1) + "行数据，第" + (colIndex + 1) + "列请输入正确的【驾驶员性别】；男 或者 女");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 驾驶员手机号
                        case 5:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾驶员手机号】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            } else if (!ValidateUtils.validatePhone(cellValue.getStringValue())) {// 判断手机号码合法性
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾驶员手机号】不合法");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String phone = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                CarBizDriverInfo carBizDriverInfo = new CarBizDriverInfo();
                                carBizDriverInfo.setPhone(phone);
                                Integer c = carBizDriverInfoExMapper.selectCountForPhone(carBizDriverInfo);
                                if (c != null && c.intValue() > 0) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 已存在【手机号为：" + cellValue.getStringValue() + "】的信息");
                                    listException.add(returnVO);
                                    colIndex = 100;// 结束本行数据
                                    isTrue = false;
                                }else {
                                    CarBizDriverInfoTemp driverInfoTemp = new CarBizDriverInfoTemp();
                                    driverInfoTemp.setPhone(cellValue.getStringValue());
                                    c = carBizDriverInfoTempExMapper.selectCountForPhone(driverInfoTemp);
                                    if (c != null && c.intValue() > 0) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 已存在【手机号为：" + cellValue.getStringValue() + "】的信息");
                                        listException.add(returnVO);
                                        colIndex = 100;// 结束本行数据
                                        isTrue = false;
                                    }else {
                                        driver.setPhone(phone);
                                    }
                                }
                            }
                            break;
                        // 驾照类型
                        case 6:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾照类型】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            }else{
                                String driverType = "A1、A2、A3、B1、B2、C1、C2、N、P";
                                String drivingLicenseType = Common.replaceBlank(cellValue.getStringValue());
                                String yuandrivingTypeString = "C1";
                                if(driverType.indexOf(drivingLicenseType)>0){
                                    yuandrivingTypeString = drivingLicenseType;
                                }
                                driver.setDrivingLicenseType(yuandrivingTypeString);
                            }
                            break;
                        // 驾照领证时间
                        case 7:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾照领证时间】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String d = cellValue.getStringValue().replace("年", "-").replace("月", "-").replace("日", "-").replace(".", "-").trim();
                                if (d.substring(d.length() - 1).equals("-")) {
                                    d = d.substring(0, d.length() - 1);
                                }
                                if(Common.isValidDate(d)){
                                    d = dateFormat.format(dateFormat.parse(d));
                                    driver.setIssueDate(d);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾照领证时间】格式错误，正确格式为：xxxx-xx-xx 或 xxxx年xx月xx日");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 机动车驾驶证号
                        case 8:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【机动车驾驶证号】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String driverLicenseNumber = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                driver.setDriverLicenseNumber(driverLicenseNumber);
                            }
                            break;
                        // 驾驶员合同（或协议）签署公司
                        case 9:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【驾驶员合同（或协议）签署公司】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String corpType = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                if (cooperationType != null && cooperationType == 5) {
                                    int count = this.queryAgreementCompanyByName(corpType);
                                    if (count > 0) {
                                        driver.setCorpType(corpType);
                                    } else {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" + (rowIndex + 1) + "行数据，第" + (colIndex + 1) + "列 【驾驶员合同（或协议）签署公司】在协议公司中不存在");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }
                                }else{
                                    driver.setCorpType(corpType);
                                }
                            }
                            break;
                        // 网络预约出租汽车驾驶员资格证号
                        case 10:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【网络预约出租汽车驾驶员资格证号】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            }else {
                                String driverLicenseIssuingNumber = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                driver.setDriverLicenseIssuingNumber(driverLicenseIssuingNumber);
                            }
                            break;
                        // 车牌号
                        case 11:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【车牌号】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                // 根据车牌号查找对应车型
                                String licensePlates = Common.replaceBlank(cellValue.getStringValue());
                                CarBizDriverInfoTemp driverEntity = new CarBizDriverInfoTemp();
                                driverEntity.setLicensePlates(licensePlates);
                                Map<String,Object> carSd = Maps.newHashMap();
                                carSd.put("licensePlates",licensePlates);
                                CarBizCarInfoTemp carBizCarInfo = carBizCarInfoTempExMapper.selectBylicensePlates(carSd);
                                if (carBizCarInfo == null) {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列无效的【车牌号】");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }else if(carBizDriverInfoTempExMapper.validateLicensePlates(driverEntity) > 0){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【车牌号】:"+cellValue.getStringValue()+"已被绑定");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                                driverEntity.setServiceCityId(cityId);
                                driverEntity.setSupplierId(supplierId);
                                if(carBizDriverInfoTempExMapper.validateCityAndSupplier(driverEntity) > 0){
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【车牌号】:"+cellValue.getStringValue()+"不在所选的城市或厂商");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }else {
                                    driver.setLicensePlates(licensePlates);
                                    driver.setCooperationType(cooperationType);
                                }
                            }
                            break;
                        // 车型类别
                        case 12:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【车型类别】不能为空");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String groupName = Common.replaceBlankNoUpper(cellValue.getStringValue());
                                CarBizCarGroup group = new CarBizCarGroup();
                                group.setGroupName(groupName);
                                group = carBizCarGroupExMapper.queryForObjectByGroupName(group);
                                if(group!=null && group.getGroupId()!=null){
                                    driver.setGroupid(group.getGroupId());
                                    driver.setCarGroupName(groupName);
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIndex+1) + "行数据，第" + (colIndex + 1) + "列 【车型类别】没有"+groupName);
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                    }
                }// 循环列结束

                // 司机手机型号
                String phoneType = "VIVO Y66";
                driver.setPhoneType(phoneType );
                // 司机手机运营商
                String phoneCorp = "中国移动";
                driver.setPhoneCorp(phoneCorp );
                // 年龄
                Integer age = 18;
                String birthDay = driver.getBirthDay();
                try {
                    age = DateUtil.yearsBetween(dateFormat.parse(birthDay));
                } catch (Exception e) {
                }
                driver.setAge(age);
                // 服务监督号码
                String superintendNo = "110222197805156413";
                driver.setSuperintendNo(superintendNo);
                // 驾龄
                Integer drivingYears = 3;
                String issueDate = driver.getIssueDate();
                try {
                    drivingYears = DateUtil.yearsBetween(dateFormat.parse(issueDate));
                } catch (Exception e) {
                }
                driver.setDrivingYears(drivingYears);
                // 驾照到期时间
                String expireDate = "2021-03-08";
                driver.setExpireDate(expireDate);
                // 档案编号
                String archivesNo = "500101072872";
                driver.setArchivesNo(archivesNo);
                // 国籍
                String nationAlity = "中国";
                driver.setNationAlity(nationAlity);
                // 驾驶员民族
                String nation = "汉";
                driver.setNation(nation);
                // 驾驶员婚姻状况
                String marriage = "已婚";
                driver.setMarriage(marriage);
                // 驾驶员外语能力
                driver.setForeignLanguage("0");
                // 驾驶员学历
                String education = "中专";
                driver.setEducation(education);
                // 户口登记机关名称
                String houseHoldRegisterPermanent = "派出所";
                driver.setHouseHoldRegisterPermanent(houseHoldRegisterPermanent);
                // 户口住址或长住地址
                String houseHoldRegister = "中国";
                driver.setHouseHoldRegister(houseHoldRegister);
                // 驾驶员通信地址
                String currentAddress = "中国";
                driver.setCurrentAddress(currentAddress);
                // 初次领取驾驶证日期
                String firstDrivingLicenseDate = "2015-03-08";
                driver.setFirstDrivingLicenseDate(firstDrivingLicenseDate );
                // 是否巡游出租汽车驾驶员
                driver.setIsXyDriver(0);
                // 网络预约出租汽车驾驶员证初领日期
                String firstMeshworkDrivingLicenseDate = "2013-07-01";
                driver.setFirstMeshworkDrivingLicenseDate(firstMeshworkDrivingLicenseDate);
                // 巡游出租汽车驾驶员资格证号
                String xyDriverNumber = "无";
                driver.setXyDriverNumber(xyDriverNumber);
                // 网络预约出租汽车驾驶员证发证机构
                String driverLicenseIssuingCorp = supplierFullName;
                driver.setDriverLicenseIssuingCorp(driverLicenseIssuingCorp);
                // 资格证发证日期
                String driverLicenseIssuingGrantDate = "2018-04-04";
                driver.setDriverLicenseIssuingGrantDate(driverLicenseIssuingGrantDate);
                // 初次领取资格证日期
                String driverLicenseIssuingFirstDate = "2018-04-04";
                driver.setDriverLicenseIssuingFirstDate(driverLicenseIssuingFirstDate);
                // 资格证有效起始日期
                String driverLicenseIssuingDateStart = "2013-07-01";
                driver.setDriverLicenseIssuingDateStart(driverLicenseIssuingDateStart);
                // 资格证有效截止日期
                String driverLicenseIssuingDateEnd = "2023-07-01";
                driver.setDriverLicenseIssuingDateEnd(driverLicenseIssuingDateEnd);
                // 注册日期
                String driverLicenseIssuingRegisterDate = "2018-04-04";
                driver.setDriverLicenseIssuingRegisterDate(driverLicenseIssuingRegisterDate);
                // 是否专职驾驶员
                String partTimeJobDri = "是";
                driver.setPartTimeJobDri(partTimeJobDri);
                // 有效合同时间
                String contractDate = "2013-07-01";
                driver.setContractDate(contractDate);
                // 合同（或协议）有效期起
                String signDate = "2013-07-01";
                driver.setSignDate(signDate);
                // 合同（或协议）有效期止
                String signDateEnd = "2013-07-01";
                driver.setSignDateEnd(signDateEnd);
                // 紧急情况联系人
                String emergencyContactPerson = "‭无";
                driver.setEmergencyContactPerson(emergencyContactPerson);
                // 紧急情况联系人电话
                String emergencyContactNumber = "12345678901";
                driver.setEmergencyContactNumber(emergencyContactNumber);
                // 紧急情况联系人通讯地址
                String emergencyContactAddr = "中国";
                driver.setEmergencyContactAddr(emergencyContactAddr);
                // 地图类型
                String mapType = "高德";
                driver.setMapType(mapType);

                // *********以下列默认值为空即可**********
                // 司机头像
                // 驾驶证扫描件
                // 服务监督链接
                // 评估
                // 银行卡卡号
                // 银行卡开户行
                // 备注
                // ********************************

                //serviceCityId //supplierId //teamid //groupId
                if (isTrue && driver != null) {
                    driver.setServiceCityId(cityId);
                    driver.setSupplierId(supplierId);
                    driver.setTeamid(String.valueOf(teamId));
                    driver.setGroupIds(String.valueOf(groupId));
                    driverList.add(driver);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        if(driverList.isEmpty()){
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        } else {
            List<CarBizDriverInfoTemp> validDriverList = new ArrayList<CarBizDriverInfoTemp>();
            StringBuffer phone = new StringBuffer();
            StringBuffer licensePlates = new StringBuffer();
            StringBuffer idCardNo = new StringBuffer();
            for (int i = 0; i < driverList.size(); i++) {
                CarBizDriverInfoTemp driver = driverList.get(i);
                String yuphone = driver.getPhone();
                String yulicensePlates = driver.getLicensePlates();
                String yuIdCardNo = driver.getIdCardNo();
                if (i == 0) {
                    phone.append(yuphone);
                    licensePlates.append(yulicensePlates);
                    idCardNo.append(yuIdCardNo);
                    validDriverList.add(driver);
                } else {
                    if (phone.indexOf(yuphone) > -1) {
                        log.info("导入司机(巴士)，有重复手机号，去除，手机号phone=" + yuphone);
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setReson("导入司机(巴士)，有重复手机号，手机号phone=" + yuphone);
                        listException.add(returnVO);
                    } else if (licensePlates.indexOf(yulicensePlates) > -1) {
                        log.info("导入司机(巴士)，有重复车牌号，去除，车牌号licensePlates=" + yulicensePlates);
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setReson("导入司机(巴士)，有重复车牌号，车牌号licensePlates=" + yulicensePlates);
                        listException.add(returnVO);
                    } else if (idCardNo.indexOf(yuIdCardNo) > -1) {
                        log.info("导入司机(巴士)，有重复身份证号，去除，身份证号idCardNo=" + yuIdCardNo);
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setReson("导入司机(巴士)，有重复身份证号，去除，身份证号idCardNo=" + yuIdCardNo);
                        listException.add(returnVO);
                    } else {
                        phone.append(yuphone);
                        licensePlates.append(yulicensePlates);
                        idCardNo.append(yuIdCardNo);
                        validDriverList.add(driver);
                    }
                }
            }
            boolean failed = false;
            for (CarBizDriverInfoTemp driver : validDriverList) {
                try {
                    log.info("导入司机(巴士)到临时表 导入司机后信息=:" + JSON.toJSONString(driver));
                    carBizDriverInfoTempExMapper.save(driver);
                    log.info("导入司机(巴士)到临时表 导入司机后司机id=" + driver.getDriverId());
                    CarBizCarInfoTemp centity = new CarBizCarInfoTemp();
                    centity.setLicensePlates(driver.getLicensePlates());
                    centity.setDriverid(Integer.valueOf(driver.getDriverId()));
                    log.info("导入司机(巴士)到临时表 导入司机后司机id=" + driver.getDriverId() + ",更新车辆信息begin=" + JSON.toJSONString(centity));
                    carBizCarInfoTempExMapper.updateByLicensePlates(centity);
                    log.info( "导入司机(巴士)到临时表 导入司机后司机id=" + driver.getDriverId() + ",更新车辆信息end=" + JSON.toJSONString(centity));
                } catch (Exception e) {
                    failed = true;
                    log.info("导入司机(巴士)到临时表 error:" + e);
                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                    returnVO.setReson(driver.getName() + "导入失败！");
                    listException.add(returnVO);
                }
            }
            if (failed) {
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            } else {
                return AjaxResponse.success(RestErrorCode.SUCCESS);
            }
        }
        /*try {
            //将错误列表导出
            if (listException.size() > 0) {
                response.setContentType("application/octet-stream;charset=ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String("巴士车辆导入错误列表".getBytes("GB2312"), "ISO8859-1") + ".xls");
                response.addHeader("Pargam", "no-cache");
                response.addHeader("Cache-Control", "no-cache");
                Collection c = listException;
                new ExportExcelUtil<>().exportExcel("巴士车辆导入错误Excel", new String[]{"车牌号","错误原因"}, c, response.getOutputStream());
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }else{
                return AjaxResponse.success(RestErrorCode.SUCCESS);
            }
        }catch(Exception e){
            e.printStackTrace();
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }*/
    }

    private Integer queryCooperationTypeBySupplierId(Integer supplierId){
        Integer cooperationType=5;
        try {
            CarBizSupplier supplier = new CarBizSupplier();
            supplier.setSupplierId(supplierId);
            CarBizSupplier suppliers = carBizSupplierExMapper.queryForObject(supplier);
            if(suppliers!=null){
                cooperationType = suppliers.getCooperationType();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导入司机  查询加盟类型queryCooperationTypeBySupplierId e:" + e);
        }
        return cooperationType;
    }

    private int queryAgreementCompanyByName(String name){
        AgreementCompany entity = new AgreementCompany();
        entity.setName(name);
        entity.setStatus(1);
        int count = agreementCompanyMapper.checkName(entity);
        return count;
    }

    private String querySupplierNameBySupplierId(Integer supplierId){
        String supplierFullName = "";
        try {
            CarBizSupplier supplier = new CarBizSupplier();
            supplier.setSupplierId(supplierId);
            CarBizSupplier suppliers = carBizSupplierExMapper.queryForObject(supplier);
            if(suppliers!=null){
                supplierFullName = suppliers.getSupplierFullName();
            }
        } catch (Exception e) {
            log.error("导入司机  查询加盟类型querySupplierNameBySupplierId e:" + e);
        }
        return supplierFullName;
    }

    /**
     * 添加
     * @param entity
     * @return
     */
    public AjaxResponse addSave(CarBizDriverInfoTemp entity) {
        try {
            if(entity.getIdCardNo()!=null&&!"".equals(entity.getIdCardNo())&&entity.getIdCardNo().length()==8){
                entity.setIdCardNo(entity.getIdCardNo().substring(0, 7)+"("+entity.getIdCardNo().substring(7, 8)+")");
            }
            String idCardNo = entity.getIdCardNo();
            if("X".equals(idCardNo.substring(idCardNo.length()-1,idCardNo.length()))){
                idCardNo = idCardNo.toLowerCase();
            }
            String mess = "";
            // 判断身份证号合法性
            mess = ValidateUtils.IDCardValidate(idCardNo);
            if (mess!=null&&!"".equals(mess)) {
                if(!Common.validateIdCarNo(idCardNo)){
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,mess);
                }
            }
            entity.setIdCardNo(idCardNo);
            CarBizDriverInfoTemp driverEntity = new CarBizDriverInfoTemp();
            driverEntity.setIdCardNo(idCardNo);
            Integer c = carBizDriverInfoExMapper.checkIdCardNoNew(driverEntity);
            if (c != null && c.intValue() > 0) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"身份证号已存在");
            }
            driverEntity.setDriverId(entity.getDriverId());
            Integer s = carBizDriverInfoTempExMapper.checkIdCardNo(driverEntity);
            if (s != null && s.intValue() > 0) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"身份证号已存在");
            }
            if(StringUtils.isNotEmpty(entity.getBankCardNumber())){
                if(carBizDriverInfoTempExMapper.validateBankCardNumber(entity) > 0){
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"银行卡卡号已被绑定");
                }
                if(carBizDriverInfoExMapper.validateBankCardNumber(entity) > 0){
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"银行卡卡号已被绑定");
                }
            }
            //加入加盟类型
            Integer cooperationType = queryCooperationTypeBySupplierId(entity.getSupplierId());
            entity.setCooperationType(cooperationType);
            if(cooperationType!=null&&cooperationType==5){
                int count = this.queryAgreementCompanyByName(entity.getCorpType());
                if(count==0){
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"【驾驶员合同（或协议）签署公司】在协议公司中不存在");
                }
            }
            CarBizDriverInfo carBizDriverInfo = new CarBizDriverInfo();
            carBizDriverInfo.setPhone(entity.getPhone());
            Integer integer = carBizDriverInfoExMapper.selectCountForPhone(carBizDriverInfo);
            if(integer > 0){
                return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
            }
            CarBizDriverInfoTemp driverInfoTemp = new CarBizDriverInfoTemp();
            driverInfoTemp.setPhone(entity.getPhone());
            Integer integer1 = carBizDriverInfoTempExMapper.selectCountForPhone(driverInfoTemp);
            if(integer1 > 0){
                return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
            }
            entity.setStatus(1);
            entity.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            entity.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
            log.info("新建");
            try {
                carBizDriverInfoTempMapper.insertSelective(entity);
                //车辆关联司机
                CarBizCarInfoTemp car = new CarBizCarInfoTemp();
                car.setLicensePlates(entity.getLicensePlates());
                car.setDriverid(Integer.parseInt(entity.getDriverId()));
                car.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
                carBizCarInfoTempExMapper.updateByLicensePlates(car);
                return AjaxResponse.success(RestErrorCode.SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("保存司机信息error:" + e);
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存司机信息error:" + e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    public AjaxResponse updateSave(CarBizDriverInfoTemp entity) {
        try {
            if(entity.getIdCardNo()!=null&&!"".equals(entity.getIdCardNo())&&entity.getIdCardNo().length()==8){
                entity.setIdCardNo(entity.getIdCardNo().substring(0, 7)+"("+entity.getIdCardNo().substring(7, 8)+")");
            }
            String idCardNo = entity.getIdCardNo();
            if("X".equals(idCardNo.substring(idCardNo.length()-1,idCardNo.length()))){
                idCardNo = idCardNo.toLowerCase();
            }
            String mess = "";
            // 判断身份证号合法性
            mess = ValidateUtils.IDCardValidate(idCardNo);
            if (mess!=null&&!"".equals(mess)) {
                if(!Common.validateIdCarNo(idCardNo)){
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,mess);
                }
            }
            entity.setIdCardNo(idCardNo);
            CarBizDriverInfoTemp driverEntity = new CarBizDriverInfoTemp();
            driverEntity.setIdCardNo(idCardNo);
            Integer c = carBizDriverInfoExMapper.checkIdCardNoNew(driverEntity);
            if (c != null && c.intValue() > 0) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"身份证号已存在");
            }
            driverEntity.setDriverId(entity.getDriverId());
            Integer s = carBizDriverInfoTempExMapper.checkIdCardNo(driverEntity);
            if (s != null && s.intValue() > 0) {
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"身份证号已存在");
            }
            if(StringUtils.isNotEmpty(entity.getBankCardNumber())){
                if(carBizDriverInfoTempExMapper.validateBankCardNumber(entity) > 0){
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"银行卡卡号已被绑定");
                }
                if(carBizDriverInfoExMapper.validateBankCardNumber(entity) > 0){
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"银行卡卡号已被绑定");
                }
            }
            //加入加盟类型
            Integer cooperationType = queryCooperationTypeBySupplierId(entity.getSupplierId());
            entity.setCooperationType(cooperationType);
            if(cooperationType!=null&&cooperationType==5){
                int count = this.queryAgreementCompanyByName(entity.getCorpType());
                if(count==0){
                    return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"【驾驶员合同（或协议）签署公司】在协议公司中不存在");
                }
            }
            if(!entity.getPhone().equals(entity.getOldPhone())){
                CarBizDriverInfo carBizDriverInfo = new CarBizDriverInfo();
                carBizDriverInfo.setPhone(entity.getPhone());
                Integer integer = carBizDriverInfoExMapper.selectCountForPhone(carBizDriverInfo);
                if(integer > 0){
                    return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
                }
                CarBizDriverInfoTemp driverInfoTemp = new CarBizDriverInfoTemp();
                driverInfoTemp.setPhone(entity.getPhone());
                Integer integer1 = carBizDriverInfoTempExMapper.selectCountForPhone(driverInfoTemp);
                if(integer1 > 0){
                    return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
                }
            }
            entity.setStatus(1);
            entity.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            log.info("临时司机修改:"+entity.toString());
            try {
                if((entity.getOldCityId()!=null&&!"".equals(entity.getOldCityId())&&!String.valueOf(entity.getCityId()).equals(entity.getOldCityId()))
                        ||(entity.getOldSupplierId()!=null&&!"".equals(entity.getOldSupplierId())&&!entity.getSupplierId().equals(entity.getOldSupplierId()))){
                    entity.setTeamid("");
                    entity.setGroupIds("");
                }
                carBizDriverInfoTempMapper.updateByPrimaryKeySelective(entity);
                CarBizCarInfoTemp car = new CarBizCarInfoTemp();
                car.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
                //判断 车牌号是否修改 如果修改 释放 车牌号
                if(entity.getLicensePlatesOld()!= null && entity.getLicensePlatesOld().length()>=1 && !entity.getLicensePlatesOld().equals(entity.getLicensePlates())){
                    log.info("****************修改车牌号 释放以前的车牌号");
                    //释放旧车
                    car.setLicensePlates(entity.getLicensePlatesOld());
                    car.setDriverid(0);
                    carBizCarInfoTempExMapper.updateByLicensePlates(car);
                }
                //绑定新车
                car.setLicensePlates(entity.getLicensePlates());
                car.setDriverid(Integer.parseInt(entity.getDriverId()));
                carBizCarInfoTempExMapper.updateByLicensePlates(car);
                return AjaxResponse.success(RestErrorCode.SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("修改司机信息error:" + e);
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("修改司机信息 error:" + e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     *查询未绑定车牌号
     * @param map
     * @return
     */
    public List<CarBizCarInfoTemp> licensePlatesNotDriverIdList(Map<String,Object> map) {
        return carBizCarInfoTempExMapper.licensePlatesNotDriverIdList(map);
    }
}