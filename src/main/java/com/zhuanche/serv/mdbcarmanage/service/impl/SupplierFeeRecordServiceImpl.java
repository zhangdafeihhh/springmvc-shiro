package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeRecord;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeRecordService;
import mapper.mdbcarmanage.SupplierFeeRecordMapper;
import mapper.mdbcarmanage.ex.SupplierFeeRecordExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/9/16 上午10:07
 * @Version 1.0
 */
@Service
public class SupplierFeeRecordServiceImpl implements SupplierFeeRecordService{

    @Autowired
    private SupplierFeeRecordExMapper exMapper;

    @Autowired
    private SupplierFeeRecordMapper mapper;

    @Override
    public int insertFeeRecord(SupplierFeeRecord record) {
        return mapper.insertSelective(record);
    }

    @Override
    public List<SupplierFeeRecord> listRecord(String feeOrderNo) {
        return exMapper.listRecord(feeOrderNo);
    }
}
