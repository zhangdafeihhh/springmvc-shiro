package com.zhuanche.serv.supplier;

import com.zhuanche.entity.mdbcarmanage.SupplierDistributor;
import mapper.mdbcarmanage.ex.SupplierDistributorExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/12/19 下午4:59
 * @Version 1.0
 */
@Service
public class SupplierDistributorServiceImpl implements SupplierDistributorService{

    @Autowired
    private SupplierDistributorExMapper exMapper;

    @Override
    public List<SupplierDistributor> distributorList(SupplierDistributor distributor) {
        return exMapper.distributorList(distributor);
    }

    @Override
    public int insert(SupplierDistributor distributor) {
        return exMapper.insertSelective(distributor);
    }

    @Override
    public int update(SupplierDistributor distributor) {
        return exMapper.updateByPrimaryKeySelective(distributor);
    }

    @Override
    public SupplierDistributor queryById(Integer id) {
        return exMapper.selectByPrimaryKey(id);
    }
}
