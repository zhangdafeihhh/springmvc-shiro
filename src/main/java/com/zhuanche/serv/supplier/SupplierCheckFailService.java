package com.zhuanche.serv.supplier;

import com.zhuanche.entity.driver.SupplierCheckFail;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/12/4 下午3:44
 * @Version 1.0
 */
public interface SupplierCheckFailService {

    List<SupplierCheckFail> failList(Integer supplierId);

    int insert(SupplierCheckFail checkFail);
}
