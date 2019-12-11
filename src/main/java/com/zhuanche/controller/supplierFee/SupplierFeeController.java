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
import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeRecord;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeRecordService;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.dateUtil.DateUtil;
import com.zhuanche.util.excel.SupplierFeeCsvUtils;
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
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

/**
 * @Author fanht
 * @Description 供应商加盟线上化
 * @Date 2019/9/11 下午7:00
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


    @RequestMapping("/listSupplierFee")
    //@RequiresPermissions(value = { "DriverInvite_look" } )
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    //@SensitiveDataOperationLog(primaryDataType="加盟司机数据",secondaryDataType="加盟司机个人基本信息",desc="加盟司机信息列表查询")
    //@RequestFunction(menu = DRIVER_JOIN_PROMOTE_LIST)
    public AjaxResponse listSupplierFee( @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                         @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                         Integer cityId, Integer supplierId,
                                         Integer status, Integer amountStatus, String settleStartDate,
                                         String settleEndDate,String paymentStartTime,String paymentEndTime){
        logger.info(MessageFormat.format("查询司机线上化入参:pageSize:%s,pageNum:{0},cityId:{1},supplierId:{2},status:{3}," +
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
            logger.error("查询司机参数化失败",e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        return AjaxResponse.success(pageDTO);
    }


    /**
     * 获取详情操作
     * @param feeOrderNo
     * @return
     */
    @RequestMapping("/supplierFeeDetail")
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public AjaxResponse supplierFeeDetail(@Verify(param = "feeOrderNo",rule = "required") String feeOrderNo){

        logger.info("获取加盟商线上化详情接口入参:" + feeOrderNo);

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
            logger.error("获取线上化异常",e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return AjaxResponse.success(detailDto);
    }


    /**
     * 管理费确认
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
        logger.info("供应商线上化确认操作，入参：" + status,remark,feeOrderNo);
        SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(null == ssoLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }
        Integer loginId = ssoLoginUser.getId();
        String userName = ssoLoginUser.getLoginName();
        try {
            SupplierFeeRecord record = new SupplierFeeRecord();

            record.setOperate(SupplierFeeManageEnum.getFeeStatus(status).getMsg());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setOperateId(loginId);
            record.setStatus(status);
            record.setRemark(remark);
            record.setOperateUser(userName);
            record.setSupplierAddress("空");
            record.setFeeOrderNo(feeOrderNo);

            int code =  recordService.insertFeeRecord(record);

            if(code > 0){
                int feeCode = 0;
                if(status == 0){
                    feeCode = supplierFeeService.updateStatusAndAmount(feeOrderNo,amountStatus,status);
                }else {
                    feeCode = supplierFeeService.updateStatusByFeeOrderNo(feeOrderNo,amountStatus);
                }
                if(feeCode > 0 ){
                    logger.info("更新状态成功");
                }
                logger.info("数据插入success");
            }else {
                logger.info("数据插入error");
            }
        } catch (Exception e) {
            logger.info("供应商线上化操作异常",e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return AjaxResponse.success(null);
    }


    /**
     * 导出对账单-PDF
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
        logger.info(MessageFormat.format("查询司机线上化入参:feeOrderNo:{0}",feeOrderNo));
        response.setContentType("application/pdf;charset=ISO8859-1");
        response.setHeader("Content-disposition", "attachment; filename="+"supplierFeePDF-1.pdf");
        SupplierFeeManage manage = supplierFeeService.queryByOrderNo(feeOrderNo);


        String[] titles = { "          供应商名称", "          结算开始日期", "          结算结束日期", "          总流水", "          流水金额", "          风控金额","          价外费","          取消费",
                "          流水合计金额", "          规模系数","          上月总流水","          流水增幅","          增长系数","          差评率","          当月佣金","          剔除佣金","          上月暂扣金额",
                "          合计","          合规司机奖励","          其他","          差评罚金","          扣款差评数量","          稽查罚金","          管理费合计"};
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A3); // Step 1—Create a Document.
            PdfWriter writer;
            writer = PdfWriter.getInstance(document, ba);
            document.open();


            document.setMargins(5, 5, 5, 5);
            //H 代表文字版式是横版，相应的 V 代表竖版
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);//STSongStd-Light 是字体，在jar 中以property为后缀
            //参数一：新建好的字体；参数二：字体大小，参数三：字体样式，多个样式用“|”分隔
            Font topfont = new Font(bfChinese,14,Font.BOLD);

            Paragraph blankRow1 = new Paragraph(18f, " ");
            blankRow1.setAlignment(Element.ALIGN_CENTER);


            PdfPTable table1 = new PdfPTable(titles.length); //创建一个表格,参数为一行有几栏
            int width1[] = {250,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300};//每栏的宽度
            table1.setKeepTogether(false);
            table1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table1.setWidthPercentage(100);//设置表格大小为可用空白区域的300%
            table1.setWidths(width1); //设置宽度

            //首行
            for(int i=0;i<titles.length;i++){
                PdfPCell cell1 = new PdfPCell(new Paragraph(titles[i],topfont));
                table1.addCell(cell1);
            }

            //每栏的值 注意行数要和上面的对应要不然导出的pdf显示不出来。如果要显示多行
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

            document.add(table1);//将表格加入到document中
            document.add(blankRow1);
            document.close();
            ba.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
            ba.close(); // 导出pdf注解

        } catch (Exception e) {
            logger.error("导出PDF异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return AjaxResponse.success(null);
    }

    /**
     * 导出对账单-EXCEL
     * @return
     */
    @RequestMapping("/exportSupplierFeeData")
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
    } )
    public AjaxResponse exportSupplierFeeData(HttpServletResponse response,HttpServletRequest request,
                                              String feeOrderNo){
        logger.info(MessageFormat.format("导出对账单:feeOrderNo:{0}",feeOrderNo));
        try {
            SupplierFeeManage manage = supplierFeeService.queryByOrderNo(feeOrderNo);
            List<String> headerList = new ArrayList<>();
            String titles = "序号,合作商,合作商全称,这列为空,结算开始日期,结算结束日期,总营业额,入围司机营业额,流水金额,风控金额,价外费,取消费,流水合计金额,规模系数,上月总流水,流水增幅,增长系数,司机贡献金合计," +
                    "合规奖励合计,佣金合计,差评率,活跃司机数量,剔除佣金,上月暂扣金额,是否补发,合计费用," +
                    "合规司机奖励,差评罚金,扣款差评数量,花园权益奖励,其它增加金额,稽查罚金,其它扣款项,管理费合计,推广系数，城市经理评级";


            String fileName = "对账单信息" + DateUtil.dateFormat(new Date(), DateUtil.intTimestampPattern)+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
            try {
                if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
                    fileName = URLEncoder.encode(fileName, "UTF-8");
                } else {  //其他浏览器
                    fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
                }
            } catch (UnsupportedEncodingException e) {
                logger.info("导出对账单异常" +e);
            }

            boolean isLast = true;
            boolean isFirst = true;

            SupplierFeeCsvUtils entity = new SupplierFeeCsvUtils();
            List<String> listStr = new ArrayList<>();
            Map<String,Object> map = getData(manage,listStr,titles);
            listStr = (List<String>) map.get("listStr");
            //headerList= (List<String>) map.get("headList");
           /* String newTitle = (String) map.get("title");*/
            int length = (int) map.get("length");
            logger.info("headerList:" + JSONObject.toJSONString(headerList));

            List<String> footerList = new ArrayList<>();
            footerList = this.footerList(footerList);
            try {

                entity.exportCsvV2(response,listStr,headerList,fileName,isFirst,isLast,footerList,length);
            } catch (IOException e) {
                logger.error("导出异常",e);
            }
        } catch (Exception e) {
            logger.error("导出对账单异常",e);

        }
        return AjaxResponse.success(null);
    }


    private Map<String,Object> getData(SupplierFeeManage manage,List<String> listStr,String title){

        Map<String,Object> mapData = Maps.newHashMap();

        List<String> headList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isEmpty(manage.getSerialNumber())){
            title = title.replaceAll("序号,","");
        }else {
            builder.append(manage.getSerialNumber()).append(",");
            listStr.add("序号:"+manage.getSerialNumber());
        }


        if(StringUtils.isEmpty(manage.getSupplierName() )){
            title = title.replaceAll("合作商,","");

        }else {
            builder.append(manage.getSupplierName());
            builder.append(",");
            listStr.add("合作商:"+manage.getSupplierName());
        }


        if(StringUtils.isEmpty(manage.getSupplierFullName())){
            title = title.replaceAll("合作商全称,","");
            title = title.replaceAll("这列为空,","");
        }else {
            title = title.replaceAll("这列为空,",",");
            builder.append(manage.getSupplierFullName() != null ? manage.getSupplierFullName() : "").append(",");
            builder.append("").append(",");
            listStr.add("合作商全称:"+manage.getSupplierFullName());

        }


        if(manage.getSettleStartDate() == null){
            title = title.replaceAll("结算开始日期,","");
        }else {
            builder.append(manage.getSettleStartDate() != null ? DateUtils.formatDate(manage.getSettleStartDate(),DateUtils.date_format) : "");
            builder.append(",");
            String beginDate = manage.getSettleStartDate() != null ? DateUtils.formatDate(manage.getSettleStartDate(),DateUtils.date_format) : "";
            listStr.add("结算开始日期:"+beginDate);

        }

        if(manage.getSettleEndDate() == null){
            title = title.replaceAll("结算结束日期,","");
        }else {
            builder.append(manage.getSettleEndDate() != null ? DateUtils.formatDate(manage.getSettleEndDate(),DateUtils.date_format) : "");
            builder.append(",");
            String endDate = manage.getSettleEndDate() != null ? DateUtils.formatDate(manage.getSettleEndDate(),DateUtils.date_format) : "";
            listStr.add("结算结束日期:"+ endDate);

        }


        if(StringUtils.isEmpty(manage.getTotalFlow())){
            title = title.replaceAll("总营业额,","");
        }else {
            builder.append(manage.getTotalFlow() != null ? manage.getTotalFlow() : "");
            builder.append(",");
            listStr.add("总营业额:"+manage.getTotalFlow());

        }




        if(StringUtils.isEmpty(manage.getTurnoverDrivers())){
            title = title.replaceAll("入围司机营业额,","");

        }else {
            builder.append(manage.getTurnoverDrivers() != null ? manage.getTurnoverDrivers() : "").append(",");
            listStr.add("入围司机营业额:"+manage.getTurnoverDrivers());

        }
        if(StringUtils.isEmpty(manage.getFlowAmount())){
            title = title.replaceAll("流水金额,","");
        }else {
            builder.append(manage.getFlowAmount() != null ? manage.getFlowAmount() : "");
            builder.append(",");
            listStr.add("流水金额:"+manage.getFlowAmount());

        }


        if(StringUtils.isEmpty(manage.getWindControlAmount())){
            title = title.replaceAll("风控金额,","");
        }else {
            builder.append(manage.getWindControlAmount() != null ? manage.getWindControlAmount() : "");
            builder.append(",");
            listStr.add("风控金额:"+manage.getWindControlAmount());

        }



        if(StringUtils.isEmpty(manage.getExtraCharge())){
            title = title.replaceAll("价外费,","");
        }else {
            builder.append(manage.getExtraCharge() != null ? manage.getExtraCharge() : "");
            builder.append(",");
            listStr.add("价外费:"+manage.getExtraCharge());

        }


        if(StringUtils.isEmpty(manage.getCancelCharge())){
            title = title.replaceAll("取消费,","");
        }else {
            builder.append(manage.getCancelCharge() != null ? manage.getCancelCharge() : "");
            builder.append(",");
            listStr.add("取消费:"+manage.getCancelCharge());

        }


        if(StringUtils.isEmpty(manage.getTotalAmountWater())){
            title = title.replaceAll("流水合计金额,","");
        }else {
            builder.append(manage.getTotalAmountWater() != null ? manage.getTotalAmountWater() : "");
            builder.append(",");
            listStr.add("流水合计金额:"+manage.getTotalAmountWater());

        }


        if(StringUtils.isEmpty(manage.getScaleEfficient())){
            title = title.replaceAll("规模系数,","");
        }else {
            builder.append(manage.getScaleEfficient() != null ? this.getTwoPoint(manage.getScaleEfficient()): "");
            builder.append(",");
            listStr.add("规模系数:"+manage.getScaleEfficient());

        }

        if(StringUtils.isEmpty(manage.getTotalFlowLastMonth())){
            title = title.replaceAll("上月总流水,","");
        }else {
            builder.append(manage.getTotalFlowLastMonth() != null ? manage.getTotalFlowLastMonth() : "");
            builder.append(",");
            listStr.add("上月总流水:"+manage.getTotalFlowLastMonth());

        }


        if(StringUtils.isEmpty(manage.getFlowIncrease())){
            title = title.replaceAll("流水增幅,","");
        }else {
            builder.append(manage.getFlowIncrease() != null ? this.getTwoPoint(manage.getFlowIncrease()) : "");
            builder.append(",");
            listStr.add("流水增幅:"+manage.getFlowIncrease());

        }


        if(StringUtils.isEmpty(manage.getGrowthFactor())){
            title = title.replaceAll("增长系数,","");
        }else {
            builder.append(manage.getGrowthFactor() != null ? this.getTwoPoint(manage.getGrowthFactor()) : "");
            builder.append(",");
            listStr.add("增长系数:"+manage.getGrowthFactor());

        }


        if(StringUtils.isEmpty(manage.getTotalDriverContribution())){
            title = title.replaceAll("司机贡献金合计,","");
        }else {
            builder.append(manage.getTotalDriverContribution() != null ? manage.getTotalDriverContribution() : "").append(",");
            listStr.add("司机贡献金合计:"+manage.getTotalDriverContribution());

        }

        if(StringUtils.isEmpty(manage.getTotalComplianceAwards())){
            title = title.replaceAll("合规奖励合计,","");
        }else {
            builder.append(manage.getTotalComplianceAwards() != null ? manage.getTotalComplianceAwards() : "").append(",");
            listStr.add("合规奖励合计:"+manage.getTotalComplianceAwards());

        }

        if(StringUtils.isEmpty(manage.getMonthCommission())){
            title = title.replaceAll("佣金合计,","");
        }else {
            builder.append(manage.getMonthCommission() != null ? manage.getMonthCommission() : "");
            builder.append(",");
            listStr.add("佣金合计:"+manage.getMonthCommission());

        }


        if(StringUtils.isEmpty(manage.getBadRatings())){
            title = title.replaceAll("差评率,","");
        }else {
            builder.append(manage.getBadRatings() != null ? this.getTwoPoint(manage.getBadRatings()) : "");
            builder.append(",");
            listStr.add("差评率:"+manage.getBadRatings());

        }


        if(manage.getNumberOfActiveDrivers() == null){
            title = title.replaceAll("活跃司机数量,","");
        }else {
            builder.append(manage.getNumberOfActiveDrivers() != null ? manage.getNumberOfActiveDrivers() : "").append(",");
            listStr.add("活跃司机数量:"+manage.getNumberOfActiveDrivers());

        }

        if(StringUtils.isEmpty(manage.getExcludeCommission())){
            title = title.replaceAll("剔除佣金,","");
        }else {
            builder.append(manage.getExcludeCommission() != null ? manage.getExcludeCommission() : "");
            builder.append(",");
            listStr.add("剔除佣金:"+manage.getExcludeCommission());

        }


        if(StringUtils.isEmpty(manage.getDeductionAmountLastMonth() )){
            title = title.replaceAll("上月暂扣金额,","");
        }else {
            builder.append(manage.getDeductionAmountLastMonth() != null ? manage.getDeductionAmountLastMonth() : "");
            builder.append(",");
            listStr.add("上月暂扣金额:"+manage.getDeductionAmountLastMonth());

        }


        if(StringUtils.isEmpty(manage.getIsReissue())){
            title = title.replaceAll("是否补发,","");
        }else {
            builder.append(manage.getIsReissue() != null ? manage.getIsReissue() : "").append(",");
            listStr.add("是否补发:"+manage.getIsReissue());
        }

        if(StringUtils.isEmpty(manage.getTotal() )){
            title = title.replaceAll("合计费用,","");
        }else {
            builder.append(manage.getTotal() != null ? manage.getTotal() : "");
            builder.append(",");
            listStr.add("合计费用:"+manage.getTotal());
        }


        if(StringUtils.isEmpty(manage.getComplianceDriverAward())){
            title = title.replaceAll("合规司机奖励,","");
        }else {
            builder.append(manage.getComplianceDriverAward() != null ? manage.getComplianceDriverAward() : "");
            builder.append(",");
            listStr.add("合规司机奖励:"+manage.getComplianceDriverAward());

        }


        if(StringUtils.isEmpty(manage.getBadRatingsAward())){
            title = title.replaceAll("差评罚金,","");
        }else {
            builder.append(manage.getBadRatingsAward() != null ? manage.getBadRatingsAward() : "");
            builder.append(",");
            listStr.add("差评罚金:"+manage.getBadRatingsAward());

        }


        if(StringUtils.isEmpty(manage.getAmountAssessmentSum())){
            title = title.replaceAll("扣款差评数量,","");
        }else {
            builder.append(manage.getAmountAssessmentSum() != null ? manage.getAmountAssessmentSum() : "");
            builder.append(",");
            listStr.add("扣款差评数量:"+manage.getAmountAssessmentSum());

        }


        if(StringUtils.isEmpty(manage.getGardenAward())){
            title = title.replaceAll("花园权益奖励,","");
        }else {
            builder.append(manage.getGardenAward() != null ? manage.getGardenAward() : "").append(",");
            listStr.add("花园权益奖励:"+manage.getGardenAward());

        }

        if(StringUtils.isEmpty(manage.getOtherIncreaseAmount())){
            title = title.replaceAll("其它增加金额,","");
        }else {
            builder.append(manage.getOtherIncreaseAmount() != null ? manage.getOtherIncreaseAmount() : "").append(",");
            listStr.add("其它增加金额:"+manage.getOtherIncreaseAmount());

        }

        if(StringUtils.isEmpty(manage.getInspectionFines())){
            title = title.replaceAll("稽查罚金,","");
        }else {
            builder.append(manage.getInspectionFines() != null ? manage.getInspectionFines() : "");
            builder.append(",");
            listStr.add("稽查罚金:"+manage.getInspectionFines());

        }


        if(StringUtils.isEmpty(manage.getOthers())){
            title = title.replaceAll("其它扣款项,","");
        }else {
            builder.append(manage.getOthers() != null ? manage.getOthers() : "");
            builder.append(",");
            listStr.add("其它扣款项:"+manage.getOthers());

        }


        if(StringUtils.isEmpty(manage.getTotalManageFees())){
            title = title.replaceAll("管理费合计,","");
        }else {
            builder.append(manage.getTotalManageFees() != null ? manage.getTotalManageFees() : "");
            builder.append(",");
            listStr.add("管理费合计:"+manage.getTotalManageFees());

        }

        if(StringUtils.isEmpty(manage.getGeneralizationfficient())){
            title = title.replaceAll("推广系数,","");
        }else {
            builder.append(manage.getGeneralizationfficient() != null ? manage.getGeneralizationfficient() : "");
            builder.append(",");
            listStr.add("推广系数:"+manage.getGeneralizationfficient());

        }

        if(StringUtils.isEmpty(manage.getCityManageRating())){
            title = title.replaceAll("城市经理评级,","");
        }else {
            builder.append(manage.getCityManageRating() != null ? manage.getCityManageRating() : "");
            builder.append(",");
            listStr.add("城市经理评级:"+manage.getCityManageRating());

        }



        //实现特定的业务需求 每隔7行换行
        /*String value  = builder.toString().substring(0,builder.length()-1);
        String[] valueStr = value.split(",");
        String str = "";
        for(int k = 0;k<valueStr.length;k++){
            int zhengshu = k%7;
            while (zhengshu==0 && StringUtils.isNotEmpty(str)){
                listStr.add(str.substring(0,str.length()-1));
                str = "";
            }
            str += valueStr[k]+",";
            if(k==valueStr.length-1 && StringUtils.isNotEmpty(str)){
                listStr.add(str.substring(0,str.length()-1));
            }
        }



        //将合计费用修改为合计  替换会出现替换多个情况
        String addTitle = title.replaceAll("合计费用","合计");
        //addTitle = addTitle.replaceAll("这列为空","");
        String[] titleStr = addTitle.split(",");

        String  headStr ="";
        for(int m = 0;m<titleStr.length;m++){
            int zhengchu = m%7;
            while (zhengchu == 0 && StringUtils.isNotEmpty(headStr)){
                headList.add(headStr.substring(0,headStr.length()-1));
                headStr = "";
            }
            headStr += titleStr[m]+",";

            if(m==valueStr.length-1 && StringUtils.isNotEmpty(headStr)){
                headList.add(headStr.substring(0,headStr.length()-1));
            }
        }*/


        mapData.put("listStr",listStr);
        mapData.put("headList",headList);
        mapData.put("length",listStr.size()<=headList.size()? listStr.size(): headList.size());//容错
        return mapData;

    }


    private List<String> footerList(List<String> footerList){
        footerList.add("\n");
        footerList.add("增值税普通发票开票信息：");
        footerList.add("名称:首约科技（北京）有限公司天津分公司");
        footerList.add("纳税人识别号:91120106MA05LCJU5H");
        footerList.add("地址、电话:天津市滨海新区临港经济区临港怡湾广场3-208-05/06 022-27763608");
        footerList.add("开户行及账号:中国工商银行空港经济区空港第一支行 0302098009100096236");
        footerList.add("\n");
        footerList.add("邮寄地址:");
        footerList.add("北京市朝阳区枣营路甲3号 首汽大厦3层");
        footerList.add("合作商运营部收:13810675102");
        footerList.add("");
        return footerList;
    }
    /**
     * 截取小数点后两位小数
     * @param param
     * @return
     */
    private String getTwoPoint(String param){
        if(StringUtils.isEmpty(param)){
            return "";
        }
        if(param.indexOf(".")>0){
            int index =param.indexOf(".");
            param = param.substring(0,index+3)+"%";
        }
        return param;
    }
}
