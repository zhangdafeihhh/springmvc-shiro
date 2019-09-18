package com.zhuanche.controller.supplierFee;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
/*import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;*/
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.securityLog.SensitiveDataOperationLog;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
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
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_JOIN_PROMOTE_LIST;

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
        logger.info(MessageFormat.format("查询司机线上化入参:pageSize:%s,pageNum:%s,cityId:%s,supplierId:%s,status:%s," +
                "amountStatus:%s,settleStartDate:%s,settleEndDate:%s,paymentStartTime:%s,paymentEndTime:%s",pageSize,
                pageNum,cityId,supplierId,status,amountStatus,settleStartDate,settleEndDate,paymentStartTime,paymentEndTime));

        SupplierFeeManageDto feeManageDto = new SupplierFeeManageDto();
        feeManageDto.setCityId(cityId);
        feeManageDto.setSettleStartDate(settleStartDate);
        feeManageDto.setSettleEndDate(settleEndDate);
        feeManageDto.setAmountStatus(amountStatus);
        feeManageDto.setStatus(status);
        Page page = PageHelper.startPage(pageNum,pageSize);
        List<SupplierFeeManage> feeManageList = supplierFeeService.queryListData(feeManageDto);
        PageHelper.clearPage();
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, page.getTotal(), feeManageList);
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

        logger.info("获取详情接口入参:" + feeOrderNo);

        SupplierFeeManage supplierFeeManage = supplierFeeService.queryByOrderNo(feeOrderNo);
        SupplierFeeManageDetailDto detailDto = new SupplierFeeManageDetailDto();
        if(supplierFeeManage!=null){
            BeanUtils.copyProperties(supplierFeeManage,detailDto);
            List<SupplierFeeRecord> recordList = recordService.listRecord(feeOrderNo);
            if(CollectionUtils.isNotEmpty(recordList)){
                detailDto.setSupplierFeeRecordList(recordList);
            }
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
    public AjaxResponse supplierFeeOpe(Integer status,String remark,String feeOrderNo){
        SSOLoginUser ssoLoginUser = WebSessionUtil.getCurrentLoginUser();
        if(null == ssoLoginUser){
            return AjaxResponse.fail(RestErrorCode.HTTP_INVALID_SESSION);
        }
        Integer loginId = ssoLoginUser.getId();
        String userName = ssoLoginUser.getLoginName();


        SupplierFeeRecord record = new SupplierFeeRecord();
        if(status == 1){
            record.setOperate(SupplierFeeStatusEnum.NORMAL.getMsg());
        }else {
            record.setOperate(SupplierFeeStatusEnum.UNNORMAL.getMsg());
        }
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
           logger.info("数据插入success");
       }else {
           logger.info("数据插入error");
       }

        return AjaxResponse.success(null);
    }


    /**
     * 导出对账单
     * @return
     */
    @RequestMapping("/exportSupplierFeeData")
    @ResponseBody
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE)
    } )
    public AjaxResponse exportSupplierFeeData(HttpServletResponse response,
                                              ServletOutputStream outputStream,
                                              String feeOrderNo){
        logger.info(MessageFormat.format("查询司机线上化入参:feeOrderNo:{0}",feeOrderNo));
        response.setContentType("application/pdf;charset=ISO8859-1");
        response.setHeader("Content-disposition", "attachment; filename="+"supplierFeePDF-1.pdf");
        SupplierFeeManage manage = supplierFeeService.queryByOrderNo(feeOrderNo);


        String[] titles = { "供应商名称", "结算开始日期", "结算结束日期", "总流水", "流水金额", "风控金额","价外费","取消费","流水合计金额",
        "规模系数","上月总流水","流水增幅","增长系数","差评率","当月佣金","剔除佣金","上月暂扣金额","合计","合规司机奖励","其他","差评罚金","扣款差评数量","稽查罚金","管理费合计"};
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
            int width1[] = {300,250,250,50,50,50,55,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50};//每栏的宽度
            table1.setWidths(width1); //设置宽度

            //首行
            for(int i=0;i<titles.length;i++){
                PdfPCell cell1 = new PdfPCell(new Paragraph(titles[i],topfont));
                table1.addCell(cell1);
            }

            //每栏的值 注意行数要和上面的对应要不然导出的pdf显示不出来。如果要显示多行
            PdfPCell cell1 = new PdfPCell(new Paragraph(manage.getSupplierName(),topfont));
            table1.addCell(cell1);
            PdfPCell cell2= new PdfPCell(new Paragraph(DateUtils.formatDate(manage.getSettleStartDate()),topfont));
            table1.addCell(cell2);
            PdfPCell cell3= new PdfPCell(new Paragraph(DateUtils.formatDate(manage.getSettleEndDate()),topfont));
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
           // document.add(table2);
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

        //注意：使用下面的方法 由于模板有问题，导致一直找不到form表单的内容
        /*  // 模板路径
        String templatePath =  "/Users/fan/workspace/mp-manage/src/main/webapp/upload/supplierFeePDF-1.pdf";
        // 生成的新文件路径
       String newPDFPath =  "/Users/fan/workspace/mp-manage/src/main/webapp/upload/supplierFeePDF-2.pdf";
      PdfReader reader;
        FileOutputStream out;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            out = new FileOutputStream(newPDFPath);// 输出流
            reader = new PdfReader(templatePath);// 读取pdf模板
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();
            *//*String[] str = {manage.getSupplierName(), DateUtils.formatDate(manage.getSettleStartDate()),DateUtils.formatDate(manage.getSettleEndDate()),
            manage.getFlowAmount()};*//*
            String[] str = { "123456789", "TOP__ONE", "男", "1991-01-01", "130222111133338888", "河北省保定市" };
            java.util.Iterator<String> it = form.getFields().keySet().iterator();

            *//*for(int i = 0;i<feeManageList.size();i++){
                SupplierFeeManage feeManage = feeManageList.get(i);

                form.setField(null,feeManage.getSupplierName());
            }*//*

            int i = 0;
            while (it.hasNext()) {
                String name = it.next().toString();
                System.out.println(name);
                form.setField(name, str[i++]);
            }
            stamper.setFormFlattening(true);// 如果为false那么生成的PDF文件还能编辑，一定要设为true
            stamper.close();
            Document doc = new Document();
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();
        } catch (IOException e) {
            System.out.println(1);
        } catch (DocumentException e) {
            System.out.println(2);
        }
*/
        return AjaxResponse.success(null);
    }

}
