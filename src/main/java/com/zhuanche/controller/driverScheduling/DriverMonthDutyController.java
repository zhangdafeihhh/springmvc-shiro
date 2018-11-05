package com.zhuanche.controller.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.dutyEnum.EnumDriverMonthDutyStatus;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.dto.driverDuty.CarDriverMonthDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMonthDuty;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DriverMonthDutyRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.driverScheduling.DriverMonthDutyService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import com.zhuanche.util.PageUtils;
import com.zhuanche.util.dateUtil.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @description: 司机排班
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author lunan
 * @version 1.0
 * @since 1.0
 * @create: 2018-08-29 16:54
 *
 */
@Controller
@RequestMapping("/saas/driverMonthDuty")
public class DriverMonthDutyController {

    private static final Logger logger = LoggerFactory.getLogger(DriverMonthDutyController.class);

    @Autowired
    private DriverMonthDutyService driverMonthDutyService;

    @Autowired
    private CitySupplierTeamCommonService commonService;

    /**
    * @Desc: 导入模板数据 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/importDriverMonthDutyInfo")
    public AjaxResponse importDriverMonthDutyInfo(DriverMonthDutyRequest params,HttpServletRequest request,MultipartFile file){
        //TODO
        logger.info("导入月排班模板数据入参："+JSON.toJSONString(params));
        if("".equals(params.getMonitorDate())||params.getMonitorDate()==null||"null".equals(params.getMonitorDate())){
            logger.error("importDriverMonthDutyInfo:导入 司机月排行 数据-失败[统计月不能为空]");
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        logger.info("每个司机一个月每天的排班导入保存,参数:"+params.toString());
//            Map<String, Object> result = this.driverMonthDutyService.importDriverMonthDuty(params, request);

        if (file == null) {
            return AjaxResponse.fail(RestErrorCode.FILE_ERROR);
        }
        /*CommonsMultipartFile commonsmultipartfile = (CommonsMultipartFile) file;
        DiskFileItem diskFileItem = (DiskFileItem) commonsmultipartfile.getFileItem();
        File newFile = diskFileItem.getStoreLocation();
        logger.info("导入月排班数据文件名:"+newFile.getName());*/
        Map<String, Object> result = this.driverMonthDutyService.importDriverMonthDuty(params, request,file);
        if(result.get("result").equals("1")){
            return AjaxResponse.success(result);
        }else{
            AjaxResponse fail = AjaxResponse.fail(RestErrorCode.RECORD_DEAL_FAILURE);
            fail.setData(result);
            return fail;
        }
    }



    /** 
    * @Desc: 修改司机上班状态 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/updateDriverMonthDutyData")
    public AjaxResponse updateDriverMonthDutyData(DriverMonthDutyRequest param){
        if(Check.NuNObj(param) || Check.NuNObj(param.getId()) || Check.NuNStr(param.getData())){
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        logger.info("修改司机上班状态入参:{}",JSON.toJSONString(param));
        CarDriverMonthDuty exists = this.driverMonthDutyService.selectByPrimaryKey(param.getId());
        if (Check.NuNObj(exists)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_NOT_FOUND);
        } else {
            // 只能修改当天及之后的排班
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            String nowDay = sdf.format(new Date()); // 今天日期
            String newData = param.getData();
            String oldData = exists.getData();
            int index = newData.indexOf(nowDay);
            boolean hasError = false;
            if (index >= 0) {
                int oldIndex = oldData.indexOf(nowDay);
                if (oldIndex >= 0) {
                    String data = oldData.substring(0, oldIndex) + newData.substring(index);
                    exists.setData(data);
                } else { // 旧排班数据没有当天的排班
                    hasError = true;
                }
            } else { // 新排班数据没有当天的排班
                exists.setData(newData);
            }
            if (hasError) {
                return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
            }
            return AjaxResponse.success(this.driverMonthDutyService.updateDriverMonthDutyData(exists));
        }
    }

    /** 
    * @Desc: 导出符合条件数据 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/exportDriverMonthDuty")
    public void exportDriverMonthDuty(DriverMonthDutyRequest param, HttpServletRequest request,HttpServletResponse response){


        try {
            if(Check.NuNStr(param.getMonitorDate())){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                param.setMonitorDate(sdf.format(new Date()));
            }
            CommonRequest commonRequest = new CommonRequest();
            BeanUtils.copyProperties(param,commonRequest);
            if(!Check.NuNStr(param.getTeamId())){
                commonRequest.setTeamId(Integer.parseInt(param.getTeamId()));
            }
            CommonRequest data = commonService.paramDeal(commonRequest);
            if(Check.NuNObj(data)){
                logger.error("没有权限操作,用户："+JSON.toJSONString(WebSessionUtil.getCurrentLoginUser()));
                return ;
            }
            param.setPageSize(10000);
            param.setCityIds(commonService.setStringShiftInteger(data.getCityIds()));
            param.setSupplierIds(commonService.setStringShiftInteger(data.getSupplierIds()));
            param.setTeamIds(data.getTeamIds());

            List<String> csvDataList = new ArrayList<>();
            long start = System.currentTimeMillis();
            PageInfo<CarDriverMonthDTO> pageInfo= driverMonthDutyService.queryDriverDutyList(param);
            List<CarDriverMonthDTO> pageList = pageInfo.getList();


            List<JSONObject>  headerList=  driverMonthDutyService.generateTableHeader(param.getMonitorDate());
            dataTrans(pageList, csvDataList,headerList);
            //计算总页数
            Integer totalPage = pageInfo.getPages();
            for(int pageNumber = 2; pageNumber <= totalPage; pageNumber++){
                param.setPageNo(pageNumber);
                pageInfo = driverMonthDutyService.queryDriverDutyList(param);
                dataTrans( pageInfo.getList(),  csvDataList,headerList);
            }
            List<String> csvheaderList = new ArrayList<>();

            StringBuffer stringBuffer = new StringBuffer();
            for(JSONObject item : headerList){
                stringBuffer.append(item.get("showName"));
                stringBuffer.append(",");
            }
            String header2 = stringBuffer.toString();
            header2 = header2.substring(0,header2.lastIndexOf(","));

            csvheaderList.add(header2);

            String fileName = "司机月排班"+ DateUtil.dateFormat(new Date(),"yyyy-MM")+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }

            CsvUtils.exportCsv(response,csvDataList,csvheaderList,fileName);
            long end = System.currentTimeMillis();
            logger.error("司机月排班成功,参数param："+(param==null?"null":JSON.toJSONString(param))+",耗时："+(end-start)+"毫秒");
        } catch (Exception e) {
            logger.error("司机月排班异常,参数param："+(param==null?"null":JSON.toJSONString(param)),e);
        }
    }
    private  void dataTrans(List<CarDriverMonthDTO> result, List<String> csvDataList,List<JSONObject>  headerList) {
        if (null == result) {
            return;
        }
        JSONObject jsonObject = null;
        for(CarDriverMonthDTO s:result){
           StringBuffer rowBuffer = new StringBuffer();
            rowBuffer.append(s.getDriverId()!=null?""+s.getDriverId()+"":"");
            rowBuffer.append(",");

            rowBuffer.append(s.getTeamName()!=null?""+s.getTeamName()+"":"");
            rowBuffer.append(",");


            rowBuffer.append(s.getDriverName()!=null?""+s.getDriverName()+"":"");
            rowBuffer.append(",");

            rowBuffer.append(s.getStatus()==1?"在职":"离职");
            rowBuffer.append(",");


            rowBuffer.append(StringUtils.isEmpty(s.getLicensePlates())?"":s.getLicensePlates());
            rowBuffer.append(",");

            String data = s.getData();
            if(StringUtils.isNotEmpty(data)){
                data = data.replace("{", "");
                data = data.replace("}", "");
               String[] dataArray = data.split(",");
                jsonObject = new JSONObject();
                for(String item :dataArray){
                    String[] itemInfoArray =  item.split(":");
                    if(itemInfoArray.length == 2){
                        jsonObject.put(""+itemInfoArray[0],itemInfoArray[1]);
                    }
                }

            }else {
                jsonObject = new JSONObject();
            }
            logger.info("jsonObject="+jsonObject);

            for(int j = 5; j < headerList.size(); j++) {
                JSONObject header = headerList.get(j);

                String statusKey = header.getString("proName");
                String statusValue = null;

                String status = jsonObject.getString(statusKey);
                if (null != status && !"".equals(status.trim())&&!"null".equals(status.trim())) {
                    try {
                        statusValue = EnumDriverMonthDutyStatus.getStatus(Integer.parseInt(status));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (null == statusValue) {
                    statusValue = "--";
                }
                rowBuffer.append(PageUtils.replaceComma(statusValue));

                if(j <= (headerList.size() -1) ){
                    rowBuffer.append(",");
                }
            }
            csvDataList.add(rowBuffer.toString());
        }
    }


    /** 
    * @Desc: 查询符合条件月排班列表 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/queryDriverMonthDutyData")
    public AjaxResponse queryDriverMonthDutyData(DriverMonthDutyRequest param) {
        logger.info("查询月排班列表数据入参:"+JSON.toJSONString(param));
        if(Check.NuNStr(param.getMonitorDate())){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            param.setMonitorDate(sdf.format(new Date()));
        }
        CommonRequest commonRequest = new CommonRequest();
        BeanUtils.copyProperties(param,commonRequest);
        if(!Check.NuNStr(param.getTeamId())){
            commonRequest.setTeamId(Integer.parseInt(param.getTeamId()));
        }
        CommonRequest data = commonService.paramDeal(commonRequest);
        if(Check.NuNObj(data)){
            logger.error("没有权限操作,用户："+JSON.toJSONString(WebSessionUtil.getCurrentLoginUser()));
            return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
        }
        param.setCityIds(commonService.setStringShiftInteger(data.getCityIds()));
        param.setSupplierIds(commonService.setStringShiftInteger(data.getSupplierIds()));
        param.setTeamIds(data.getTeamIds());
        PageInfo<CarDriverMonthDTO> pageInfo = driverMonthDutyService.queryDriverDutyList(param);

        PageDTO pageDTO = new PageDTO();
        pageDTO.setResult(pageInfo.getList());
        pageDTO.setTotal(new Integer(""+pageInfo.getTotal()));
        return AjaxResponse.success(pageDTO);
    }

    /** 
    * @Desc: 下载模板 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/5 
    */ 
    @SuppressWarnings({ "deprecation", "unchecked" })
    @RequestMapping(value = "/downloadTemplateMonthDuty")
    @ResponseBody
    public synchronized void downloadTemplateMonthDuty(DriverMonthDutyRequest param,HttpServletRequest request, HttpServletResponse response) {
        logger.info("downloadTemplateMonthDuty:下载 司机月排行 导入模板"+"--入参:"+ JSON.toJSONString(param));

        if("".equals(param.getMonitorDate())||param.getMonitorDate()==null
                ||"null".equals(param.getMonitorDate())){
            logger.error("downloadTemplateMonthDuty:下载 司机月排行 导入模板-失败[统计月不能为空]");
            return;
        }
        String path = request.getRealPath("/")+File.separator+"template"+File.separator+"driverMonthDutyInfo.xlsx";
        InputStream inputStream = null;
        try{
            // 获取表头
            Map<String, Object> result = new LinkedHashMap<String,Object>();
            Map<String, Object> tabelHeader = new LinkedHashMap<String,Object>();
            result = this.driverMonthDutyService.queryDriverDutyTable(param);
            tabelHeader = (Map<String, Object>)result.get("Rows");
            CommonRequest commonRequest = new CommonRequest();
            BeanUtils.copyProperties(param,commonRequest);
            if(!Check.NuNStr(param.getTeamId())){
                commonRequest.setTeamId(Integer.parseInt(param.getTeamId()));
            }
            CommonRequest data = commonService.paramDeal(commonRequest);
            if(Check.NuNObj(data)){
                logger.error("没有权限操作,用户："+JSON.toJSONString(WebSessionUtil.getCurrentLoginUser()));
                return;
            }
            param.setCityIds(commonService.setStringShiftInteger(data.getCityIds()));
            param.setSupplierIds(commonService.setStringShiftInteger(data.getSupplierIds()));
            param.setTeamIds(data.getTeamIds());

            List<CarDriverInfoDTO> driverInfoList = new ArrayList<CarDriverInfoDTO>();
            Map<String, Object> driverTeamMap = null;
            List<Map<String,Object>> driverIdList = null;
            // 根据选定的车队查询 在职的 司机信息
            driverIdList = driverMonthDutyService.queryDriverIdsByTeamIds(param.getTeamIds());
            if (null != driverIdList && !driverIdList.isEmpty()) {
                driverTeamMap = new LinkedHashMap<String, Object>();
                for (Map<String, Object> map : driverIdList) {
                    if (null != map.get("teamName") && !"".equals(map.get("teamName"))) {
                        driverTeamMap.put(map.get("driverId").toString(), map.get("teamName").toString());
                    } else {
                        driverTeamMap.put(map.get("driverId").toString(), "");
                    }
                }
            }
            driverInfoList = driverMonthDutyService.queryDriverListInfoForMonthDuty(param, driverTeamMap, param.getTeamIds());
            // 打开导入模板文件
            // 打开导入模板文件
            Workbook workbook = null;
            inputStream = new FileInputStream(path);
            workbook = create(inputStream);
            // 更新表头
            Sheet sheet = workbook.getSheetAt(0);
            int coloumNum=sheet.getRow(1).getPhysicalNumberOfCells();//获得总列数
            int rowNum=sheet.getLastRowNum();//获得总行数
            if (null != tabelHeader && !tabelHeader.isEmpty()) {
                Iterator<String> keyIterator =  tabelHeader.keySet().iterator();
                int cellIndex = 0;
                Row row = sheet.getRow(1);
                while(keyIterator.hasNext()){
                    String key = keyIterator.next();
                    String title = tabelHeader.get(key).toString();
                    Cell cell = row.getCell(cellIndex++);
                    cell.setCellValue(title);
                }
                for(; cellIndex <= coloumNum - 1; cellIndex++) {
                    Cell cell = row.getCell(cellIndex);
                    cell.setCellValue("——");
                }
            }
            // 清空原有数据
            for(int i = rowNum; i > 1; i--) {
                Row row = sheet.getRow(i);
                sheet.removeRow(row);
            }
            // 初始化用户数据
            if (null != driverInfoList && !driverInfoList.isEmpty()) {
                int rowIndex = 2;
                for (CarDriverInfoDTO driverInfo : driverInfoList) {
                    Row row = sheet.getRow(rowIndex);
                    if (null == row) {
                        row = sheet.createRow(rowIndex);
                    }
                    rowIndex++;
                    Cell cell0 = row.createCell(0);
                    cell0.setCellValue(driverInfo.getDriverId());
                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(driverInfo.getTeamName());
                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(driverInfo.getName());
                    Cell cell3 = row.createCell(3);
                    if (null != driverInfo.getStatus() && 1 == driverInfo.getStatus().intValue()) {
                        cell3.setCellValue("在职");
                    } else {
                        cell3.setCellValue("离职");
                    }
                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(driverInfo.getLicensePlates());
                }

            }
            /*HttpServletResponse excelResponse = this.setResponse(response, param.getMonitorDate()+"司机月排班");
            ServletOutputStream out = excelResponse.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();*/
            // 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
            //response.addHeader("Content-Disposition", "attachment;filename=" + new String((driverMonthDutyEntity.getMonitorDate() + "司机月排班").getBytes("gbk"),"iso8859-1"));
            //response.addHeader("Content-Length", "" + file.length());
            //response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            this.exportExcelFromTemplet(request, response, workbook, new String((param.getMonitorDate() + "司机月排班").getBytes("utf-8"), "iso8859-1"));
        }catch (FileNotFoundException e) {
            logger.error("fileDownloadDriverMonthDutyInfo:下载 司机月排行 导入模板-失败", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("fileDownloadDriverMonthDutyInfo:下载 司机月排行 导入模板-失败", e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("fileDownloadDriverMonthDutyInfo:下载 司机月排行 导入模板-失败", e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("fileDownloadDriverMonthDutyInfo:下载 司机月排行 导入模板-失败", e);
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置文件下载 response格式
     */
    private HttpServletResponse setResponse(HttpServletResponse response, String filename) throws IOException {
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("GB2312"), "ISO8859-1") + ".xls");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        return response;
    }

    public void exportExcelFromTemplet(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) throws IOException {
        if(StringUtils.isEmpty(fileName)) {
            fileName = "exportExcel";
        }
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");//指定下载的文件名
        response.setContentType("application/octet-stream");
        ServletOutputStream os =  response.getOutputStream();
        wb.write(os);
        os.flush();
        os.close();
    }

    private Workbook create(InputStream inp) throws IOException,InvalidFormatException {
        if (!inp.markSupported()) {
            inp = new PushbackInputStream(inp, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(inp)) {
            return new HSSFWorkbook(inp);
        }
        if (POIXMLDocument.hasOOXMLHeader(inp)) {
            return new XSSFWorkbook(OPCPackage.open(inp));
        }
        throw new IllegalArgumentException("你的excel版本目前poi解析不了");
    }

    /** 
    * @Desc: 返回司机月排班表头 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/queryDriverDutyTable")
    public AjaxResponse queryDriverDutyTable(DriverMonthDutyRequest driverMonthDutyRequest){
        Map<String,Object> result = new LinkedHashMap<String,Object>();
        logger.info("queryDriverDutyTable begin: 司机月排班表表头");
        result = this.driverMonthDutyService.queryDriverDutyTable(driverMonthDutyRequest);
        logger.info("queryDriverDutyTable end: 司机月排班表表头:" + result);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        result.put("nowDay", sdf.format(new Date()));
        return AjaxResponse.success(result);
    }

    /** 
    * @Desc: 获取 排班状态 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/queryDriverMonthDutyStatus", method = { RequestMethod.POST })
    public AjaxResponse queryDriverMonthDutyStatus(HttpServletRequest request){
        return AjaxResponse.success(EnumDriverMonthDutyStatus.getStatusMap());
    }

}