package com.zhuanche.serv.rentcar.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.rentcar.CarImportExceptionEntity;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.ValidateUtils;
import mapper.rentcar.ex.DriverOutageExMapper;
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
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DriverOutageServiceImpl implements DriverOutageService {

    private static final Logger logger = LoggerFactory.getLogger(DriverOutageServiceImpl.class);

    @Autowired
    private DriverOutageExMapper driverOutageExMapper;

    @Value("${mp.restapi.url}")
    private String mpReatApiUrl;

    @Override
//    @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    public List<DriverOutage> queryForListObject(DriverOutage params) {
        return driverOutageExMapper.queryForListObject(params);
    }

    @Override
    public int queryForInt(DriverOutage params) {
        logger.info("查询停运司机总数，参数为："+(params==null?"null":JSON.toJSONString(params)));
        int count =  driverOutageExMapper.queryForInt(params);
        logger.info("查询停运司机总数，返回结果为:"+count+",参数为："+(params==null?"null":JSON.toJSONString(params)));
        return count;
    }

    @Override
    public DriverOutage queryForObject(DriverOutage params) {
        return driverOutageExMapper.queryForObject(params);
    }

    @Override
    public List<DriverOutage> queryForListObjectNoLimit(
            DriverOutage params) {
        return driverOutageExMapper.queryForListObjectNoLimit(params);
    }

    @Override
    public Workbook exportExcelDriverOutage(List<DriverOutage> list,
                                            String path) throws Exception {
        FileInputStream io = new FileInputStream(path);
        // 创建 excel
        Workbook wb = new XSSFWorkbook(io);
        if (list != null && list.size() > 0) {
            Sheet sheet = null;
            try {
                sheet = wb.getSheetAt(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cell cell = null;
            int i = 0;
            for (DriverOutage s : list) {
                Row row = sheet.createRow(i + 1);
                // 城市
                cell = row.createCell(0);
                cell.setCellValue(s.getCityName() != null ? ""
                        + s.getCityName() + "" : "");
                // 司机姓名
                cell = row.createCell(1);
                cell.setCellValue(s.getDriverName() != null ? ""
                        + s.getDriverName() + "" : "");
                // 手机号
                cell = row.createCell(2);
                cell.setCellValue(s.getDriverPhone() != null ? ""
                        + s.getDriverPhone() + "" : "");
                // 供应商
                cell = row.createCell(3);
                cell.setCellValue(s.getSupplierName() != null ? ""
                        + s.getSupplierName() + "" : "");
                // 车型类别
                cell = row.createCell(4);
                cell.setCellValue(s.getCarGroupName() != null ? ""
                        + s.getCarGroupName() + "" : "");
                // 车牌
                cell = row.createCell(5);
                cell.setCellValue(s.getLicensePlates() != null ? ""
                        + s.getLicensePlates() + "" : "");
                // 开始停运时间
                cell = row.createCell(6);
                cell.setCellValue(s.getOutStartDateStr() != null ? ""
                        + s.getOutStartDateStr() + "" : "");
                // 停运时长(h)
                cell = row.createCell(7);
                cell.setCellValue(s.getOutStopLongTime() != null ? ""
                        + s.getOutStopLongTime() + "" : "");
                // 解除停运时间
                cell = row.createCell(8);
                cell.setCellValue(s.getOutEndDateStr() != null ? ""
                        + s.getOutEndDateStr() + "" : "");
                // 实际开始停运时间
                cell = row.createCell(9);
                cell.setCellValue(s.getFactStartDateStr() != null ? ""
                        + s.getFactStartDateStr() + "" : "");
                // 实际解除停运时间
                cell = row.createCell(10);
                cell.setCellValue(s.getFactEndDateStr() != null ? ""
                        + s.getFactEndDateStr() + "" : "");
                // 停运来源 :1系统停运 2人工停运
                cell = row.createCell(11);
                cell.setCellValue(s.getOutageSource() != null ? (s
                        .getOutageSource() == 1 ? "系统停运" : "人工停运") : "");
                // 停运原因
                cell = row.createCell(12);
                cell.setCellValue(s.getOutageReason() != null ? ""
                        + s.getOutageReason() + "" : "");
                // 创建人
                cell = row.createCell(13);
                cell.setCellValue(s.getCreateName() != null ? ""
                        + s.getCreateName() + "" : "");
                // 创建时间
                cell = row.createCell(14);
                cell.setCellValue(s.getCreateDateStr() != null ? ""
                        + s.getCreateDateStr() + "" : "");
                // 解除人
                cell = row.createCell(15);
                cell.setCellValue(s.getRemoveName() != null ? ""
                        + s.getRemoveName() + "" : "");
                // 解除原因
                cell = row.createCell(16);
                cell.setCellValue(s.getRemoveReason() != null ? ""
                        + s.getRemoveReason() + "" : "");
                // 停运状态 1：已执行 2：执行中 3：待执行 4：撤销
                Integer removeStatus = s.getRemoveStatus();
                String removeStatusName = "待执行";
                if (removeStatus == 1) {
                    removeStatusName = "已执行";
                } else if (removeStatus == 2) {
                    removeStatusName = "执行中";
                } else if (removeStatus == 3) {
                    removeStatusName = "待执行";
                } else if (removeStatus == 4) {
                    removeStatusName = "撤销";
                }
                cell = row.createCell(17);
                cell.setCellValue(removeStatusName);
                // 解除时间
                cell = row.createCell(18);
                cell.setCellValue(s.getRemoveDateStr() != null ? ""
                        + s.getRemoveDateStr() + "" : "");

                i++;
            }
        }
        return wb;
    }

    @Override
    public Map<String, Object> saveDriverOutage(DriverOutage params) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("driverId", params.getDriverId());
        paramMap.put("outStartDate", params.getOutStartDateStr());
        paramMap.put("outStopLongTime", params.getOutStopLongTime());
        paramMap.put("outageSource", 2);//人工停运
        paramMap.put("outageReason", params.getOutageReason());
        paramMap.put("createBy", WebSessionUtil.getCurrentLoginUser().getId());
        paramMap.put("createName", WebSessionUtil.getCurrentLoginUser().getName());

        String url = mpReatApiUrl + Common.SAVE_DRIVER_OUTAGE;
        String jsonObjectStr = null;
        try {
            jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
        } catch (HttpException e) {
            logger.info("临时停运调用接口 paramMap={},error={}", paramMap, e);
        }
        logger.info("临时停运调用新增接口 paramMap={}, result={}", paramMap, jsonObjectStr);
        JSONObject jsonObject = JSON.parseObject(jsonObjectStr);

        if(jsonObject!=null && jsonObject.containsKey("code")){
            if(jsonObject.getIntValue("code")==0) {
                result.put("result", 1);
                result.put("exception", "成功！");
            }else{
                result.put("result", 0);
                result.put("exception", jsonObject.get("msg"));
            }
        }else{
            result.put("result", 0);
            result.put("exception", "调用接口停运失败！");
        }
        return result;
    }

    @Override
    public Map<String, Object> updateDriverOutage(DriverOutage params) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("outageId", params.getOutageId());
        paramMap.put("removeReason", params.getRemoveReason());
        paramMap.put("removeBy", WebSessionUtil.getCurrentLoginUser().getId());
        paramMap.put("removeName", WebSessionUtil.getCurrentLoginUser().getName());

        String url = mpReatApiUrl + Common.UPDATE_DRIVER_OUTAGE;
        String jsonObjectStr = null;
        try {
            jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
        } catch (HttpException e) {
            logger.info("临时停运调用接口 paramMap={},error={}", paramMap, e);
        }
        logger.info("临时停运调用解除接口 paramMap={}, result={}", paramMap, jsonObjectStr);
        JSONObject jsonObject = JSON.parseObject(jsonObjectStr);

        if(jsonObject!=null && jsonObject.containsKey("code")){
            if(jsonObject.getIntValue("code")==0) {
                result.put("result", 1);
                result.put("exception", "成功！");
            }else{
                result.put("result", 0);
                result.put("exception", jsonObject.get("msg"));
            }
        }else{
            result.put("result", 0);
            result.put("exception", "调用接口解除停运失败！");
        }
        return result;
    }


    @Override
    public Map<String, Object> updateDriverOutages(DriverOutage params) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("outageIds", params.getOutageIds());
        paramMap.put("removeReason", params.getRemoveReason());
        paramMap.put("removeBy", WebSessionUtil.getCurrentLoginUser().getId());
        paramMap.put("removeName", WebSessionUtil.getCurrentLoginUser().getName());

        String url = mpReatApiUrl + Common.UPDATE_DRIVER_OUTAGES;
        String jsonObjectStr = null;
        try {
            jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
        } catch (HttpException e) {
            logger.info("临时停运调用接口 paramMap={},error={}", paramMap, e);
        }
        logger.info("临时停运调用解除接口 paramMap={}, result={}", paramMap, jsonObjectStr);
        JSONObject jsonObject = JSON.parseObject(jsonObjectStr);

        if(jsonObject!=null && jsonObject.containsKey("code")){
            if(jsonObject.getIntValue("code")==0) {
                result.put("result", 1);
                result.put("exception", "成功！");
            }else{
                result.put("result", 0);
                result.put("exception", jsonObject.get("msg"));
            }
        }else{
            result.put("result", 0);
            result.put("exception", "调用接口解除停运失败！");
        }
        return result;
    }

    @Override
    public Map<String, Object> checkDriverOutageStartDate(
            DriverOutage params) {
        Map<String, Object> result = new HashMap<String, Object>();
        return result;
    }

    @Override
    public DriverOutage queryDriverNameByPhone(DriverOutage params) {
        return driverOutageExMapper.queryDriverNameByPhone(params);
    }

    public static String addDate(String day, int x) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null)
            return "";
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(date);
        nowTime.add(Calendar.MINUTE, x);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(nowTime.getTime());
    }

    //永久停运
    /*
     * 查询列表，有分页
     */
    @Override
    public List<DriverOutage> queryAllForListObject(DriverOutage params) {
        return driverOutageExMapper.queryAllForListObject(params);
    }

    /*
     * 查询数量
     */
    @Override
    public int queryAllForInt(DriverOutage params) {
        return driverOutageExMapper.queryAllForInt(params);
    }

    //根据司机id，查询司机临时停运，正在执行或者待执行的数据
    @Override
    public List<DriverOutage> queryDriverOutageByDriverId(DriverOutage params) {
        return driverOutageExMapper.queryDriverOutageByDriverId(params);
    }

    //根据司机id，查询司机永久停运，启用的数据
    @Override
    public DriverOutage queryDriverOutageAllByDriverId(DriverOutage params) {
        return driverOutageExMapper.queryDriverOutageAllByDriverId(params);
    }

    /*
     * 保存
     */
    @Override
    public Map<String, Object> saveDriverOutageAll(DriverOutage params) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("driverId", params.getDriverId());
        paramMap.put("outageSource", 2);//人工停运
        paramMap.put("outageReason", params.getOutageReason());
        paramMap.put("createBy", WebSessionUtil.getCurrentLoginUser().getId());
        paramMap.put("createName", WebSessionUtil.getCurrentLoginUser().getName());

        String url = mpReatApiUrl + Common.SAVE_DRIVER_OUTAGE_ALL;
        String jsonObjectStr = null;
        try {
            jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
        } catch (HttpException e) {
            logger.info("永久停运调用接口 paramMap={},error={}", paramMap, e);
        }
        logger.info("永久停运调用新增接口 paramMap={}, result={}", paramMap, jsonObjectStr);
        JSONObject jsonObject = JSON.parseObject(jsonObjectStr);

        if(jsonObject!=null && jsonObject.containsKey("code")){
            if(jsonObject.getIntValue("code")==0) {
                result.put("result", 1);
                result.put("exception", "成功！");
            }else{
                result.put("result", 0);
                result.put("exception", jsonObject.get("msg"));
            }
        }else{
            result.put("result", 0);
            result.put("exception", "调用接口解除停运失败！");
        }
        return result;

    }

    @Override
    public Map<String, Object> updateDriverOutagesAll(DriverOutage params) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("outageIds", params.getOutageIds());
        paramMap.put("removeReason", params.getRemoveReason());
        paramMap.put("removeBy", WebSessionUtil.getCurrentLoginUser().getId());
        paramMap.put("removeName", WebSessionUtil.getCurrentLoginUser().getName());

        String url = mpReatApiUrl + Common.UPDATE_DRIVER_OUTAGE_ALL;
        String jsonObjectStr = null;
        try {
            jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
        } catch (HttpException e) {
            logger.info("永久停运调用接口 paramMap={},error={}", paramMap, e);
        }
        logger.info("永久停运调用解除接口 paramMap={}, result={}", paramMap, jsonObjectStr);
        JSONObject jsonObject = JSON.parseObject(jsonObjectStr);

        if(jsonObject!=null && jsonObject.containsKey("code")){
            if(jsonObject.getIntValue("code")==0) {
                result.put("result", 1);
                result.put("exception", "成功！");
            }else{
                result.put("result", 0);
                result.put("exception", jsonObject.get("msg"));
            }
        }else{
            result.put("result", 0);
            result.put("exception", "调用接口解除停运失败！");
        }
        return result;
    }

    @Override
    public AjaxResponse importDriverOutageInfo(String fileName, MultipartFile file, HttpServletRequest request) {
        String resultError1 = "-1";// 模板错误
        String resultErrorMag1 = "导入模板格式错误!";


//        Map<String, Object> result = new HashMap<String, Object>();
        List<CarImportExceptionEntity> listException = new ArrayList<CarImportExceptionEntity>();
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        List<DriverOutage> outList = new ArrayList<DriverOutage>();
        List<Map<String, Object>> outageMapList = new ArrayList<Map<String, Object>>();
//        String fileName = params.getFileName();
//        String path  = Common.getPath(request);
//        String dirPath = path+params.getFileName();
//        File DRIVERINFO = new File(dirPath);
        try {
//            InputStream is = new FileInputStream(DRIVERINFO);
            Workbook workbook = null;
            String fileType = fileName.split("\\.")[1];
            if (fileType.equals("xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            for (int colIx = 0; colIx < 4; colIx++) {
                Cell cell = row1.getCell(colIx); // 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    return AjaxResponse.fail(5000, resultErrorMag1);
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("序号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "序号列不正确");
                            }
                            break;
                        case 2:
                            if (!cellValue.getStringValue().contains("司机姓名")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "司机姓名列不正确");
                            }
                            break;
                        case 3:
                            if (!cellValue.getStringValue().contains("司机手机号")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "序号列不正确");
                            }
                            break;
                        case 4:
                            if (!cellValue.getStringValue().contains("停运原因")) {
                                return AjaxResponse.fail(RestErrorCode.FILE_IMPORT_ERROR, "停运原因列不正确");
                            }
                            break;
                    }
                }
            }

            int minRowIx = 1;// 过滤掉标题，从第一行开始导入数据
            int maxRowIx = sheet.getLastRowNum(); // 要导入数据的总条数
            Map<String, Object> outageMap = Maps.newHashMap();

            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // 获取行对象
                if (row == null) {
                    continue;
                }
                DriverOutage outage = new DriverOutage();
                outage.setCreateBy(currentLoginUser.getId());
                outage.setCreateName(currentLoginUser.getLoginName());
                outage.setCreateDate(new Date());
                boolean isTrue = true;// 标识是否为有效数据
                // 导入模板总共4 列
                for (int colIx = 0; colIx < 4; colIx++) {
                    Cell cell = row.getCell(colIx); // 获取列对象
                    CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                    switch ((colIx + 1)) {
                        // 车牌号
                        case 1:
                            //logger.info("序号："+cellValue.getStringValue());
                            break;
                        // 司机姓名
                        case 2:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【司机姓名】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                outage.setDriverName(cellValue.getStringValue());
                            }
                            break;
                        // 司机手机号
                        case 3:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【司机手机号】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            }
                            // 判断手机号码合法性
                            else if (!ValidateUtils.validatePhone(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                        + "列 【驾驶员手机】不合法");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String phone = cellValue.getStringValue();
                                DriverOutage driverout = new DriverOutage();
                                driverout.setDriverPhone(phone);
                                //查数据
                                DriverOutage hasout = this.queryDriverNameByPhone(driverout);
                                if (hasout != null) {
                                    if (hasout.getDriverName() != null && !"".equals(hasout.getDriverName())
                                            && outage.getDriverName() != null && !"".equals(outage.getDriverName())
                                            && !hasout.getDriverName().equals(outage.getDriverName())) {
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                                + "列 【驾驶员手机】司机手机号和姓名匹配不上");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    } else {
                                        outage.setDriverPhone(phone);
                                        outage.setDriverId(hasout.getDriverId());
                                        outage.setOutStartDate(new Date());
                                        outage.setRemoveStatus(1);
                                        outage.setOutageSource(2);
                                        outageMap.put("driverId", hasout.getDriverId());
                                    }
                                } else {
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" + (rowIx + 1) + "行数据，第" + (colIx + 1)
                                            + "列 【驾驶员手机】司机手机号不存在");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 停运原因
                        case 4:
                            if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【停运原因】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                String outageReason = Common.replaceBlank(cellValue.getStringValue());
                                outage.setOutageReason(outageReason);
                                outageMap.put("outageReason", outageReason);
                            }
                            break;
                    }// switch end

                }// 循环列结束
                if (isTrue && outage != null) {
                    outList.add(outage);
                    outageMapList.add(outageMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean flag = false;
        if ("".equals(outList) || outList == null || outList.size() == 0) {

        } else {
            Map<String, Object> paramMap = Maps.newHashMap();
            JSONArray jsonarray = JSONArray.parseArray(outageMapList.toString());
            String driverOutage = jsonarray.toString();
            paramMap.put("driverOutage", driverOutage);
            paramMap.put("outageSource", 2);//人工停运
            paramMap.put("createBy", WebSessionUtil.getCurrentLoginUser().getId());
            paramMap.put("createName", WebSessionUtil.getCurrentLoginUser().getName());

            String url = mpReatApiUrl + Common.BATH_INPUT_DRIVER_OUTAGE_ALL;
            String jsonObjectStr = null;
            try {
                jsonObjectStr = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
            } catch (HttpException e) {
                logger.info("永久停运调用接口 paramMap={},error={}", paramMap, e);
            }
            logger.info("永久停运调用新增接口 paramMap={}, result={}", paramMap, jsonObjectStr);
            JSONObject jsonObject = JSON.parseObject(jsonObjectStr);

            if(jsonObject!=null && jsonObject.containsKey("code")){
                if(jsonObject.getIntValue("code")==0) {// 返回为0 ==========成功
                    JSONObject json = (JSONObject) jsonObject.get("data");
                    String arrayDriverName = (String) json.get("error");
                    if (!"".equals(arrayDriverName) && arrayDriverName != null) {
                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                        returnVO.setReson(arrayDriverName + "导入失败！");
                        listException.add(returnVO);
                    }
                    flag = true;
                }else{
                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                    returnVO.setReson(jsonObject.get("msg") + "导入失败！");
                    listException.add(returnVO);
                }
            }else{
                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                returnVO.setReson("调用接口解除停运失败");
                listException.add(returnVO);
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
}
