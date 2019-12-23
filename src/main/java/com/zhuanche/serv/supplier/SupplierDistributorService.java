package com.zhuanche.serv.supplier;

import com.zhuanche.entity.mdbcarmanage.SupplierDistributor;

import java.util.List;
import java.util.Set;

/**
 * @Author fanht
 * @Description
 * @Date 2019/12/19 下午4:59
 * @Version 1.0
 */
public interface SupplierDistributorService {

    List<SupplierDistributor> distributorList(SupplierDistributor distributor);

    int insert(SupplierDistributor distributor);

    int update(SupplierDistributor distributor);

    SupplierDistributor queryById(Integer id);

    List<SupplierDistributor> distributorList(Set<Integer> ids);

}
