package com.zhuanche.controller.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.dutyEnum.EnumDriverMonthDutyStatus;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driverDuty.CarDriverMonthDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMonthDuty;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DriverMonthDutyRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.driverScheduling.DriverMonthDutyService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import com.zhuanche.util.MobileOverlayUtil;
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
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.*;


/**
 * @description: ????????????
 *
 * <PRE>
 * <BR>	????????????
 * <BR>-----------------------------------------------
 * <BR>	????????????			?????????			????????????
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
    * @Desc: ?????????????????? 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/importDriverMonthDutyInfo")
	@RequiresPermissions(value = { "DriverWorkManage_import" } )
    @RequestFunction(menu = DRIVER_DUTY_MANAGE_IMPORT)
    public AjaxResponse importDriverMonthDutyInfo(DriverMonthDutyRequest params,HttpServletRequest request,MultipartFile file){
        //TODO
        logger.info("????????????????????????????????????"+JSON.toJSONString(params));
        if("".equals(params.getMonitorDate())||params.getMonitorDate()==null||"null".equals(params.getMonitorDate())){
            logger.error("importDriverMonthDutyInfo:?????? ??????????????? ??????-??????[?????????????????????]");
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        logger.info("????????????????????????????????????????????????,??????:"+params.toString());
//            Map<String, Object> result = this.driverMonthDutyService.importDriverMonthDuty(params, request);

        if (file == null) {
            return AjaxResponse.fail(RestErrorCode.FILE_ERROR);
        }
        /*CommonsMultipartFile commonsmultipartfile = (CommonsMultipartFile) file;
        DiskFileItem diskFileItem = (DiskFileItem) commonsmultipartfile.getFileItem();
        File newFile = diskFileItem.getStoreLocation();
        logger.info("??????????????????????????????:"+newFile.getName());*/
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
    * @Desc: ???????????????????????? 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/updateDriverMonthDutyData")
    @RequestFunction(menu = DRIVER_DUTY_MANAGE_UPDATE)
    public AjaxResponse updateDriverMonthDutyData(DriverMonthDutyRequest param){
        if(Check.NuNObj(param) || Check.NuNObj(param.getId()) || Check.NuNStr(param.getData())){
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        logger.info("??????????????????????????????:{}",JSON.toJSONString(param));
        CarDriverMonthDuty exists = this.driverMonthDutyService.selectByPrimaryKey(param.getId());
        if (Check.NuNObj(exists)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_NOT_FOUND);
        } else {
            // ????????????????????????????????????
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            String nowDay = sdf.format(new Date()); // ????????????
            String newData = param.getData();
            String oldData = exists.getData();
            int index = newData.indexOf(nowDay);
            boolean hasError = false;
            if (index >= 0) {
                int oldIndex = oldData.indexOf(nowDay);
                if (oldIndex >= 0) {
                    String data = oldData.substring(0, oldIndex) + newData.substring(index);
                    exists.setData(data);
                } else { // ????????????????????????????????????
                    hasError = true;
                }
            } else { // ????????????????????????????????????
                exists.setData(newData);
            }
            if (hasError) {
                return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
            }
            return AjaxResponse.success(this.driverMonthDutyService.updateDriverMonthDutyData(exists));
        }
    }

    /** 
    * @Desc: ???????????????????????? 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/exportDriverMonthDuty")
	@RequiresPermissions(value = { "DriverWorkManage_export" } )
    @RequestFunction(menu = DRIVER_DUTY_MANAGE_EXPORT)
    public String exportDriverMonthDuty(DriverMonthDutyRequest param, HttpServletRequest request,HttpServletResponse response){
        logger.info("?????????????????????????????????:"+JSON.toJSONString(param));
        long start = System.currentTimeMillis();
        try{
            if(Check.NuNStr(param.getMonitorDate())){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                param.setMonitorDate(sdf.format(new Date()));
            }
            CommonRequest commonRequest = new CommonRequest();
            BeanUtils.copyProperties(param,commonRequest);
            if(!Check.NuNStr(param.getTeamId())){
                commonRequest.setTeamId(Integer.parseInt(param.getTeamId()));
            }
            List<String> csvheaderList = new ArrayList<>();

            CommonRequest data = commonService.paramDeal(commonRequest);
            if(Check.NuNObj(data)){
                logger.error("????????????????????????????????????,?????????"+JSON.toJSONString(WebSessionUtil.getCurrentLoginUser()));
                return "??????????????????";
            }
            param.setCityIds(commonService.setStringShiftInteger(data.getCityIds()));
            param.setSupplierIds(commonService.setStringShiftInteger(data.getSupplierIds()));
            param.setTeamIds(data.getTeamIds());
            param.setPageSize(CsvUtils.downPerSize);

            PageInfo<CarDriverMonthDTO> pageInfo = driverMonthDutyService.queryDriverDutyList(param);
            List<JSONObject>  headerList=  driverMonthDutyService.generateTableHeader(param.getMonitorDate());

            StringBuffer stringBuffer = new StringBuffer();
            for(JSONObject item : headerList){
                stringBuffer.append(item.get("showName"));
                stringBuffer.append(",");
            }
            String header2 = stringBuffer.toString();
            header2 = header2.substring(0,header2.lastIndexOf(","));
            csvheaderList.add(header2);

            String fileName = "???????????????"+ DateUtil.dateFormat(new Date(),"yyyy-MM")+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //???????????????????????????????????????
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE????????????Edge?????????
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //???????????????
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }

            List<CarDriverMonthDTO> pageList = pageInfo.getList();
            CsvUtils utilEntity = new CsvUtils();

            Integer totalPage = pageInfo.getPages();
            boolean isLast = false;
            boolean isFirst = true;
            if(pageList == null || pageList.size() == 0){
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("?????????????????????????????????");
                utilEntity.exportCsvV2(response,csvDataList,csvheaderList,fileName,true,true);
                long end = System.currentTimeMillis();
                logger.info("?????????????????????,?????????????????????????????????,??????param???"+(param==null?"null":JSON.toJSONString(param))+",?????????"+(end-start)+"??????");
                return "????????????????????????";
            }else {
                List<String> csvDataList = new ArrayList<>();
                if(totalPage == 1){
                    isLast = true;
                }
                //????????????
                dataTrans( pageList,  csvDataList,headerList);
                utilEntity.exportCsvV2(response,csvDataList,csvheaderList,fileName,isFirst,isLast);
                if(isLast){
                    long end = System.currentTimeMillis();
                    logger.info("?????????????????????,??????param???"+(param==null?"null":JSON.toJSONString(param))+",?????????"+(end-start)+"??????");
                    return "????????????????????????";
                }
                //???isFirst???false;
                isFirst = false;
                for(int pageNumber = 2; pageNumber <= totalPage; pageNumber++){
                    param.setPageNo(pageNumber);
                    logger.info("?????????????????????,???"+pageNumber+"????????????param???"+JSON.toJSONString(param));
                    pageInfo = driverMonthDutyService.queryDriverDutyList(param);
                    csvDataList = new ArrayList<>();
                    dataTrans( pageInfo.getList(),  csvDataList,headerList);
                    if(pageNumber == totalPage){
                        isLast = true;
                    }
                    utilEntity.exportCsvV2(response,csvDataList,csvheaderList,fileName,isFirst,isLast);
                }
                long end = System.currentTimeMillis();
                logger.info("?????????????????????,??????param???"+(param==null?"null":JSON.toJSONString(param))+",?????????"+(end-start)+"??????");
                return "????????????????????????";
            }

        }catch (Exception e){
            logger.error("?????????????????????????????????:"+JSON.toJSONString(param),e);
            e.printStackTrace();
        }

        return "";
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

            rowBuffer.append(s.getStatus()==1?"??????":"??????");
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
    * @Desc: ????????????????????????????????? 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/queryDriverMonthDutyData")
	@RequiresPermissions(value = { "DriverWorkManage_look" } )
    @RequestFunction(menu = DRIVER_DUTY_MANAGE_LIST)
    public AjaxResponse queryDriverMonthDutyData(DriverMonthDutyRequest param) {
        logger.info("?????????????????????????????????:"+JSON.toJSONString(param));
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
            logger.error("??????????????????,?????????"+JSON.toJSONString(WebSessionUtil.getCurrentLoginUser()));
            return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
        }
        param.setCityIds(commonService.setStringShiftInteger(data.getCityIds()));
        param.setSupplierIds(commonService.setStringShiftInteger(data.getSupplierIds()));
        param.setTeamIds(data.getTeamIds());
        logger.info("??????????????????????????????????????????:"+JSON.toJSONString(param));
        PageInfo<CarDriverMonthDTO> pageInfo = driverMonthDutyService.queryDriverDutyList(param);

        PageDTO pageDTO = new PageDTO();
        pageDTO.setResult(pageInfo.getList());
        pageDTO.setTotal(new Integer(""+pageInfo.getTotal()));
        overLayPhone(pageInfo.getList());
        return AjaxResponse.success(pageDTO);
    }

    private void overLayPhone(List<CarDriverMonthDTO> list) {
        if (Objects.nonNull(list)){
            for (CarDriverMonthDTO carDriverMonthDTO : list) {
                carDriverMonthDTO.setDriverPhone(MobileOverlayUtil.doOverlayPhone(carDriverMonthDTO.getDriverPhone()));
            }
        }
    }

    /** 
    * @Desc: ???????????? 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/5 
    */ 
    @SuppressWarnings({ "deprecation", "unchecked" })
    @RequestMapping(value = "/downloadTemplateMonthDuty")
	@RequiresPermissions(value = { "DriverWorkManage_demo" } )
    @ResponseBody
    @RequestFunction(menu = DRIVER_DUTY_MANAGE_TEMPLATE_DOWNLOAD)
    public synchronized void downloadTemplateMonthDuty(DriverMonthDutyRequest param,HttpServletRequest request, HttpServletResponse response) {
        logger.info("downloadTemplateMonthDuty:?????? ??????????????? ????????????"+"--??????:"+ JSON.toJSONString(param));

        if("".equals(param.getMonitorDate())||param.getMonitorDate()==null
                ||"null".equals(param.getMonitorDate())){
            logger.error("downloadTemplateMonthDuty:?????? ??????????????? ????????????-??????[?????????????????????]");
            return;
        }
        String path = request.getRealPath("/")+File.separator+"template"+File.separator+"driverMonthDutyInfo.xlsx";
        InputStream inputStream = null;
        try{
            // ????????????
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
                logger.error("??????????????????,?????????"+JSON.toJSONString(WebSessionUtil.getCurrentLoginUser()));
                return;
            }
            param.setCityIds(commonService.setStringShiftInteger(data.getCityIds()));
            param.setSupplierIds(commonService.setStringShiftInteger(data.getSupplierIds()));
            param.setTeamIds(data.getTeamIds());

            List<CarDriverInfoDTO> driverInfoList = new ArrayList<CarDriverInfoDTO>();
            Map<String, Object> driverTeamMap = null;
            List<Map<String,Object>> driverIdList = null;
            // ??????????????????????????? ????????? ????????????
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
            // ????????????????????????
            // ????????????????????????
            Workbook workbook = null;
            inputStream = new FileInputStream(path);
            workbook = create(inputStream);
            // ????????????
            Sheet sheet = workbook.getSheetAt(0);
            int coloumNum=sheet.getRow(1).getPhysicalNumberOfCells();//???????????????
            int rowNum=sheet.getLastRowNum();//???????????????
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
                    cell.setCellValue("??????");
                }
            }
            // ??????????????????
            for(int i = rowNum; i > 1; i--) {
                Row row = sheet.getRow(i);
                sheet.removeRow(row);
            }
            // ?????????????????????
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
                        cell3.setCellValue("??????");
                    } else {
                        cell3.setCellValue("??????");
                    }
                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(driverInfo.getLicensePlates());
                }

            }
            /*HttpServletResponse excelResponse = this.setResponse(response, param.getMonitorDate()+"???????????????");
            ServletOutputStream out = excelResponse.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();*/
            // ?????????????????????????????????,???????????????????????????utf-8,?????????????????????,????????????????????????????????????????????????????????????????????????
            //response.addHeader("Content-Disposition", "attachment;filename=" + new String((driverMonthDutyEntity.getMonitorDate() + "???????????????").getBytes("gbk"),"iso8859-1"));
            //response.addHeader("Content-Length", "" + file.length());
            //response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            this.exportExcelFromTemplet(request, response, workbook, new String((param.getMonitorDate() + "???????????????").getBytes("utf-8"), "iso8859-1"));
        }catch (FileNotFoundException e) {
            logger.error("fileDownloadDriverMonthDutyInfo:?????? ??????????????? ????????????-??????", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("fileDownloadDriverMonthDutyInfo:?????? ??????????????? ????????????-??????", e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("fileDownloadDriverMonthDutyInfo:?????? ??????????????? ????????????-??????", e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("fileDownloadDriverMonthDutyInfo:?????? ??????????????? ????????????-??????", e);
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


    public void exportExcelFromTemplet(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) throws IOException {
        if(StringUtils.isEmpty(fileName)) {
            fileName = "exportExcel";
        }
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");//????????????????????????
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
        throw new IllegalArgumentException("??????excel????????????poi????????????");
    }

    /** 
    * @Desc: ??????????????????????????? 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/queryDriverDutyTable")
    public AjaxResponse queryDriverDutyTable(DriverMonthDutyRequest driverMonthDutyRequest){
        Map<String,Object> result = new LinkedHashMap<String,Object>();
        logger.info("queryDriverDutyTable begin: ????????????????????????");
        result = this.driverMonthDutyService.queryDriverDutyTable(driverMonthDutyRequest);
        logger.info("queryDriverDutyTable end: ????????????????????????:" + result);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        result.put("nowDay", sdf.format(new Date()));
        return AjaxResponse.success(result);
    }

    /** 
    * @Desc: ?????? ???????????? 
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