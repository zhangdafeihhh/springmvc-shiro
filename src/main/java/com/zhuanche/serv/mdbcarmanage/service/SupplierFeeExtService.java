package com.zhuanche.serv.mdbcarmanage.service;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeExt;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/11/30 下午1:37
 * @Version 1.0
 */
public interface SupplierFeeExtService {

    /**
     * 查询有哪些字段需要导出
     * @param supplierFeeId
     * @return
     */
    List<SupplierFeeExt> supplierFeeExtList(Integer supplierFeeId);
}
