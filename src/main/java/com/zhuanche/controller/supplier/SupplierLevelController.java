package com.zhuanche.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.CommonConfig;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.ImportCheckEntity;
import com.zhuanche.dto.driver.SupplierLevelAdditionalDto;
import com.zhuanche.entity.driver.SupplierLevel;
import com.zhuanche.entity.driver.SupplierLevelAdditional;
import com.zhuanche.serv.supplier.SupplierLevelService;
import com.zhuanche.util.Common;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
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

    /**
     * 发布供应商等级
     * @param ids
     * @param request
     * @param response
     * @param modelMap
     * @return
     */
    @RequestMapping(value="doPublishSupplierLevel",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse doPublishSupplierLevel(
            @RequestParam(value = "ids", required = true)String ids,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("发布供应商等级，参数为：ids="+ids );
            if(StringUtils.isEmpty(ids)){

                return AjaxResponse.fail(-2,"参数ids为空");
            }else{
                String[]idArray = ids.split(",");
                List<Integer> integerIdList = new ArrayList<>();
                for(int i=0;i<idArray.length;i++){
                    integerIdList.add(Integer.parseInt(idArray[i]));
                }
                supplierLevelService.doPublishSupplierLevel(integerIdList);
                return AjaxResponse.success(Boolean.TRUE);
            }

        }catch (Exception e){
            logger.error("发布供应商等级异常，参数为：ids="+ids ,e);
            return AjaxResponse.fail(-1,"发布供应商等级异常");
        }
    }

    @RequestMapping(value="doUnPublishSupplierLevel",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse doUnPublishSupplierLevel(
            @RequestParam(value = "ids", required = true)String ids,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("取消发布供应商等级，参数为：ids="+ids );
            if(StringUtils.isEmpty(ids)){

                return AjaxResponse.fail(-2,"参数ids为空");
            }else{
                String[]idArray = ids.split(",");
                List<Integer> integerIdList = new ArrayList<>();
                for(int i=0;i<idArray.length;i++){
                    integerIdList.add(Integer.parseInt(idArray[i]));
                }
                supplierLevelService.doUnPublishSupplierLevel(integerIdList);
                return AjaxResponse.success(Boolean.TRUE);
            }

        }catch (Exception e){
            logger.error("取消发布供应商等级异常，参数为：ids="+ids ,e);
            return AjaxResponse.fail(-1,"取消发布供应商等级异常");
        }
    }

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

    @RequestMapping(value="dodeletebysupplierlevelid",method= {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public AjaxResponse dodeletebysupplierlevelid(
            @RequestParam(value = "supplierLevelAdditionalId", required = true)Integer supplierLevelAdditionalId,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            logger.info("根据供应商等级附加分的id进行删除附加分，参数为：supplierLevelAdditionalId="+supplierLevelAdditionalId );
            supplierLevelService.doDeleteBySupplierLevelId(supplierLevelAdditionalId);
            return AjaxResponse.success(Boolean.TRUE);

        }catch (Exception e){
            logger.error("根据供应商等级附加分的id进行删除附加分异常，参数为：supplierLevelAdditionalId="+supplierLevelAdditionalId ,e);
            return AjaxResponse.fail(-1,"根据供应商等级附加分的id进行删除附加分异常");
        }
    }

    @ResponseBody
    @RequestMapping("/importsupplierleveladditional")
    public AjaxResponse importCarStatus(@RequestParam(value = "filename", required = true)String filename,
                                        @RequestParam(value = "month", required = true)String month,
                                        HttpServletRequest request){

        logger.info("导入供应商等级信息:参数filename=" + filename);
        Map<String, Object> result = _doPareImportData(  filename, month,    request);
        if(result.get("error")==Boolean.TRUE){

            return AjaxResponse.fail(-1,result);
        }else{
            List<SupplierLevelAdditional> supplierLevelAdditionalDtos = (List<SupplierLevelAdditional>) result.get("dataList");
            supplierLevelService.doImportSupplierLevelAdditional(supplierLevelAdditionalDtos);

            return AjaxResponse.success(Boolean.TRUE);
        }

        //error
       // result = this.carAssetDailyService.importCarInfo(params,  request);

    }

    private Map<String, Object> _doPareImportData(String filename,String month,   HttpServletRequest request){


        String path  = Common.getPath(request);
        String dirPath = path+filename;
        File DRIVERINFO = new File(dirPath);
        Map<String, Object> result = new HashMap<String, Object>();
        String download = null;
        List<JSONObject> listException = new ArrayList<>();

        List<SupplierLevelAdditional> supplierLevelAdditionalDtoList = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(DRIVERINFO);
            Workbook workbook = null;
            String fileType = filename.split("\\.")[1];
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


            int minRowIx = 1;// 过滤掉标题，从第一行开始导入数据
            int maxRowIx = sheet.getLastRowNum(); // 要导入数据的总条数
            // int successCount = 0;// 成功导入条数

            Boolean haveError = false;
            String supplierName = null;
            String itemName = null;
            String itemValue = null;



            for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
                Row row = sheet.getRow(rowIx); // 获取行对象

                supplierName = row.getCell(0).getStringCellValue();
                itemName = row.getCell(1).getStringCellValue();
                itemValue = row.getCell(2).getStringCellValue();
                ImportCheckEntity checkResult = checkEntity(rowIx,supplierName,itemName,itemValue,month);


                if(checkResult.getResult()){
                    SupplierLevelAdditional entity = new SupplierLevelAdditional();

                    //TODO 根据供应商和月份来获得供应商等级的ID
                    entity.setItemName(itemName);
                    entity.setItemValue(new BigDecimal(itemValue));
                    supplierLevelAdditionalDtoList.add(entity);

                }else {
                    //数据无效
                    JSONObject error = new JSONObject();
                    error.put("supplierName","");
                    error.put("itemName","");
                    error.put("itemValue","");
                    error.put("errorReason",checkResult.getReason());

                    listException.add(error);

                    haveError = true;
                }

            }

            Workbook wb = null;
            try {
                // 将错误列表导出
                if (listException.size() > 0) {
                    Date now = new Date();
                    wb = exportExcel(request.getServletContext().getRealPath("/")+ "template" + File.separator + "supplierLevel_exception"+now.getTime()+".xlsx", listException);
                    download = exportExcelFromTempletToLoacl(request, wb,
                            new String("ERROR".getBytes("utf-8"), "iso8859-1"));
                }
            } catch (Exception e) {
                logger.error("导入供应商等级的附加分异常" ,e);
            }finally {
                if (listException.size() > 0) {
                    try {
                        wb.close();
                    } catch (IOException e) {
                        logger.error("导入供应商等级的附加分异常" ,e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("车辆状态文件导入异常" ,e);
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
        }else {
            result.put("error", true);
            result.put("dataList", supplierLevelAdditionalDtoList);
        }
        return result;
    }

    public ImportCheckEntity checkEntity(Integer rowNum,String supplierName,String itemName,String itemValue,String month){
        ImportCheckEntity result = new ImportCheckEntity();
        result.setResult(true);
        return result;
    }
    public Workbook exportExcel(String path,
                                List<JSONObject> list) throws Exception {
        FileInputStream io = new FileInputStream(path);
        // 创建 excel
        Workbook wb = new XSSFWorkbook(io);

        if (list != null && list.size() > 0) {
            Sheet sheet = wb.getSheetAt(0);

            int i = 0;
            for (JSONObject s : list) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(s.getString("supplierName"));
                row.createCell(1).setCellValue(s.getString("itemName"));
                row.createCell(1).setCellValue(s.getString("itemValue"));
                row.createCell(1).setCellValue(s.getString("errorReason"));
                i++;
            }
        }
        return wb;
    }

    public String exportExcelFromTempletToLoacl(HttpServletRequest request,
                                                Workbook wb, String fileName) throws Exception {
        if (StringUtils.isEmpty(fileName)) {
           throw new RuntimeException("fileName不能为空");
        }
        // 生成文件相对路径
        String fileURI = "/template/error/" + fileName + buildRandom(2) + "_"
                + System.currentTimeMillis() + ".xlsx";

        FileOutputStream fos = new FileOutputStream(CommonConfig.ERROR_BASE_FILE + fileURI);
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
}
