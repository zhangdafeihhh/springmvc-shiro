package com.zhuanche.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhuanche.common.CommonConfig;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.ImportCheckEntity;
import com.zhuanche.entity.driver.SupplierLevel;
import com.zhuanche.entity.driver.SupplierLevelAdditional;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.supplier.SupplierLevelService;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.excel.CsvUtils2File;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/supplierlevel")
public class SupplierLevelController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierLevelService.class);


    @Autowired
    private SupplierLevelService supplierLevelService;

    @Autowired
    private CarBizSupplierExMapper supplierExMapper;


    @RequestMapping(value="dopager",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse findPage(
            @RequestParam(value = "pageno", required = false,defaultValue = "0")int pageno,
            @RequestParam(value = "pagesize", required = false,defaultValue = "20")int pagesize,
            SupplierLevel entity,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("????????????????????????????????????pageno="+pageno+",pagesize="+pagesize+",entity="+ JSON.toJSONString(entity));
            PageInfo<SupplierLevel>  pageInfo =   supplierLevelService.findPage(pageno,pagesize,entity);
            return AjaxResponse.success(pageInfo);
        }catch (Exception e){
            logger.error("??????????????????????????????????????????pageno="+pageno+",pagesize="+pagesize+",entity="+ JSON.toJSONString(entity),e);
            return AjaxResponse.fail(-1,"????????????");
        }
    }

    @RequestMapping(value="findAddditionalScore",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse findAddditionalScore(
            @RequestParam(value = "supplierLevelId", required = true)int supplierLevelId,

            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("???????????????????????????id???????????????????????????????????????????????????supplierLevelId="+supplierLevelId);


            List<SupplierLevelAdditional>  list =    supplierLevelService.findSupplierLevelAdditionalBySupplierLevelId(supplierLevelId);
            return AjaxResponse.success(list);
        }catch (Exception e){
            logger.error("???????????????????????????id???????????????????????????????????????????????????supplierLevelId="+supplierLevelId,e);
            return AjaxResponse.fail(-1,"????????????");
        }
    }
    @RequestMapping(value="findAddditionalScoreBySupplierId",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse findAddditionalScoreBySupplierId(
            @RequestParam(value = "supplierId", required = true)int supplierId,

            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("???????????????Id????????????????????????????????????supplierId="+supplierId);
            SupplierLevel entity =    supplierLevelService.findSupplierLevelScoreBySupplierId(supplierId);
            String supplierName = supplierExMapper.getSupplierNameById(supplierId);
            if(entity == null){
                entity = new SupplierLevel();
                entity.setSupplierName(supplierName);
            }else {
                entity.setSupplierName(supplierName);
            }
            return AjaxResponse.success(entity);
        }catch (Exception e){
            logger.error("???????????????Id??????????????????????????????????????????supplierId="+supplierId,e);
            return AjaxResponse.fail(-1,"????????????");
        }
    }

        @RequestMapping(value="doSaveSupplierLevelAdditionScore",method= {RequestMethod.GET,RequestMethod.POST})
        @ResponseBody
        public AjaxResponse doSaveSupplierLevelAdditionScore(
                @RequestParam(value = "supplierLevelId", required = true)Integer supplierLevelId,
                @RequestParam(value = "delIds", required = false)String delIds,
                @RequestParam(value = "saveJson", required = false)String saveJson,
                HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
            try{
                logger.info("????????????????????????????????????delIds="+delIds+";saveJson="+saveJson );
                if(StringUtils.isEmpty(delIds) &&StringUtils.isEmpty(saveJson) ){
                    return AjaxResponse.failMsg(-2,"???????????????delIds???saveJson??????????????????");
                }
                else{
                    SupplierLevel result =   supplierLevelService.doSaveSupplierLevelAdditionScore(supplierLevelId,delIds,saveJson);
                    return AjaxResponse.success(result);
                }
            }catch (Exception e){
                logger.error("??????????????????????????????????????????delIds="+delIds+";saveJson="+saveJson );
                return AjaxResponse.failMsg(-1,"???????????????????????????");
            }
        }
    /**
     * ?????????????????????
     * @param request
     * @param response
     * @param modelMap
     * @return
     */
//    @RequestMapping(value="doPublishSupplierLevel",method= {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public AjaxResponse doPublishSupplierLevel(
//            @RequestParam(value = "ids", required = true)String ids,
//            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
//        try{
//            logger.info("????????????????????????????????????ids="+ids );
//            if(StringUtils.isEmpty(ids)){
//
//                return AjaxResponse.fail(-2,"??????ids??????");
//            }else{
//                String[]idArray = ids.split(",");
//                List<Integer> integerIdList = new ArrayList<>();
//                for(int i=0;i<idArray.length;i++){
//                    integerIdList.add(Integer.parseInt(idArray[i]));
//                }
//                supplierLevelService.doPublishSupplierLevel(integerIdList);
//                return AjaxResponse.success(Boolean.TRUE);
//            }
//
//        }catch (Exception e){
//            logger.error("??????????????????????????????????????????ids="+ids ,e);
//            return AjaxResponse.fail(-1,"???????????????????????????");
//        }
//    }

//    @RequestMapping(value="doUnPublishSupplierLevel",method= {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public AjaxResponse doUnPublishSupplierLevel(
//            @RequestParam(value = "ids", required = true)String ids,
//            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
//        try{
//            logger.info("??????????????????????????????????????????ids="+ids );
//            if(StringUtils.isEmpty(ids)){
//
//                return AjaxResponse.fail(-2,"??????ids??????");
//            }else{
//                String[]idArray = ids.split(",");
//                List<Integer> integerIdList = new ArrayList<>();
//                for(int i=0;i<idArray.length;i++){
//                    integerIdList.add(Integer.parseInt(idArray[i]));
//                }
//                supplierLevelService.doUnPublishSupplierLevel(integerIdList);
//                return AjaxResponse.success(Boolean.TRUE);
//            }
//
//        }catch (Exception e){
//            logger.error("????????????????????????????????????????????????ids="+ids ,e);
//            return AjaxResponse.fail(-1,"?????????????????????????????????");
//        }
//    }

//    @RequestMapping(value="doDeleteBySupplierLevelAdditionalId",method= {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public AjaxResponse doDeleteBySupplierLevelAdditionalId(
//            @RequestParam(value = "id", required = true)Integer id,
//            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
//        try{
//            logger.info("????????????????????????id????????????????????????????????????id="+id );
//            supplierLevelService.doDeleteBySupplierLevelAdditionalId(id);
//            return AjaxResponse.success(Boolean.TRUE);
//
//        }catch (Exception e){
//            logger.error("????????????????????????id????????????????????????????????????id="+id ,e);
//            return AjaxResponse.fail(-1,"????????????????????????id???????????????????????????");
//        }
//    }

    @RequestMapping(value="findSupplierLevelAdditionalBySupplierLevelId",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse findSupplierLevelAdditionalBySupplierLevelId(
            @RequestParam(value = "supplierLevelId", required = true)Integer supplierLevelId,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("?????????????????????id?????????????????????????????????????????????supplierLevelId="+supplierLevelId );
            List<SupplierLevelAdditional> list =    supplierLevelService.findSupplierLevelAdditionalBySupplierLevelId(supplierLevelId);
            return AjaxResponse.success(list);

        }catch (Exception e){
            logger.error("?????????????????????id???????????????????????????????????????????????????supplierLevelId="+supplierLevelId ,e);
            return AjaxResponse.fail(-1,"?????????????????????id????????????????????????????????????");
        }
    }

    @RequestMapping(value="export",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse export(
            SupplierLevel entity,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("????????????????????????????????????entity="+JSON.toJSONString(entity) );


            int pageno = 1;
            int pagesize = 2000;
            PageInfo<SupplierLevel>  pageInfo =   supplierLevelService.findPage(pageno,pagesize,entity);

            String fileName = "???????????????"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
            //???????????????????????????????????????
            String agent = request.getHeader("User-Agent").toUpperCase();
            //IE????????????Edge?????????
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //???????????????
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }


            CsvUtils utilEntity = new CsvUtils();
            String header = "???????????????,??????,??????,????????????,?????????,?????????,?????????,?????????,?????????,??????,??????";
            List<String> headerList = new ArrayList<>();
            headerList.add(header);
            if( pageInfo.getTotal() == 0){
                List<String> stringList = new ArrayList<>();
                stringList.add("?????????????????????????????????");

                utilEntity.exportCsvV2(response,stringList,headerList,fileName,true,true);
            }else{
                int pages = pageInfo.getPages();
                List<SupplierLevel> dataList = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                boolean isFirst = false;
                boolean isend = false;
                for(pageno=1;pageno<=pages;pageno++){
                    pageInfo =   supplierLevelService.findPage(pageno,pagesize,entity);
                    dataList = pageInfo.getList();
                    List<String> stringList = new ArrayList<>();
                    for(SupplierLevel item : dataList){
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(StringUtils.isEmpty(item.getSupplierName())?"":item.getSupplierName());
                        stringBuffer.append(",");

                        stringBuffer.append(StringUtils.isEmpty(item.getCityName())?"":item.getCityName());
                        stringBuffer.append(",");

                        stringBuffer.append(StringUtils.isEmpty(item.getMonth())?"":item.getMonth());
                        stringBuffer.append(",");

                        stringBuffer.append(sdf.format(item.getStartTime())+"-"+sdf.format(item.getEndTime()));

                        stringBuffer.append(",");
                        if(item.getScaleScore() != null){
                            stringBuffer.append(item.getScaleScore());
                        }
                        stringBuffer.append(",");

                        if(item.getEfficiencyScore() != null){
                            stringBuffer.append(item.getEfficiencyScore());
                        }
                        stringBuffer.append(",");

                        if(item.getServiceScore() != null){
                            stringBuffer.append(item.getServiceScore());
                        }
                        stringBuffer.append(",");

                        if(item.getAdditionalScore() != null){
                            stringBuffer.append(item.getAdditionalScore());
                        }
                        stringBuffer.append(",");

                        if(item.getGradeScore() != null){
                            stringBuffer.append(item.getGradeScore());
                        }
                        stringBuffer.append(",");
                        if(item.getGradeLevel() != null){
                            stringBuffer.append(item.getGradeLevel());
                        }
                        stringBuffer.append(",");

                        if(item.getStates() == 1){
                            stringBuffer.append("?????????");
                        }else {
                            stringBuffer.append("?????????");
                        }

                        stringList.add(stringBuffer.toString());
                    }
                    if(pageno == 1){
                        isFirst = true;
                    }else {
                        isFirst = false;
                    }
                    if(pageno == pages){
                        isend = true;
                    }
                    utilEntity.exportCsvV2(response,stringList,headerList,fileName,isFirst,isend);

                }

            }

            return AjaxResponse.success(Boolean.TRUE);

        }catch (Exception e){
            logger.error("??????????????????????????????????????????entity="+JSON.toJSONString(entity) ,e);
            return AjaxResponse.fail(-1,"???????????????????????????");
        }
    }


    @RequestMapping(value="exportadditionalScore",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse exportadditionalScore(
            SupplierLevel entity,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("???????????????????????????????????????entity="+JSON.toJSONString(entity) );


            String fileName = "????????????????????????"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
            //???????????????????????????????????????
            String agent = request.getHeader("User-Agent").toUpperCase();
            //IE????????????Edge?????????
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //???????????????
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }

            int pageno = 1;
            int pagesize = 2000;
            PageInfo<SupplierLevel>  pageInfo =   supplierLevelService.findPage(pageno,pagesize,entity);

            CsvUtils utilEntity = new CsvUtils();
            String header = "???????????????,????????????,???????????????,?????????";
            List<String> headerList = new ArrayList<>();
            headerList.add(header);
            if( pageInfo.getTotal() == 0){
                List<String> stringList = new ArrayList<>();
                stringList.add("?????????????????????????????????");

                utilEntity.exportCsvV2(response,stringList,headerList,fileName,true,true);
            }else{
                int pages = pageInfo.getPages();
                List<SupplierLevel> dataList = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                boolean isFirst = false;
                boolean isend = false;
                List<SupplierLevelAdditional> additionList = null;
                for(pageno=1;pageno<=pages;pageno++){
                    pageInfo =   supplierLevelService.findPage(pageno,pagesize,entity);
                    dataList = pageInfo.getList();
                    List<String> stringList = new ArrayList<>();
                    for(SupplierLevel item : dataList){
                        additionList = supplierLevelService.findSupplierLevelAdditionalBySupplierLevelId(item.getId());

                        if(additionList != null && additionList.size() >= 1){
                            for(SupplierLevelAdditional itemAddition:additionList){
                                StringBuffer stringBuffer = new StringBuffer();
                                stringBuffer.append(StringUtils.isEmpty(item.getSupplierName())?"":item.getSupplierName());
                                stringBuffer.append(",");

                                stringBuffer.append(StringUtils.isEmpty(item.getMonth())?"":item.getMonth());
                                stringBuffer.append(",");

                                stringBuffer.append(itemAddition.getItemName());
                                stringBuffer.append(",");

                                stringBuffer.append(itemAddition.getItemValue());

                                stringList.add(stringBuffer.toString());
                            }
                        }else{
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append(StringUtils.isEmpty(item.getSupplierName())?"":item.getSupplierName());
                            stringBuffer.append(",");

                            stringBuffer.append(StringUtils.isEmpty(item.getMonth())?"":item.getMonth());
                            stringBuffer.append(",");
                            stringBuffer.append(",");
                            stringList.add(stringBuffer.toString());
                        }



                    }
                    if(pageno == 1){
                        isFirst = true;
                    }else {
                        isFirst = false;
                    }
                    if(pageno == pages){
                        isend = true;
                    }
                    utilEntity.exportCsvV2(response,stringList,headerList,fileName,isFirst,isend);

                }

            }

            return AjaxResponse.success(Boolean.TRUE);

        }catch (Exception e){
            logger.error("?????????????????????????????????????????????entity="+JSON.toJSONString(entity) ,e);
            return AjaxResponse.fail(-1,"??????????????????????????????");
        }
    }

    /**
     * ????????????????????????
     * @param file
     * @param month
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/importsupplierleveladditional")
    public AjaxResponse importsupplierleveladditional(
                //@RequestParam(value = "filename", required = true)String filename,
                MultipartFile file,
                                        @RequestParam(value = "month", required = true)String month,
                                        HttpServletRequest request){

        logger.info("???????????????????????????:month=" + month+",fileSize="+(file==null?"null":file.getSize()));
        try{
            //????????????????????????
            Map<String, Object> result = _doPareImportData(    file, month,    request);
            if(Integer.parseInt(result.get("result").toString()) == -1){
                AjaxResponse errorAjaxResponse = AjaxResponse.failMsg(-2,"???????????????");
                errorAjaxResponse.setMsg("???????????????");
                return errorAjaxResponse;
            }else if(result.get("error")==Boolean.TRUE){
                //??????????????????????????????
                AjaxResponse errorAjaxResponse = AjaxResponse.success(result);
                errorAjaxResponse.setMsg("????????????????????????,??????????????????");
                return errorAjaxResponse;
            }else{
                //????????????????????????
                List<SupplierLevelAdditional> supplierLevelAdditionalDtos = (List<SupplierLevelAdditional>) result.get("dataList");
                supplierLevelService.doImportSupplierLevelAdditional(supplierLevelAdditionalDtos);

                return AjaxResponse.success(Boolean.TRUE);
            }
        }catch (Exception e){
            logger.error("?????????????????????????????????:month=" + month+",fileSize="+(file==null?"null":file.getSize()));
            AjaxResponse errorAjaxResponse = AjaxResponse.failMsg(-1,"????????????????????????,??????????????????");
            return errorAjaxResponse;
        }


    }

    @RequestMapping("/downloadtemplate")
    public void template( HttpServletRequest request,HttpServletResponse response){
        logger.info("????????????????????????????????????");
        try {
            String path = request.getRealPath("/") + File.separator + "template"   + File.separator + "supplierlevelAdditional.xlsx";
            this.fileDownload(request,response,path);
        } catch (Exception e) {
            logger.error("??????????????????????????????????????????", e);
        }
    }


    /**???????????????????????????????????????????????????
     * ??????????????????
     * @param request
     * @param response
     * @param month
     */
    @RequestMapping("/doGenerateByDate")
    @ResponseBody
    public AjaxResponse doGenerateByDate( HttpServletRequest request,HttpServletResponse response,String month){
        logger.info("???????????????????????????month="+month);
        try {
            supplierLevelService.doGenerateByDate(month);

            return AjaxResponse.success("??????");
        } catch (Exception e) {
            logger.error("??????????????????????????????,month="+month, e);
            return AjaxResponse.failMsg(-1,"??????");
        }
    }
    /**
     * ??????????????????
     * @param request
     * @param response
     * @param month
     */
    @RequestMapping("/doSeqence")
    @ResponseBody
    public AjaxResponse doSeqence( HttpServletRequest request,HttpServletResponse response,String month){
        logger.info("??????????????????month="+month);
        try {
            supplierLevelService.doSeqence(month);

            return AjaxResponse.success("??????");
        } catch (Exception e) {
            logger.error("?????????????????????,month="+month, e);
            return AjaxResponse.failMsg(-1,"??????");
        }
    }

    private Map<String, Object> _doPareImportData(MultipartFile file,String month,   HttpServletRequest request){

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("result", 1);
        String download = null;
        List<JSONObject> listException = new ArrayList<>();

        List<SupplierLevelAdditional> supplierLevelAdditionalDtoList = new ArrayList<>();
        try {
            InputStream is = file.getInputStream();
            Workbook workbook = null;
            String fileType = file.getOriginalFilename().split("\\.")[1];
            if (fileType.equals("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(is);
            }
            Sheet sheet = workbook.getSheetAt(0);

            // ????????????????????????
            Row row1 = sheet.getRow(0);
            if(row1==null){
                logger.info("??????????????????L1");
                result.put("result", -1);
                result.put("msg", "??????????????????");

                return result;
            }

            Cell cell0 =null;
            Cell cell1 =null;
            Cell cell2 =null;

            cell0 = row1.getCell(0);
            cell1 = row1.getCell(1);
            cell2 = row1.getCell(2);
            String cell0V = null;
            String cell1V = null;
            String cell2V = null;
            try {
                  cell0V = cell0.getStringCellValue();
                  cell1V = cell1.getStringCellValue();
                 cell2V = cell2.getStringCellValue();
                if (!"???????????????".equals(cell0V) || !"????????????".equals(cell1V) || !"?????????".equals(cell2V)) {
                    logger.info("??????????????????L2??????????????????????????????????????????" + cell0V + ",?????????????????????" + cell1V + ",??????????????????" + cell2V);
                    result.put("result", -1);
                    result.put("msg", "??????????????????");
                    return result;
                }
            }catch (Exception e){
                logger.info("??????????????????L3??????????????????????????????????????????" + cell0V + ",?????????????????????" + cell1V + ",??????????????????" + cell2V);
                result.put("result", -1);
                result.put("msg", "??????????????????");
                return result;
            }


            int minRowIx = 1;// ????????????????????????????????????????????????
            int maxRowIx = sheet.getLastRowNum(); // ???????????????????????????

            Boolean haveError = false;
            String supplierName = null;
            String itemName = null;
            Double itemValue = null;


            Map<String,Integer> rowItemCache = new HashMap<>();
            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                  cell0 =null;
                  cell1 =null;
                  cell2 =null;

                Row row = sheet.getRow(rowIx); // ???????????????
                cell0 = row.getCell(0);
                cell1 = row.getCell(1);
                cell2 = row.getCell(2);
                if(cell0 == null){
                    supplierName = null;
                }else{
                    supplierName = cell0.getStringCellValue();
                    if(supplierName != null){
                        supplierName = supplierName.trim();
                    }
                }
                if(StringUtils.isEmpty(supplierName)){
                    continue;
                }

                if(cell1 == null){
                    itemName = null;
                }else{
                    itemName = cell1.getStringCellValue();
                    if(itemName != null){
                        itemName = itemName.trim();
                    }
                }
                if(StringUtils.isEmpty(itemName)){
                    continue;
                }

                if(cell2 == null){
                    itemValue = null;
                }else{
                    itemValue = cell2.getNumericCellValue() ;
                }

                ImportCheckEntity checkResult = checkEntity(rowIx,supplierName,itemName,itemValue,month,rowItemCache);

                if(checkResult.getResult()){
                    SupplierLevelAdditional entity = new SupplierLevelAdditional();
                    entity.setItemName(itemName);
                    entity.setSupplierLevelId(checkResult.getForeignId());
                    entity.setItemValue(new BigDecimal(itemValue));
                    supplierLevelAdditionalDtoList.add(entity);

                }else {
                    //????????????
                    JSONObject error = new JSONObject();
                    error.put("supplierName",supplierName);
                    error.put("itemName",itemName);
                    error.put("itemValue",itemValue);
                    error.put("errorReason",checkResult.getReason());

                    listException.add(error);
                    haveError = true;
                }
            }

//            Workbook wb = null;
            try {
                // ?????????????????????
                if (listException.size() > 0) {
                    Date now = new Date();
                    //????????????
                    String fileName =  new String("ERROR".getBytes("utf-8"), "iso8859-1");

                    download = "/template/error/" + fileName + buildRandom(2) + "_"+ System.currentTimeMillis() + ".xlsx";

                    List<String> titles = new ArrayList<>();

                    //?????????????????????????????????????????????
                    File dir = new File(CommonConfig.ERROR_BASE_FILE+"/template/error/");
                    if(!dir.exists()){
                        dir.mkdirs();
                    }

                    writeExcel(CommonConfig.ERROR_BASE_FILE+download,"????????????",titles,listException);
                }
            } catch (Exception e) {
                logger.error("???????????????????????????????????????V1" ,e);
            }
        } catch (Exception e) {
            logger.error("???????????????????????????????????????V2" ,e);
        }

        if (!"".equals(download) && download != null) {
            result.put("download", download);
        } else {
            result.put("download", "");
        }
        result.put("errorSize", listException.size());
        result.put("errorList", listException);


        if(listException.size() == 0){
            result.put("error", false);
            result.put("dataList", supplierLevelAdditionalDtoList);
        }else {
            result.put("error", true);

        }
        return result;
    }

    public static Workbook create(InputStream in) throws
            IOException, InvalidFormatException {
        if (!in.markSupported()) {
            in = new PushbackInputStream(in, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(in)) {
            return new HSSFWorkbook(in);
        }
        if (POIXMLDocument.hasOOXMLHeader(in)) {
            return new XSSFWorkbook(OPCPackage.open(in));
        }
        throw new IllegalArgumentException("??????excel????????????poi????????????");
    }
    private ImportCheckEntity checkEntity(Integer rowNum,String supplierName,String itemName,Double itemValue,String month,Map<String,Integer> rowItemCache){
        ImportCheckEntity result = new ImportCheckEntity();
        result.setResult(true);

        if(rowItemCache.containsKey(supplierName+"_"+itemName)){
            result.setResult(false);
            result.setReason("???????????????????????????????????????");
        }else{
            rowItemCache.put(supplierName+"_"+itemName,rowNum);

            SupplierLevel supplierLevel =   supplierLevelService.findByMonthAndSupplierName(month,supplierName);
            if(supplierLevel == null){
                result.setResult(false);
                result.setReason("???????????????????????????");
            }else if(supplierLevel.getStates() != 1){
                result.setResult(false);
                result.setReason("???????????????????????????????????????");
            }else {
                Integer supplierLevelId = supplierLevel.getId();

                SupplierLevelAdditional additional =  supplierLevelService.findBySupplierLevelIdAndSupplierLevelAdditionalName(supplierLevelId,itemName);
                if(additional != null){
                    result.setResult(false);
                    result.setReason("?????????????????????");
                }else {
                    if(itemValue == null){
                        result.setResult(false);
                        result.setReason("?????????????????????????????????");
                    } else if(itemValue == 0){
                        result.setResult(false);
                        result.setReason("??????????????????????????????0");
                    }else {
                        result.setResult(true);
                        result.setForeignId(supplierLevel.getId());
                    }
                }
            }
        }


        return result;
    }


    /**
     * ????????????
     * @param filepath filepath ???????????????
     */
    private static String getSuffiex(String filepath) {
        if (StringUtils.isBlank(filepath)) {
            return "";
        }
        int index = filepath.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filepath.substring(index + 1, filepath.length());
    }

    /**
     * ??????Excel??????
     * @param filepath filepath ???????????????
     * @param sheetName ???Sheet????????????
     * @param titles ??????
     * @param values ??????????????????
     */
    public static boolean writeExcel(String filepath, String sheetName, List<String> titles,
                                     List<JSONObject> values) throws IOException {
        boolean success = false;
        OutputStream outputStream = null;
        if (StringUtils.isBlank(filepath)) {
            throw new IllegalArgumentException("????????????????????????");
        } else {
            String suffiex = getSuffiex(filepath);
            if (StringUtils.isBlank(suffiex)) {
                throw new IllegalArgumentException("????????????????????????");
            }
            Workbook workbook;
            if ("xls".equals(suffiex.toLowerCase())) {
                workbook = new HSSFWorkbook();
            } else {
                workbook = new XSSFWorkbook();
            }
            // ??????????????????
            Sheet sheet;
            if (StringUtils.isBlank(sheetName)) {
                // name ????????????????????????
                sheet = workbook.createSheet();
            } else {
                sheet = workbook.createSheet(sheetName);
            }
            // ??????????????????????????????15?????????
            sheet.setDefaultColumnWidth((short) 15);
            // ????????????

            // ???????????????
            Row row = null;

            // ????????????
            Iterator<JSONObject> iterator = values.iterator();
            // ??????
            int index = 0;

            String supplierName = null;
            String itemName = null;
            String itemValue = null;
            String errorReason = null;
            for(JSONObject s:values){
                row = sheet.createRow(index);
                Cell cell0 = row.createCell(0);
                Cell cell1 = row.createCell(1);
                Cell cell2 = row.createCell(2);
                Cell cell3 = row.createCell(3);

                supplierName = s.containsKey("supplierName")? s.getString("supplierName"):"";
                itemName = s.containsKey("itemName")? s.getString("itemName"):"";
                itemValue = s.containsKey("itemValue")? s.getString("itemValue"):"";
                errorReason = s.containsKey("errorReason")? s.getString("errorReason"):"";


                cell0.setCellValue(supplierName);
                cell1.setCellValue(itemName);
                cell2.setCellValue(itemValue);
                cell3.setCellValue(errorReason);

                index++;
            }


            try {
                outputStream = new FileOutputStream(filepath);
                workbook.write(outputStream);
                success = true;
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            }
            return success;
        }
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


    /**
     *
     *???????????????????????????
     */
    @RequestMapping("/exportException")
    public void exportException(String fileName, HttpServletRequest request,HttpServletResponse response){

        logger.info("???????????????????????????????????????????????????");
        String path = CommonConfig.ERROR_BASE_FILE + fileName;
        this.fileDownload(request,response,path);
    }

    /*
     * ??????
     */
    private void fileDownload(HttpServletRequest request, HttpServletResponse response,String path) {

        File file = new File(path);// path????????????????????????????????????????????????
        String filename = file.getName();// ????????????????????????
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            // ?????????????????????????????????,???????????????????????????utf-8,?????????????????????,????????????????????????????????????????????????????????????????????????
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            os.write(buffer);// ????????????
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            logger.error("fileDownload error", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("fileDownload error", e);
        } catch (IOException e) {
            logger.error("fileDownload error", e);
        }
    }
}
