package com.zhuanche.controller.supplierFee;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
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
@RequestMapping("/supplier/supplierFee")
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
        return AjaxResponse.success(feeManageList);
    }



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
    public AjaxResponse manageFeeOpe(Integer status,String remark,String feeOrderNo){
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
    public AjaxResponse exportSupplierFeeData(Integer cityId, Integer supplierId,
                                              Integer status, Integer amountStatus, String settleStartDate,
                                              String settleEndDate,String paymentStartTime,String paymentEndTime){
        logger.info(MessageFormat.format("查询司机线上化入参:cityId:%s,supplierId:%s,status:%s," +
                        "amountStatus:%s,settleStartDate:%s,settleEndDate:%s,paymentStartTime:%s,paymentEndTime:%s",cityId,supplierId,status,amountStatus,settleStartDate,settleEndDate,paymentStartTime,paymentEndTime));

        SupplierFeeManageDto feeManageDto = new SupplierFeeManageDto();
        feeManageDto.setCityId(cityId);
        feeManageDto.setSettleStartDate(settleStartDate);
        feeManageDto.setSettleEndDate(settleEndDate);
        feeManageDto.setAmountStatus(amountStatus);
        feeManageDto.setStatus(status);
        List<SupplierFeeManage> feeManageList = supplierFeeService.queryListData(feeManageDto);


        return null;
    }

}
