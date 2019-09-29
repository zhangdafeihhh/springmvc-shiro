package com.zhuanche.serv.mdbcarmanage.service;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeRecord;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/9/16 上午10:05
 * @Version 1.0
 */
public interface SupplierFeeRecordService {

    /**
     * 供应商确认
     * @param record
     * @return
     */
    int insertFeeRecord(SupplierFeeRecord record);

    List<SupplierFeeRecord> listRecord(String feeOrderNo);

}
