package com.zhuanche.controller.supplierFee;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.SupplierFeeManageDetailDto;
import com.zhuanche.dto.mdbcarmanage.SupplierFeeManageDto;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeExt;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeRecord;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeRecordService;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.dateUtil.DateUtil;
import com.zhuanche.util.excel.SupplierFeeCsvUtils;
import mapper.mdbcarmanage.ex.SupplierFeeExtExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

/**
 * @Author fanht
 * @Description ????????????????????????
 * @Date 2019/9/11 ??????7:00
 * @Version 1.0
 */
@Controller
@RequestMapping("/supplierFee")
public class SupplierFeeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private SupplierFeeService supplierFeeService;

    @Autowired
    private SupplierFeeRecordService recordService;

    @Autowired
    private SupplierFeeExtExMapper extExMapper;


    @RequestMapping("/listSupplierFee")
    //@RequiresPermissions(value = { "DriverInvite_look" } )
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    //@SensitiveDataOperationLog(primaryDataType="??????????????????",secondaryDataType="??????????????????????????????",desc="??????????????????????????????")
    //@RequestFunction(menu = DRIVER_JOIN_PROMOTE_LIST)
    public AjaxResponse listSupplierFee( @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                         @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                         Integer cityId, Integer supplierId,
                                         Integer status, Integer amountStatus, String settleStartDate,
                                         String settleEndDate,String paymentStartTime,String paymentEndTime){
        logger.info(MessageFormat.format("???????????????????????????:pageSize:%s,pageNum:{0},cityId:{1},supplierId:{2},status:{3}," +
                        "amountStatus:{4},settleStartDate:{5},settleEndDate:{6},paymentStartTime:{7},paymentEndTime:{8}",pageSize,
                pageNum,cityId,supplierId,status,amountStatus,settleStartDate,settleEndDate,paymentStartTime,paymentEndTime));

        PageDTO pageDTO = null;
        try {
            SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
            SupplierFeeManageDto feeManageDto = new SupplierFeeManageDto();

            if (!WebSessionUtil.isSupperAdmin()){
                String sessionCityIds = StringUtils.join(user.getCityIds().toArray(), ",");
                String sessionSuppliers = StringUtils.join(user.getSupplierIds().toArray(), ",");
                feeManageDto.setCityIds(sessionCityIds);
                feeManageDto.setSupplierIds(sessionSuppliers);
            }

            feeManageDto.setCityId(cityId);
            feeManageDto.setSettleStartDate(settleStartDate);
            feeManageDto.setSettleEndDate(settleEndDate);
            feeManageDto.setPaymentStartTime(paymentStartTime);
            feeManageDto.setPaymentEndTime(paymentEndTime);
            feeManageDto.setAmountStatus(amountStatus);
            feeManageDto.setSupplierId(supplierId);
            feeManageDto.setStatus(status);
            Page page = PageHelper.startPage(pageNum,pageSize);
            List<SupplierFeeManage> feeManageList = supplierFeeService.queryListData(feeManageDto);
            PageHelper.clearPage();
            pageDTO = new PageDTO(pageNum, pageSize, page.getTotal(), feeManageList);
        } catch (Exception e) {
            logger.error("???????????????????????????",e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        return AjaxResponse.success(pageDTO);
    }


    /**
     * ??????????????????
     * @param feeOrderNo
     * @return
     */
    @RequestMapping("/supplierFeeDetail")
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public AjaxResponse supplierFeeDetail(@Verify(param = "feeOrderNo",rule = "required") String feeOrderNo){

        logger.info("??????????????????????????????????????????:" + feeOrderNo);

        SupplierFeeManageDetailDto detailDto = null;
        try {
            SupplierFeeManage supplierFeeManage = supplierFeeService.queryByOrderNo(feeOrderNo);
            detailDto = new SupplierFeeManageDetailDto();
            if(supplierFeeManage!=null){
                BeanUtils.copyProperties(supplierFeeManage,detailDto);
                detailDto.setScaleEfficient(this.getTwoPoint(detailDto.getScaleEfficient()));
                detailDto.setFlowIncrease(this.getTwoPoint(detailDto.getFlowIncrease()));
                detailDto.setGrowthFactor(this.getTwoPoint(detailDto.getGrowthFactor()));
                detailDto.setBadRatings(this.getTwoPoint(detailDto.getBadRatings()));
                List<SupplierFeeRecord> recordList = recordService.listRecord(feeOrderNo);
                if(CollectionUtils.isNotEmpty(recordList)){
                    detailDto.setSupplierFeeRecordList(recordList);
                }
            }
        } catch (BeansException e) {
            logger.error("?????????????????????",e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return AjaxResponse.success(detailDto);
    }


    /**
     * ???????????????
     * @return
     */
    @RequestMapping("/supplierFeeOpe")
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.MASTER)
    } )
    public AjaxResponse supplierFeeOpe(@Verify(param = "status",rule = "required") Integer status,
                                       @Verify(param = "remark",rule = "required") String remark,
                                       @Verify(param = "feeOrderNo",rule = "required") String feeOrderNo,
                                       @Verify(param = "amountStatus",rule = "required")Integer amountStatus){
        logger.info("??????????????????????????????????????????" + status,remark,feeOrderNo);
        SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(null == ssoLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }
        Integer loginId = ssoLoginUser.getId();
        String userName = ssoLoginUser.getName();
        try {
            SupplierFeeRecord record = new SupplierFeeRecord();
            record.setOperate(SupplierFeeManageEnum.BECONFIRMED.getMsg());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setOperateId(loginId);
            record.setStatus(SupplierFeeManageEnum.BECONFIRMED.getCode());
            record.setRemark(remark);
            record.setOperateUser(userName);
            record.setSupplierAddress("???");
            record.setFeeOrderNo(feeOrderNo);

            int code =  recordService.insertFeeRecord(record);


            if(status.equals(SupplierFeeManageEnum.SUBSTITUTETICKET.getCode())){

                SupplierFeeRecord recordTwo = new SupplierFeeRecord();
                recordTwo.setOperate(SupplierFeeManageEnum.APPLYCATCH.getMsg());
                recordTwo.setCreateTime(new Date());
                recordTwo.setUpdateTime(new Date());
                recordTwo.setOperateId(loginId);
                recordTwo.setStatus(SupplierFeeManageEnum.APPLYCATCH.getCode());
                recordTwo.setRemark(remark);
                recordTwo.setOperateUser(userName);
                recordTwo.setSupplierAddress("???");
                recordTwo.setFeeOrderNo(feeOrderNo);

                recordService.insertFeeRecord(recordTwo);
            }

            if(code > 0){
                int feeCode = 0;
                if(status == 0){
                    feeCode = supplierFeeService.updateStatusAndAmount(feeOrderNo,amountStatus,status);
                }else {
                    feeCode = supplierFeeService.updateStatusByFeeOrderNo(feeOrderNo,amountStatus);
                }
                if(feeCode > 0 ){
                    logger.info("??????????????????");
                }
                logger.info("????????????success");
            }else {
                logger.info("????????????error");
            }
        } catch (Exception e) {
            logger.info("??????????????????????????????",e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return AjaxResponse.success(null);
    }


    /**
     * ???????????????-PDF
     * @return
     */
    @RequestMapping("/exportPDFSupplierFeeData")
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
    } )
    public AjaxResponse exportPDFSupplierFeeData(HttpServletResponse response,
                                                 ServletOutputStream outputStream,
                                                 String feeOrderNo){
        logger.info(MessageFormat.format("???????????????????????????:feeOrderNo:{0}",feeOrderNo));
        response.setContentType("application/pdf;charset=ISO8859-1");
        response.setHeader("Content-disposition", "attachment; filename="+"supplierFeePDF-1.pdf");
        SupplierFeeManage manage = supplierFeeService.queryByOrderNo(feeOrderNo);


        String[] titles = { "          ???????????????", "          ??????????????????", "          ??????????????????", "          ?????????", "          ????????????", "          ????????????","          ?????????","          ?????????",
                "          ??????????????????", "          ????????????","          ???????????????","          ????????????","          ????????????","          ?????????","          ????????????","          ????????????","          ??????????????????",
                "          ??????","          ??????????????????","          ??????","          ????????????","          ??????????????????","          ????????????","          ???????????????"};
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A3); // Step 1???Create a Document.
            PdfWriter writer;
            writer = PdfWriter.getInstance(document, ba);
            document.open();


            document.setMargins(5, 5, 5, 5);
            /**H ??????????????????????????????????????? V ????????????*/
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);//STSongStd-Light ???????????????jar ??????property?????????
            /**?????????????????????????????????????????????????????????????????????????????????????????????????????????|?????????*/
            Font topfont = new Font(bfChinese,14,Font.BOLD);

            Paragraph blankRow1 = new Paragraph(18f, " ");
            blankRow1.setAlignment(Element.ALIGN_CENTER);


            PdfPTable table1 = new PdfPTable(titles.length); //??????????????????,????????????????????????
            int[] width1 = {250,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300};//???????????????
            table1.setKeepTogether(false);
            table1.setHorizontalAlignment(Element.ALIGN_LEFT);
            /**??????????????????????????????????????????300%*/
            table1.setWidthPercentage(100);
            /**????????????*/
            table1.setWidths(width1);


            for(int i=0;i<titles.length;i++){
                PdfPCell cell1 = new PdfPCell(new Paragraph(titles[i],topfont));
                table1.addCell(cell1);
            }

            /**???????????? ???????????????????????????????????????????????????pdf???????????????????????????????????????*/
            PdfPCell cell1 = new PdfPCell(new Paragraph(manage.getSupplierName(),topfont));
            table1.addCell(cell1);
            PdfPCell cell2= new PdfPCell(new Paragraph(DateUtils.formatDate(manage.getSettleStartDate(),DateUtils.dateTimeFormat_parttern),topfont));
            table1.addCell(cell2);
            PdfPCell cell3= new PdfPCell(new Paragraph(DateUtils.formatDate(manage.getSettleEndDate(),DateUtils.dateTimeFormat_parttern),topfont));
            table1.addCell(cell3);
            PdfPCell cell4= new PdfPCell(new Paragraph(manage.getTotalFlow(),topfont));
            table1.addCell(cell4);
            PdfPCell cell5= new PdfPCell(new Paragraph(manage.getFlowAmount(),topfont));
            table1.addCell(cell5);
            PdfPCell cell6= new PdfPCell(new Paragraph(manage.getWindControlAmount(),topfont));
            table1.addCell(cell6);

            PdfPCell cell7= new PdfPCell(new Paragraph(manage.getExtraCharge(),topfont));
            table1.addCell(cell7);
            PdfPCell cell8= new PdfPCell(new Paragraph(manage.getCancelCharge(),topfont));
            table1.addCell(cell8);
            PdfPCell cell9= new PdfPCell(new Paragraph(manage.getTotalAmountWater(),topfont));
            table1.addCell(cell9);

            PdfPCell cell10= new PdfPCell(new Paragraph(manage.getScaleEfficient(),topfont));
            table1.addCell(cell10);
            PdfPCell cell11= new PdfPCell(new Paragraph(manage.getTotalFlowLastMonth(),topfont));
            table1.addCell(cell11);
            PdfPCell cell12= new PdfPCell(new Paragraph(manage.getFlowIncrease(),topfont));
            table1.addCell(cell12);


            PdfPCell cell13= new PdfPCell(new Paragraph(manage.getGrowthFactor(),topfont));
            table1.addCell(cell13);
            PdfPCell cell14= new PdfPCell(new Paragraph(manage.getBadRatings(),topfont));
            table1.addCell(cell14);
            PdfPCell cell15= new PdfPCell(new Paragraph(manage.getMonthCommission(),topfont));
            table1.addCell(cell15);

            PdfPCell cell16= new PdfPCell(new Paragraph(manage.getExcludeCommission(),topfont));
            table1.addCell(cell16);
            PdfPCell cell17= new PdfPCell(new Paragraph(manage.getDeductionAmountLastMonth(),topfont));
            table1.addCell(cell17);
            PdfPCell cell18= new PdfPCell(new Paragraph(manage.getTotal(),topfont));
            table1.addCell(cell18);

            PdfPCell cell19= new PdfPCell(new Paragraph(manage.getComplianceDriverAward(),topfont));
            table1.addCell(cell19);
            PdfPCell cell20= new PdfPCell(new Paragraph(manage.getOthers().toString(),topfont));
            table1.addCell(cell20);
            PdfPCell cell21= new PdfPCell(new Paragraph(manage.getBadRatingsAward(),topfont));
            table1.addCell(cell21);

            PdfPCell cell22= new PdfPCell(new Paragraph(manage.getAmountAssessmentSum(),topfont));
            table1.addCell(cell22);
            PdfPCell cell23= new PdfPCell(new Paragraph(manage.getInspectionFines(),topfont));
            table1.addCell(cell23);
            PdfPCell cell24= new PdfPCell(new Paragraph(manage.getTotalManageFees(),topfont));
            table1.addCell(cell24);
            /**??????????????????document???*/
            document.add(table1);
            document.add(blankRow1);
            document.close();
            ba.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
            ba.close();

        } catch (Exception e) {
            logger.error("??????PDF??????" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return AjaxResponse.success(null);
    }

    /**
     * ???????????????-EXCEL
     * @return
     */
    @RequestMapping("/exportSupplierFeeData")
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
    } )
    public AjaxResponse exportSupplierFeeData(HttpServletResponse response,HttpServletRequest request,
                                              String feeOrderNo){
        logger.info(MessageFormat.format("???????????????:feeOrderNo:{0}",feeOrderNo));
        try {
            SupplierFeeManage manage = supplierFeeService.queryByOrderNo(feeOrderNo);
            List<String> headerList = new ArrayList<>();
            String titles = "?????????ID,???????????????,?????????,????????????,??????????????????,??????????????????,????????????,?????????????????????,????????????,????????????,?????????,?????????,??????????????????,????????????,???????????????,?????????????????????,??????????????????,????????????,????????????,?????????????????????," +
                    "??????????????????,????????????,?????????,???????????????,?????????????????????,??????????????????,??????????????????,????????????,??????????????????,????????????,????????????," +
                    "??????????????????,????????????,??????????????????,??????????????????,??????????????????,????????????,???????????????,???????????????,?????????????????????????????????";


            String fileName = "???????????????" + DateUtil.dateFormat(new Date(), DateUtil.intTimestampPattern)+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //???????????????????????????????????????
            try {
                if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE????????????Edge?????????
                    fileName = URLEncoder.encode(fileName, "UTF-8");
                } else {
                    fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
                }
            } catch (UnsupportedEncodingException e) {
                logger.info("?????????????????????" +e);
            }

            boolean isLast = true;
            boolean isFirst = true;

            SupplierFeeCsvUtils entity = new SupplierFeeCsvUtils();
            List<String> listStr = new ArrayList<>();
            Map<String,Object> map = getData(manage,listStr,titles);
            listStr = (List<String>) map.get("listStr");

            listStr = this.getExtList(listStr,manage.getId());

            int length = (int) map.get("length");
            logger.info("headerList:" + JSONObject.toJSONString(headerList));

            List<String> footerList = new ArrayList<>();
            footerList = this.footerList(footerList);
            try {

                entity.exportCsvV2(response,listStr,headerList,fileName,isFirst,isLast,footerList,length);
            } catch (IOException e) {
                logger.error("????????????",e);
            }
        } catch (Exception e) {
            logger.error("?????????????????????",e);

        }
        return AjaxResponse.success(null);
    }


    /**
     * ??????????????????
     * @param listStr
     * @param supplierExtId
     * @return
     */
    private List<String> getExtList(List<String> listStr,Integer supplierExtId){
        try {
            List<SupplierFeeExt> extList = extExMapper.queryBySupplierFeeId(supplierExtId);
            if(CollectionUtils.isEmpty(extList)){
                return listStr;
            }
            extList.forEach(i->{
                listStr.add(i.getFieldName()+":" + i.getFieldValue());
            });

            return listStr;
        } catch (Exception e) {
            logger.error("????????????",e);
        }
        return listStr;
    }

    private Map<String,Object> getData(SupplierFeeManage manage,List<String> listStr,String title){

        Map<String,Object> mapData = Maps.newHashMap();

        List<String> headList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isEmpty(manage.getSupplierId()+"")){
            title = title.replaceAll("?????????ID,","");
        }else {
            builder.append(manage.getSupplierId()).append(",");
            listStr.add("?????????ID:"+manage.getSupplierId());
        }


        if(StringUtils.isEmpty(manage.getSupplierName() )){
            title = title.replaceAll("???????????????,","");

        }else {
            builder.append(manage.getSupplierName());
            builder.append(",");
            listStr.add("???????????????:"+manage.getSupplierName());
        }


        if(StringUtils.isEmpty(manage.getSupplierFullName())){
            title = title.replaceAll("?????????,","");
            title = title.replaceAll("????????????,","");
        }else {
            title = title.replaceAll("????????????,",",");
            builder.append(manage.getSupplierFullName() != null ? manage.getSupplierFullName() : "").append(",");
            builder.append("").append(",");
            listStr.add("?????????:"+manage.getSupplierFullName());

        }


        if(manage.getSettleStartDate() == null){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getSettleStartDate() != null ? DateUtils.formatDate(manage.getSettleStartDate(),DateUtils.date_format) : "");
            builder.append(",");
            String beginDate = manage.getSettleStartDate() != null ? DateUtils.formatDate(manage.getSettleStartDate(),DateUtils.date_format) : "";
            listStr.add("??????????????????:"+beginDate);

        }

        if(manage.getSettleEndDate() == null){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getSettleEndDate() != null ? DateUtils.formatDate(manage.getSettleEndDate(),DateUtils.date_format) : "");
            builder.append(",");
            String endDate = manage.getSettleEndDate() != null ? DateUtils.formatDate(manage.getSettleEndDate(),DateUtils.date_format) : "";
            listStr.add("??????????????????:"+ endDate);

        }


        if(StringUtils.isEmpty(manage.getTotalFlow()) || paramIsNull(manage.getTotalFlow())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getTotalFlow() != null ? manage.getTotalFlow() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getTotalFlow());

        }




        if(StringUtils.isEmpty(manage.getTurnoverDrivers()) || paramIsNull(manage.getTurnoverDrivers())){
            title = title.replaceAll("?????????????????????,","");

        }else {
            builder.append(manage.getTurnoverDrivers() != null ? manage.getTurnoverDrivers() : "").append(",");
            listStr.add("?????????????????????:"+manage.getTurnoverDrivers());

        }
        if(StringUtils.isEmpty(manage.getFlowAmount()) || paramIsNull(manage.getFlowAmount())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getFlowAmount() != null ? manage.getTotalFlow() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getFlowAmount());
        }


        if(StringUtils.isEmpty(manage.getWindControlAmount()) || paramIsNull(manage.getWindControlAmount())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getWindControlAmount() != null ? manage.getWindControlAmount() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getWindControlAmount());

        }



        if(StringUtils.isEmpty(manage.getExtraCharge()) || paramIsNull(manage.getExtraCharge())){
            title = title.replaceAll("?????????,","");
        }else {
            builder.append(manage.getExtraCharge() != null ? manage.getExtraCharge() : "");
            builder.append(",");
            listStr.add("?????????:"+manage.getExtraCharge());

        }


        if(StringUtils.isEmpty(manage.getCancelCharge()) || paramIsNull(manage.getCancelCharge())){
            title = title.replaceAll("?????????,","");
        }else {
            builder.append(manage.getCancelCharge() != null ? manage.getCancelCharge() : "");
            builder.append(",");
            listStr.add("?????????:"+manage.getCancelCharge());

        }


        if(StringUtils.isEmpty(manage.getTotalAmountWater()) || paramIsNull(manage.getTotalAmountWater())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getTotalAmountWater() != null ? manage.getTotalAmountWater() : "");
            builder.append(",");
            listStr.add("??????????????????:"+manage.getTotalAmountWater());

        }


        if(StringUtils.isEmpty(manage.getScaleEfficient()) || paramIsNull(manage.getScaleEfficient())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getScaleEfficient() != null ? this.rateToPertent(manage.getScaleEfficient()): "");
            builder.append(",");
            listStr.add("????????????:"+this.rateToPertent(manage.getScaleEfficient()));

        }

        if(StringUtils.isEmpty(manage.getTotalFlowLastMonth()) || paramIsNull(manage.getTotalFlowLastMonth())){
            title = title.replaceAll("???????????????,","");
        }else {
            builder.append(manage.getTotalFlowLastMonth() != null ? manage.getTotalFlowLastMonth() : "");
            builder.append(",");
            listStr.add("???????????????:"+manage.getTotalFlowLastMonth());

        }

        if(StringUtils.isEmpty(manage.getPreRunCarNum()) || paramIsNull(manage.getPreRunCarNum())){
            title = title.replaceAll("?????????????????????,","");
        }else {
            builder.append(manage.getPreRunCarNum() != null ? manage.getPreRunCarNum() : "");
            builder.append(",");
            listStr.add("?????????????????????:"+manage.getPreRunCarNum());

        }


        if(StringUtils.isEmpty(manage.getRunCarIncreaseRate()) || paramIsNull(manage.getRunCarIncreaseRate())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getRunCarIncreaseRate() != null ? this.rateToPertent(manage.getRunCarIncreaseRate()) : "");
            builder.append(",");
            listStr.add("??????????????????:"+this.rateToPertent(manage.getRunCarIncreaseRate()) );

        }

        if(StringUtils.isEmpty(manage.getFlowIncrease() ) || paramIsNull(manage.getFlowIncrease())) {
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getFlowIncrease() != null ? this.rateToPertent(manage.getFlowIncrease()) : "");
            builder.append(",");
            listStr.add("????????????:"+this.rateToPertent(manage.getFlowIncrease()));

        }


        if(StringUtils.isEmpty(manage.getGrowthFactor())  || paramIsNull(manage.getGrowthFactor())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getGrowthFactor() != null ? this.rateToPertent(manage.getGrowthFactor()) : "");
            builder.append(",");
            listStr.add("????????????:"+this.rateToPertent(manage.getGrowthFactor()));

        }


        if(StringUtils.isEmpty(manage.getTotalDriverContribution()) || paramIsNull(manage.getTotalDriverContribution())){
            title = title.replaceAll("?????????????????????,","");
        }else {
            builder.append(manage.getTotalDriverContribution() != null ? manage.getTotalDriverContribution() : "").append(",");
            listStr.add("?????????????????????:"+manage.getTotalDriverContribution());

        }

        if(StringUtils.isEmpty(manage.getTotalComplianceAwards()) || paramIsNull(manage.getTotalComplianceAwards())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getTotalComplianceAwards() != null ? manage.getTotalComplianceAwards() : "").append(",");
            listStr.add("??????????????????:"+manage.getTotalComplianceAwards());

        }

        if(StringUtils.isEmpty(manage.getMonthCommission()) || paramIsNull(manage.getMonthCommission())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getMonthCommission() != null ? manage.getMonthCommission() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getMonthCommission());

        }


        if(StringUtils.isEmpty(manage.getBadRatings()) || paramIsNull(manage.getBadRatings())){
            title = title.replaceAll("?????????,","");
        }else {
            builder.append(manage.getBadRatings() != null ? this.rateToPertent(manage.getBadRatings()) : "");
            builder.append(",");
            listStr.add("?????????:"+this.rateToPertent(manage.getBadRatings()));

        }

        if(StringUtils.isEmpty(manage.getResponsibleComplainRate()) || paramIsNull(manage.getResponsibleComplainRate())){
            title = title.replaceAll("???????????????,","");
        }else {
            builder.append(manage.getResponsibleComplainRate() != null ? this.rateToPertent(manage.getResponsibleComplainRate()) : "");
            builder.append(",");
            listStr.add("???????????????:"+this.rateToPertent(manage.getResponsibleComplainRate()));

        }


        if(StringUtils.isEmpty(manage.getDriverHeadPhotoRunRate()) || paramIsNull(manage.getDriverHeadPhotoRunRate())){
            title = title.replaceAll("?????????????????????,","");
        }else {
            builder.append(manage.getDriverHeadPhotoRunRate() != null ? this.rateToPertent(manage.getDriverHeadPhotoRunRate()) : "");
            builder.append(",");
            listStr.add("?????????????????????:"+this.rateToPertent(manage.getDriverHeadPhotoRunRate()));
        }


        if(StringUtils.isEmpty(manage.getBaseShareRate()) || paramIsNull(manage.getBaseShareRate())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getBaseShareRate() != null ? this.rateToPertent(manage.getBaseShareRate()) : "");
            builder.append(",");
            listStr.add("??????????????????:"+this.rateToPertent(manage.getBaseShareRate()));
        }



        if(manage.getNumberOfActiveDrivers() == null || manage.getNumberOfActiveDrivers() == 0){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getNumberOfActiveDrivers() != null ? manage.getNumberOfActiveDrivers() : "").append(",");
            listStr.add("??????????????????:"+manage.getNumberOfActiveDrivers());

        }

        if(StringUtils.isEmpty(manage.getExcludeCommission()) || paramIsNull(manage.getExcludeCommission())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getExcludeCommission() != null ? manage.getExcludeCommission() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getExcludeCommission());

        }


        if(StringUtils.isEmpty(manage.getDeductionAmountLastMonth()) || paramIsNull(manage.getDeductionAmountLastMonth())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getDeductionAmountLastMonth() != null ? manage.getDeductionAmountLastMonth() : "");
            builder.append(",");
            listStr.add("??????????????????:"+manage.getDeductionAmountLastMonth());

        }


        if(StringUtils.isEmpty(manage.getIsReissue())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getIsReissue() != null ? manage.getIsReissue() : "").append(",");
            listStr.add("????????????:"+manage.getIsReissue());
        }

        if(StringUtils.isEmpty(manage.getTotal()) || paramIsNull(manage.getTotal())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getTotal() != null ? manage.getTotal() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getTotal());
        }


        if(StringUtils.isEmpty(manage.getComplianceDriverAward()) || paramIsNull(manage.getComplianceDriverAward())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getComplianceDriverAward() != null ? manage.getComplianceDriverAward() : "");
            builder.append(",");
            listStr.add("??????????????????:"+manage.getComplianceDriverAward());

        }


        if(StringUtils.isEmpty(manage.getBadRatingsAward()) || paramIsNull(manage.getBadRatingsAward())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getBadRatingsAward() != null ? manage.getBadRatingsAward() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getBadRatingsAward());

        }


        if(StringUtils.isEmpty(manage.getAmountAssessmentSum()) || paramIsNull(manage.getAmountAssessmentSum())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getAmountAssessmentSum() != null ? manage.getAmountAssessmentSum() : "");
            builder.append(",");
            listStr.add("??????????????????:"+manage.getAmountAssessmentSum());

        }


        if(StringUtils.isEmpty(manage.getGardenAward()) || paramIsNull(manage.getGardenAward())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getGardenAward() != null ? manage.getGardenAward() : "").append(",");
            listStr.add("??????????????????:"+manage.getGardenAward());

        }

        if(StringUtils.isEmpty(manage.getOtherIncreaseAmount()) || paramIsNull(manage.getOtherIncreaseAmount())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getOtherIncreaseAmount() != null ? manage.getOtherIncreaseAmount() : "").append(",");
            listStr.add("??????????????????:"+manage.getOtherIncreaseAmount());

        }

        if(StringUtils.isEmpty(manage.getInspectionFines()) || paramIsNull(manage.getInspectionFines())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getInspectionFines() != null ? manage.getInspectionFines() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getInspectionFines());

        }


        if(StringUtils.isEmpty(manage.getOthers()) || paramIsNull(manage.getOthers())){
            title = title.replaceAll("???????????????,","");
        }else {
            builder.append(manage.getOthers() != null ? manage.getOthers() : "");
            builder.append(",");
            listStr.add("???????????????:"+manage.getOthers());

        }


        if(StringUtils.isEmpty(manage.getTotalManageFees()) || paramIsNull(manage.getTotalManageFees())){
            title = title.replaceAll("???????????????,","");
        }else {
            builder.append(manage.getTotalManageFees() != null ? manage.getTotalManageFees() : "");
            builder.append(",");
            listStr.add("???????????????:"+manage.getTotalManageFees());

        }

        if(StringUtils.isEmpty(manage.getGeneralizationfficient()) || paramIsNull(manage.getGeneralizationfficient())){
            title = title.replaceAll("????????????,","");
        }else {
            builder.append(manage.getGeneralizationfficient() != null ? manage.getGeneralizationfficient() : "");
            builder.append(",");
            listStr.add("????????????:"+manage.getGeneralizationfficient());

        }

        if(StringUtils.isEmpty(manage.getCityManageRating()) || paramIsNull(manage.getCityManageRating())){
            title = title.replaceAll("??????????????????,","");
        }else {
            builder.append(manage.getCityManageRating() != null ? manage.getCityManageRating() : "");
            builder.append(",");
            listStr.add("??????????????????:"+manage.getCityManageRating());

        }


        mapData.put("listStr",listStr);
        mapData.put("headList",headList);
        mapData.put("length",listStr.size()<=headList.size()? listStr.size(): headList.size());//??????
        return mapData;

    }


    private List<String> footerList(List<String> footerList){
        footerList.add("\n");
        footerList.add("????????????????????????????????????");
        footerList.add("??????:???????????????????????????????????????????????????");
        footerList.add("??????????????????:91120106MA05LCJU5H");
        footerList.add("???????????????:??????????????????????????????????????????????????????3-208-05/06 022-27763608");
        footerList.add("??????????????????:??????????????????????????????????????????????????? 0302098009100096236");
        footerList.add("\n");
        footerList.add("????????????:");
        footerList.add("??????????????????????????????????????????37?????? ????????????????????? ????????????");
        footerList.add("?????????????????????:13810675102");
        footerList.add("");
        return footerList;
    }
    /**
     * ??????????????????????????????
     * @param param
     * @return
     */
    private String getTwoPoint(String param){
        if(StringUtils.isEmpty(param)){
            return "";
        }
        if(param.indexOf(".")>0){
            int index =param.indexOf(".");
            if(param.length()>index+3){
                param = param.substring(0,index+3)+"%";
            }
        }
        return param;
    }




    @RequestMapping("/applyCashDetail")
    @ResponseBody
    public AjaxResponse applyCashDetail(@Verify(param = "feeOrderNo",rule = "required") String feeOrderNo){
        SupplierFeeManage supplierFeeManage = supplierFeeService.queryByOrderNo(feeOrderNo);
        if(supplierFeeManage != null){
            return AjaxResponse.success(supplierFeeManage.getTotalManageFees());
        }
        return AjaxResponse.success(null);
    }


    /**
     * ????????????
     * @param feeOrderNo
     * @return
     */
    @RequestMapping("/applyCash")
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.MASTER )
    } )
    public AjaxResponse applyCash(@Verify(param = "feeOrderNo",rule = "required") String feeOrderNo){
        logger.info("??????????????????????????????????????????" + feeOrderNo);
        SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(null == ssoLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }
        
        Integer loginId = ssoLoginUser.getId();
         try {
            SupplierFeeRecord record = new SupplierFeeRecord();

            record.setOperate(SupplierFeeManageEnum.APPLYCATCH.getMsg());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setOperateId(loginId);
            record.setStatus(SupplierFeeManageEnum.APPLYCATCH.getCode());
            record.setRemark(null);
            record.setOperateUser(ssoLoginUser.getName());
            record.setSupplierAddress("???");
            record.setFeeOrderNo(feeOrderNo);

            int code =  recordService.insertFeeRecord(record);

            if(code > 0){
                SupplierFeeManage feeManage = supplierFeeService.queryByOrderNo(feeOrderNo);
                int feeCode = 0;
                if(feeManage != null && SupplierFeeManageEnum.APPLYCATCH.getCode() == feeManage.getStatus() ){
                    feeCode = supplierFeeService.updateStatus(feeOrderNo,SupplierFeeManageEnum.WAITINGTICKET.getCode());
                }else {
                    feeCode = supplierFeeService.updateStatus(feeOrderNo,null);
                }

                if(feeCode > 0 ){
                    logger.info("??????????????????");
                }
                logger.info("????????????success");
            }else {
                logger.info("????????????error");
            }
        } catch (Exception e) {
            logger.info("??????????????????????????????",e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return AjaxResponse.success(null);
    }


    /**??????????????? ???0.00 ??? 0.0000 ????????????????????????*/
    private boolean paramIsNull(String param){
        if(StringUtils.isEmpty(param)){
            return true;
        }

        if(param.equals("0.00") || param.equals("0.0000")){
            return false;
        }

        return false;
    }

    /**??????????????????*/
    private String rateToPertent(String param){

        try {
            if(StringUtils.isEmpty(param)){
                return null;
            }

            BigDecimal bigDecimal = new BigDecimal(param);

            BigDecimal newParam =  bigDecimal.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN);
            return  String.valueOf(newParam) + "%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }
}
