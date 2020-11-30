package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeExt;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeExtService;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeService;
import mapper.mdbcarmanage.ex.SupplierFeeExtExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/11/30 下午1:39
 * @Version 1.0
 */
@Service
public class SupplierFeeExtServiceImpl implements SupplierFeeExtService{

    @Autowired
    private SupplierFeeExtExMapper extExMapper;

    @Override
    public List<SupplierFeeExt> supplierFeeExtList(Integer supplierFeeId) {
        return extExMapper.queryBySupplierFeeId(supplierFeeId);
    }
}
