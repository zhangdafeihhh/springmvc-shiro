package com.zhuanche.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.CommonConfig;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.ImportCheckEntity;
import com.zhuanche.entity.driver.SupplierLevel;
import com.zhuanche.entity.driver.SupplierLevelAdditional;
import com.zhuanche.serv.supplier.SupplierLevelService;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.excel.CsvUtils2File;
import net.sf.json.JSONObject;
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


    @RequestMapping(value="dopager",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse findPage(
            @RequestParam(value = "pageno", required = false,defaultValue = "0")int pageno,
            @RequestParam(value = "pagesize", required = false,defaultValue = "20")int pagesize,
            SupplierLevel entity,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("查询供应商等级，参数为：pageno="+pageno+",pagesize="+pagesize+",entity="+ JSON.toJSONString(entity));
            PageInfo<SupplierLevel>  pageInfo =   supplierLevelService.findPage(pageno,pagesize,entity);
            return AjaxResponse.success(pageInfo);
        }catch (Exception e){
            logger.error("查询供应商等级失败，参数为：pageno="+pageno+",pagesize="+pagesize+",entity="+ JSON.toJSONString(entity),e);
            return AjaxResponse.fail(-1,"查询失败");
        }
    }

    @RequestMapping(value="findAddditionalScore",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse findAddditionalScore(
            @RequestParam(value = "supplierLevelId", required = true)int supplierLevelId,

            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("根据供应商积分等级id查询供应商附加分信息列表，参数为：supplierLevelId="+supplierLevelId);


            List<SupplierLevelAdditional>  list =    supplierLevelService.findSupplierLevelAdditionalBySupplierLevelId(supplierLevelId);
            return AjaxResponse.success(list);
        }catch (Exception e){
            logger.error("根据供应商积分等级id查询供应商附加分信息列表，参数为：supplierLevelId="+supplierLevelId,e);
            return AjaxResponse.fail(-1,"查询失败");
        }
    }
    @RequestMapping(value="findAddditionalScoreBySupplierId",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse findAddditionalScoreBySupplierId(
            @RequestParam(value = "supplierId", required = true)int supplierId,

            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("根据供应商Id查询供应商等级，参数为：supplierId="+supplierId);
            SupplierLevel entity =    supplierLevelService.findSupplierLevelScoreBySupplierId(supplierId);
            return AjaxResponse.success(entity);
        }catch (Exception e){
            logger.error("根据供应商Id查询供应商等级异常，参数为：supplierId="+supplierId,e);
            return AjaxResponse.fail(-1,"查询失败");
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
                logger.info("保存修改附加分，参数为：delIds="+delIds+";saveJson="+saveJson );
                if(StringUtils.isEmpty(delIds) &&StringUtils.isEmpty(saveJson) ){
                    return AjaxResponse.failMsg(-2,"参数错误，delIds和saveJson不能同时为空");
                }
                else{
                    SupplierLevel result =   supplierLevelService.doSaveSupplierLevelAdditionScore(supplierLevelId,delIds,saveJson);
                    return AjaxResponse.success(result);
                }
            }catch (Exception e){
                logger.error("保存修改附加分异常，参数为：delIds="+delIds+";saveJson="+saveJson );
                return AjaxResponse.failMsg(-1,"保存修改附加分异常");
            }
        }
    /**
     * 发布供应商等级
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
//            logger.info("发布供应商等级，参数为：ids="+ids );
//            if(StringUtils.isEmpty(ids)){
//
//                return AjaxResponse.fail(-2,"参数ids为空");
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
//            logger.error("发布供应商等级异常，参数为：ids="+ids ,e);
//            return AjaxResponse.fail(-1,"发布供应商等级异常");
//        }
//    }

//    @RequestMapping(value="doUnPublishSupplierLevel",method= {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public AjaxResponse doUnPublishSupplierLevel(
//            @RequestParam(value = "ids", required = true)String ids,
//            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
//        try{
//            logger.info("取消发布供应商等级，参数为：ids="+ids );
//            if(StringUtils.isEmpty(ids)){
//
//                return AjaxResponse.fail(-2,"参数ids为空");
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
//            logger.error("取消发布供应商等级异常，参数为：ids="+ids ,e);
//            return AjaxResponse.fail(-1,"取消发布供应商等级异常");
//        }
//    }

//    @RequestMapping(value="doDeleteBySupplierLevelAdditionalId",method= {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public AjaxResponse doDeleteBySupplierLevelAdditionalId(
//            @RequestParam(value = "id", required = true)Integer id,
//            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
//        try{
//            logger.info("根据供应商附加分id进行删除附加分，参数为：id="+id );
//            supplierLevelService.doDeleteBySupplierLevelAdditionalId(id);
//            return AjaxResponse.success(Boolean.TRUE);
//
//        }catch (Exception e){
//            logger.error("根据供应商附加分id进行删除附加分，参数为：id="+id ,e);
//            return AjaxResponse.fail(-1,"根据供应商附加分id进行删除附加分异常");
//        }
//    }

    @RequestMapping(value="findSupplierLevelAdditionalBySupplierLevelId",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse findSupplierLevelAdditionalBySupplierLevelId(
            @RequestParam(value = "supplierLevelId", required = true)Integer supplierLevelId,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("根据供应商等级id查询供应商附加分信息，参数为：supplierLevelId="+supplierLevelId );
            List<SupplierLevelAdditional> list =    supplierLevelService.findSupplierLevelAdditionalBySupplierLevelId(supplierLevelId);
            return AjaxResponse.success(list);

        }catch (Exception e){
            logger.error("根据供应商等级id查询供应商附加分信息异常，参数为：supplierLevelId="+supplierLevelId ,e);
            return AjaxResponse.fail(-1,"根据供应商等级id查询供应商附加分信息异常");
        }
    }

    @RequestMapping(value="export",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse export(
            SupplierLevel entity,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("导出供应商等级，参数为：entity="+JSON.toJSONString(entity) );


            int pageno = 1;
            int pagesize = 2000;
            PageInfo<SupplierLevel>  pageInfo =   supplierLevelService.findPage(pageno,pagesize,entity);

            String fileName = "供应商等级"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
            //获得浏览器信息并转换为大写
            String agent = request.getHeader("User-Agent").toUpperCase();
            //IE浏览器和Edge浏览器
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }


            CsvUtils utilEntity = new CsvUtils();
            String header = "供应商名称,城市,月份,有效周期,规模分,效率分,服务分,附加分,等级分,等级,状态";
            List<String> headerList = new ArrayList<>();
            headerList.add(header);
            if( pageInfo.getTotal() == 0){
                List<String> stringList = new ArrayList<>();
                stringList.add("没有查到符合条件的数据");

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
                            stringBuffer.append("待发布");
                        }else {
                            stringBuffer.append("已发布");
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
            logger.error("导出供应商等级异常，参数为：entity="+JSON.toJSONString(entity) ,e);
            return AjaxResponse.fail(-1,"导出供应商等级异常");
        }
    }


    @RequestMapping(value="exportadditionalScore",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse exportadditionalScore(
            SupplierLevel entity,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("供应商等级附加分，参数为：entity="+JSON.toJSONString(entity) );


            String fileName = "供应商等级附加分"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
            //获得浏览器信息并转换为大写
            String agent = request.getHeader("User-Agent").toUpperCase();
            //IE浏览器和Edge浏览器
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }

            int pageno = 1;
            int pagesize = 2000;
            PageInfo<SupplierLevel>  pageInfo =   supplierLevelService.findPage(pageno,pagesize,entity);

            CsvUtils utilEntity = new CsvUtils();
            String header = "供应商名称,结算月份,附件项名称,附加分";
            List<String> headerList = new ArrayList<>();
            headerList.add(header);
            if( pageInfo.getTotal() == 0){
                List<String> stringList = new ArrayList<>();
                stringList.add("没有查到符合条件的数据");

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
                        StringBuffer stringBuffer = new StringBuffer();
                        if(additionList != null){

                            stringBuffer.append(StringUtils.isEmpty(item.getSupplierName())?"":item.getSupplierName());
                            stringBuffer.append(",");

                            stringBuffer.append(StringUtils.isEmpty(item.getMonth())?"":item.getMonth());
                            stringBuffer.append(",");
                            for(SupplierLevelAdditional itemAddition:additionList){
                                stringBuffer.append(itemAddition.getItemName());
                                stringBuffer.append(",");

                                stringBuffer.append(itemAddition.getItemValue());
                            }
                        }else{

                            stringBuffer.append(StringUtils.isEmpty(item.getSupplierName())?"":item.getSupplierName());
                            stringBuffer.append(",");

                            stringBuffer.append(StringUtils.isEmpty(item.getMonth())?"":item.getMonth());
                            stringBuffer.append(",");
                            stringBuffer.append(",");
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
            logger.error("供应商等级附加分异常，参数为：entity="+JSON.toJSONString(entity) ,e);
            return AjaxResponse.fail(-1,"供应商等级附加分异常");
        }
    }

    /**
     * 导入供应商附加分
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

        logger.info("导入供应商等级信息:month=" + month+",fileSize="+(file==null?"null":file.getSize()));
        try{
            //解析要导入的文档
            Map<String, Object> result = _doPareImportData(    file, month,    request);
            if(Integer.parseInt(result.get("result").toString()) == -1){
                AjaxResponse errorAjaxResponse = AjaxResponse.failMsg(-2,"请使用模板");
                errorAjaxResponse.setMsg("请使用模板");
                return errorAjaxResponse;
            }else if(result.get("error")==Boolean.TRUE){
                //有异常则标识导入失败
                AjaxResponse errorAjaxResponse = AjaxResponse.success(result);
                errorAjaxResponse.setMsg("上传数据中有问题,点击下载查看");
                return errorAjaxResponse;
            }else{
                //无异常则数据入库
                List<SupplierLevelAdditional> supplierLevelAdditionalDtos = (List<SupplierLevelAdditional>) result.get("dataList");
                supplierLevelService.doImportSupplierLevelAdditional(supplierLevelAdditionalDtos);

                return AjaxResponse.success(Boolean.TRUE);
            }
        }catch (Exception e){
            logger.error("导入供应商等级信息异常:month=" + month+",fileSize="+(file==null?"null":file.getSize()));
            AjaxResponse errorAjaxResponse = AjaxResponse.failMsg(-1,"上传数据中有问题,点击下载查看");
            return errorAjaxResponse;
        }


    }

    @RequestMapping("/downloadtemplate")
    public void template( HttpServletRequest request,HttpServletResponse response){
        logger.info("下载供应商附加分导入模板");
        try {
            String path = request.getRealPath("/") + File.separator + "template"   + File.separator + "supplierlevelAdditional.xlsx";
            this.fileDownload(request,response,path);
        } catch (Exception e) {
            logger.error("下载供应商附加分导入模板异常", e);
        }
    }


    /**拉取大数据的数据，开始本地创建数据
     * 供给测试使用
     * @param request
     * @param response
     * @param month
     */
    @RequestMapping("/doGenerateByDate")
    @ResponseBody
    public AjaxResponse doGenerateByDate( HttpServletRequest request,HttpServletResponse response,String month){
        logger.info("生成供应商等级分：month="+month);
        try {
            supplierLevelService.doGenerateByDate(month);

            return AjaxResponse.success("成功");
        } catch (Exception e) {
            logger.error("生成供应商等级分异常,month="+month, e);
            return AjaxResponse.failMsg(-1,"异常");
        }
    }
    /**
     * 供给测试使用
     * @param request
     * @param response
     * @param month
     */
    @RequestMapping("/doSeqence")
    @ResponseBody
    public AjaxResponse doSeqence( HttpServletRequest request,HttpServletResponse response,String month){
        logger.info("排名并发布：month="+month);
        try {
            supplierLevelService.doSeqence(month);

            return AjaxResponse.success("成功");
        } catch (Exception e) {
            logger.error("排名并发布异常,month="+month, e);
            return AjaxResponse.failMsg(-1,"异常");
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

            // 检查模板是否正确
            Row row1 = sheet.getRow(0);
            if(row1==null){
                logger.info("导入模板错误L1");
                result.put("result", -1);
                result.put("msg", "导入模板错误");

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
                if (!"供应商名称".equals(cell0V) || !"附加项目".equals(cell1V) || !"附加分".equals(cell2V)) {
                    logger.info("导入模板错误L2，列名不一致。第一列名称为：" + cell0V + ",第二列名称为：" + cell1V + ",第三列名称为" + cell2V);
                    result.put("result", -1);
                    result.put("msg", "导入模板错误");
                    return result;
                }
            }catch (Exception e){
                logger.info("导入模板错误L3，列名不一致。第一列名称为：" + cell0V + ",第二列名称为：" + cell1V + ",第三列名称为" + cell2V);
                result.put("result", -1);
                result.put("msg", "导入模板错误");
                return result;
            }


            int minRowIx = 1;// 过滤掉标题，从第一行开始导入数据
            int maxRowIx = sheet.getLastRowNum(); // 要导入数据的总条数

            Boolean haveError = false;
            String supplierName = null;
            String itemName = null;
            String itemValue = null;


            Map<String,Integer> rowItemCache = new HashMap<>();
            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                  cell0 =null;
                  cell1 =null;
                  cell2 =null;

                Row row = sheet.getRow(rowIx); // 获取行对象
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
                    itemValue = cell2.getNumericCellValue()+"";
                }

                ImportCheckEntity checkResult = checkEntity(rowIx,supplierName,itemName,itemValue,month,rowItemCache);

                if(checkResult.getResult()){
                    SupplierLevelAdditional entity = new SupplierLevelAdditional();
                    entity.setItemName(itemName);
                    entity.setSupplierLevelId(checkResult.getForeignId());
                    entity.setItemValue(new BigDecimal(itemValue));
                    supplierLevelAdditionalDtoList.add(entity);

                }else {
                    //数据无效
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
                // 将错误列表导出
                if (listException.size() > 0) {
                    Date now = new Date();
                    //保存文件
                    String fileName =  new String("ERROR".getBytes("utf-8"), "iso8859-1");

                    download = "/template/error/" + fileName + buildRandom(2) + "_"+ System.currentTimeMillis() + ".csv";
                   boolean exportResult = exportExcel(CommonConfig.ERROR_BASE_FILE+download, listException);
                }
            } catch (Exception e) {
                logger.error("导入供应商等级的附加分异常V1" ,e);
            }
        } catch (Exception e) {
            logger.error("导入供应商等级的附加分异常V2" ,e);
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
        throw new IllegalArgumentException("你的excel版本目前poi解析不了");
    }
    private ImportCheckEntity checkEntity(Integer rowNum,String supplierName,String itemName,String itemValue,String month,Map<String,Integer> rowItemCache){
        ImportCheckEntity result = new ImportCheckEntity();
        result.setResult(true);

        if(rowItemCache.containsKey(supplierName+"_"+itemName)){
            result.setResult(false);
            result.setReason("该供应商的这个类目重复出现");
        }else{
            rowItemCache.put(supplierName+"_"+itemName,rowNum);

            SupplierLevel supplierLevel =   supplierLevelService.findByMonthAndSupplierName(month,supplierName);
            if(supplierLevel == null){
                result.setResult(false);
                result.setReason("供应商无等级分记录");
            }else if(supplierLevel.getStates() != 1){
                result.setResult(false);
                result.setReason("供应商无待发布的等级分记录");
            }else {
                Integer supplierLevelId = supplierLevel.getId();

                SupplierLevelAdditional additional =  supplierLevelService.findBySupplierLevelIdAndSupplierLevelAdditionalName(supplierLevelId,itemName);
                if(additional != null){
                    result.setResult(false);
                    result.setReason("该附加项已存在");
                }else {
                    if(StringUtils.isEmpty(itemValue)){
                        result.setResult(false);
                        result.setReason("供应商的附加分不能为空");
                    } else if(itemValue.trim().equals("0")){
                        result.setResult(false);
                        result.setReason("供应商的附加分不能为0");
                    }else {
                        result.setResult(true);
                        result.setForeignId(supplierLevel.getId());
                    }
                }
            }
        }


        return result;
    }
    public boolean exportExcel(String path,
                                List<JSONObject> list) throws Exception {
        if (list != null && list.size() > 0) {
            File f = new File(path);
            List<String > errorList = new ArrayList<>();
            int i = 0;
            for (JSONObject s : list) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(s.containsKey("supplierName")? s.getString("supplierName"):"");
                stringBuffer.append(",");
                stringBuffer.append(s.containsKey("itemName")?s.getString("itemName"):"");
                stringBuffer.append(",");
                stringBuffer.append(s.containsKey("itemValue")?s.getString("itemValue"):"");
                stringBuffer.append(",");
                stringBuffer.append(s.containsKey("errorReason")?s.getString("errorReason"):"");
                errorList.add(stringBuffer.toString());
                i++;
            }

            CsvUtils2File.exportCsv(f,errorList);
        }
        return true;
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
     *下载导入错误的信息
     */
    @RequestMapping("/exportException")
    public void exportException(String fileName, HttpServletRequest request,HttpServletResponse response){

        logger.info("供应商等级下载错误：下载错误的信息");
        String path = CommonConfig.ERROR_BASE_FILE + fileName;
        this.fileDownload(request,response,path);
    }

    /*
     * 下载
     */
    private void fileDownload(HttpServletRequest request, HttpServletResponse response,String path) {

        File file = new File(path);// path是根据日志路径和文件名拼接出来的
        String filename = file.getName();// 获取日志文件名称
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            // 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            os.write(buffer);// 输出文件
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
