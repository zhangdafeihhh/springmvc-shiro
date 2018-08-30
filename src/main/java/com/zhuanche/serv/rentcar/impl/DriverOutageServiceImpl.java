package com.zhuanche.serv.rentcar.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.entity.rentcar.DriverOutageVo;
import com.zhuanche.serv.rentcar.DriverOutageService;
import mapper.rentcar.ex.DriverOutageExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DriverOutageServiceImpl implements DriverOutageService {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverOutageServiceImpl.class);

    @Autowired
    private DriverOutageExMapper DriverOutageExMapper;

//    @Autowired
//    @Qualifier("carApiTemplate")
//    private MyRestTemplate carApiTemplate;

    @Override
    public List<DriverOutage> queryForListObject(DriverOutage params) {
        return DriverOutageExMapper.queryForListObject(params);
    }

    @Override
    public int queryForInt(DriverOutage params) {
        return DriverOutageExMapper.queryForInt(params);
    }

    @Override
    public DriverOutage queryForObject(DriverOutage params) {
        return DriverOutageExMapper.queryForObject(params);
    }

    @Override
    public List<DriverOutage> queryForListObjectNoLimit(
            DriverOutage params) {
        return DriverOutageExMapper.queryForListObjectNoLimit(params);
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
        Map<String, Object> result = new HashMap<String, Object>();
        // 调接口，保存
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            params.setOutageSource(2);//人工停运
            params.setRemoveStatus(3);//待执行
            params.setCreateBy(ToolUtils.getSecurityUser().getUserId());
            params.setCreateName(ToolUtils.getSecurityUser().getUsername());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.setOutStartDate(sdf.parse(params.getOutStartDateStr()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        String url = "/webservice/outage/saveDriverOutage";
//        result = carApiTemplate.postForObject(url, JSONObject.class, paramMap);
        Map<String,Object> result = new HashMap<String,Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("【临时停运】 新增  BEGIN***********************");
        logger.info("【临时停运】操作时间：" + sdf.format(new Date()));
        try {
            DriverOutage outage = this.DriverOutageService.queryDriverOutageAllByDriverId(params);
            if(outage!=null){
                result.put("result", 0);
                result.put("exception", "该司机已存在启用的永久停运！");
            }else{
                int total = DriverOutageService.checkDriverOutageStartDate(out);
                if(total>0){
                    logger.info("【临时停运】司机id="+out.getDriverId()+"已存在改时间段停运信息="+out.toString());
                    result.put("result", 0);
                    result.put("exception", "已存在改时间段停运信息="+out.toString());
                }else{
                    logger.info("【临时停运】司机id="+out.getDriverId()+"不存在改时间段停运信息="+out.toString());

                    DriverOutageService.insertDriverOutage(out);

                    /*
                     * 该司机是否有正在执行的停运，如果有，不管，
                     * 如果没有，则查找数据库中最早需要停运的数据，与当前时间对比，
                     * 如果已过，更新mongo停运标识为存在，并且把开始时间设为当前时间，同时更新数据库
                     * 如果还无需开始，则更新mongo停运标识为不存在
                     */
                    setDriverOutageMongo(out,1);

                    result.put("result", 1);
                }
            }
            logger.info("【临时停运】新增  END***********************");
        } catch (Exception e) {
            result.put("result", 0);
            result.put("exception", e.getMessage());
            logger.info("【临时停运】新增  error e:"+e);
        }
        return result;
        return result;
    }

    @Override
    public Map<String,Object> updateDriverOutage(DriverOutage params){
        Map<String, Object> result = new HashMap<String, Object>();
        // 调接口，保存
        Map<String, Object> paramMap = new HashMap<String, Object>();
        params.setRemoveBy(ToolUtils.getSecurityUser().getUserId());
        params.setRemoveName(ToolUtils.getSecurityUser().getUsername());
        JSONObject json = JSONObject.fromObject(params);
        paramMap.put("DriverOutageInfo", json);
        String url = "/webservice/outage/updateDriverOutage";
        result = carApiTemplate.postForObject(url, JSONObject.class, paramMap);
        return result;
    }

    @Override
    public Map<String,Object> updateDriverOutages(DriverOutage params){
        Map<String, Object> result = new HashMap<String, Object>();
        // 调接口，保存
        Map<String, Object> paramMap = new HashMap<String, Object>();
        params.setRemoveBy(ToolUtils.getSecurityUser().getUserId());
        params.setRemoveName(ToolUtils.getSecurityUser().getUsername());
        JSONObject json = JSONObject.fromObject(params);
        paramMap.put("DriverOutageInfo", json);
        String url = "/webservice/outage/updateDriverOutages";
        result = carApiTemplate.postForObject(url, JSONObject.class, paramMap);
        return result;
    }

    @Override
    public Map<String, Object> checkDriverOutageStartDate(
            DriverOutage params) {
        Map<String, Object> result = new HashMap<String, Object>();
        return result;
    }

    @Override
    public DriverOutage queryDriverNameByPhone(DriverOutage params){
        return DriverOutageExMapper.queryDriverNameByPhone(params);
    }

    public static void main(String[] args) {
        String date1 = "2017-05-06 23:50:00";// 指定时间
        String date2 = addDate(date1, 15);// 加1小时方法

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
    public List<DriverOutage> queryAllForListObject(DriverOutage params){
        return DriverOutageExMapper.queryAllForListObject(params);
    }
    /*
     * 查询数量
     */
    @Override
    public int queryAllForInt(DriverOutage params){
        return DriverOutageExMapper.queryAllForInt(params);
    }
    //根据司机id，查询司机临时停运，正在执行或者待执行的数据
    @Override
    public List<DriverOutage> queryDriverOutageByDriverId(DriverOutage params){
        return DriverOutageExMapper.queryDriverOutageByDriverId(params);
    }
    //根据司机id，查询司机永久停运，启用的数据
    @Override
    public DriverOutage queryDriverOutageAllByDriverId(DriverOutage params){
        return DriverOutageExMapper.queryDriverOutageAllByDriverId(params);
    }
    /*
     * 保存
     */
    @Override
    public Map<String,Object> saveDriverOutageAll(DriverOutage params){
        Map<String, Object> result = new HashMap<String, Object>();
        // 调接口，保存
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            params.setOutageSource(2);//人工停运
            params.setRemoveStatus(1);//待执行
            params.setCreateBy(ToolUtils.getSecurityUser().getUserId());
            params.setCreateName(ToolUtils.getSecurityUser().getUsername());
            params.setOutStartDate(new Date());
        } catch (Exception e) {
            logger.info("saveDriverOutageAll error:"+e);
        }
        JSONObject json = JSONObject.fromObject(params);
        paramMap.put("DriverOutageInfo", json);
        String url = "/webservice/outageAll/saveDriverOutageAll";
        result = carApiTemplate.postForObject(url, JSONObject.class, paramMap);
        return result;
    }

    @Override
    public Map<String,Object> updateDriverOutagesAll(DriverOutage params){
        Map<String, Object> result = new HashMap<String, Object>();
        // 调接口，保存
        Map<String, Object> paramMap = new HashMap<String, Object>();
        params.setRemoveBy(ToolUtils.getSecurityUser().getUserId());
        params.setRemoveName(ToolUtils.getSecurityUser().getUsername());
        JSONObject json = JSONObject.fromObject(params);
        paramMap.put("DriverOutageInfo", json);
        String url = "/webservice/outageAll/updateDriverOutagesAll";
        result = carApiTemplate.postForObject(url, JSONObject.class, paramMap);
        return result;
    }

    @Override
    public Map<String,Object> importDriverOutageInfo(DriverOutageVo params, HttpServletRequest request){
        String resultError1 = "-1";// 模板错误
        String resultErrorMag1 = "导入模板格式错误!";

        Map<String, Object> result = new HashMap<String, Object>();
        List<CarImportExceptionEntity> listException = new ArrayList<CarImportExceptionEntity>();
        CustomUserDetails user = ToolUtils.getSecurityUser();
        List<DriverOutage> outList = new ArrayList<DriverOutage>();
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
            FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            for (int colIx = 0; colIx < 4; colIx++) {
                Cell cell = row1.getCell(colIx); // 获取列对象
                CellValue cellValue = evaluator.evaluate(cell); // 获取列属性
                if (cell == null || cellValue == null) {
                    result.put("result", resultError1);
                    result.put("msg", resultErrorMag1);
                    return result;
                } else {
                    switch ((colIx + 1)) {
                        case 1:
                            if (!cellValue.getStringValue().contains("序号")) {
                                result.put("result", resultError1);
                                result.put("msg", resultErrorMag1);
                                return result;
                            }
                            break;
                        case 2:
                            if (!cellValue.getStringValue().contains("司机姓名")) {
                                result.put("result", resultError1);
                                result.put("msg", resultErrorMag1);
                                return result;
                            }
                            break;
                        case 3:
                            if (!cellValue.getStringValue().contains("司机手机号")) {
                                result.put("result", resultError1);
                                result.put("msg", resultErrorMag1);
                                return result;
                            }
                            break;
                        case 4:
                            if (!cellValue.getStringValue().contains("停运原因")) {
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

            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // 获取行对象
                if (row == null) {
                    continue;
                }
                DriverOutage outage = new DriverOutage();
                outage.setCreateBy(user.getUserId());
                outage.setCreateName(user.getUsername());
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
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
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
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【司机手机号】不能为空且单元格格式必须为文本");
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
                                String phone = cellValue.getStringValue();
                                DriverOutage driverout = new DriverOutage();
                                driverout.setDriverPhone(phone);
                                //查数据
                                DriverOutage hasout = this.queryDriverNameByPhone(driverout);
                                if(hasout!=null){
                                    if(hasout.getDriverName()!=null&&!"".equals(hasout.getDriverName())
                                            &&outage.getDriverName()!=null&&!"".equals(outage.getDriverName())
                                            &&!hasout.getDriverName().equals(outage.getDriverName())){
                                        CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                        returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                                + "列 【驾驶员手机】司机手机号和姓名匹配不上");
                                        listException.add(returnVO);
                                        isTrue = false;
                                    }else{
                                        outage.setDriverPhone(phone);
                                        outage.setDriverId(hasout.getDriverId());
                                        outage.setOutStartDate(new Date());
                                        outage.setRemoveStatus(1);
                                        outage.setOutageSource(2);
                                    }
                                }else{
                                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                    returnVO.setReson("第" +  (rowIx+1) + "行数据，第" + (colIx + 1)
                                            + "列 【驾驶员手机】司机手机号不存在");
                                    listException.add(returnVO);
                                    isTrue = false;
                                }
                            }
                            break;
                        // 停运原因
                        case 4:
                            if (cellValue == null|| StringUtils.isEmpty(cellValue.getStringValue())) {
                                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                                returnVO.setReson("第" + (rowIx + 1) + "行数据，第"
                                        + (colIx + 1) + "列 【停运原因】不能为空且单元格格式必须为文本");
                                listException.add(returnVO);
                                isTrue = false;
                            } else {
                                outage.setOutageReason(cellValue.getStringValue());
                            }
                            break;
                    }// switch end

                }// 循环列结束
                if (isTrue && outage != null) {
                    outList.add(outage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String download = "";
        if ("".equals(outList) || outList == null || outList.size() == 0) {
            result.put("result", "0");
            result.put("msg", "导入失败！");
        } else {
            // 调接口，保存
            Map<String, Object> paramMap = new HashMap<String, Object>();
            JSONArray jsonarray = JSONArray.fromObject(outList);
            String cars = jsonarray.toString();
            paramMap.put("DriverOutageList", cars);
            String url = "/webservice/outageAll/batchInputDriverOutageInfo";
            JSONObject jsonobject = carApiTemplate.postForObject(url,JSONObject.class, paramMap);
            // 返回为0 ==========不成功
            if ((int) jsonobject.get("result") == 0) {
                logger.info("接口返回为0 ==========导入不成功");
                CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                returnVO.setReson((String) jsonobject.get("exception"));
                listException.add(returnVO);
                result.put("result", "0");
                result.put("msg", "导入失败！");
            } else {
                // 返回为1 ==========成功
                JSONObject json = (JSONObject) jsonobject.get("jsonStr");
                String arrayDriverName = (String) json.get("error");
                if (!"".equals(arrayDriverName) && arrayDriverName != null) {
                    CarImportExceptionEntity returnVO = new CarImportExceptionEntity();
                    returnVO.setReson(arrayDriverName + "导入失败！");
                    listException.add(returnVO);
                }
                result.put("result", "1");
                result.put("msg","");
            }
        }
        try {
            // 将错误列表导出
            if (listException.size() > 0) {
                Workbook wb = Common.exportExcel(request.getServletContext().getRealPath("/")+ "template" + File.separator + "car_exception.xlsx", listException);
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

        return result;
    }
}
