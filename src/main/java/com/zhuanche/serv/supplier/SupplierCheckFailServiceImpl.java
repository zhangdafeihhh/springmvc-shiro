package com.zhuanche.serv.supplier;

import com.zhuanche.entity.driver.SupplierCheckFail;
import mapper.driver.ex.SupplierCheckFailExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/12/4 下午3:46
 * @Version 1.0
 */
@Service
public class SupplierCheckFailServiceImpl implements SupplierCheckFailService{

    @Autowired
    private SupplierCheckFailExMapper exMapper;

    @Override
    public List<SupplierCheckFail> failList(Integer supplierId) {
        return exMapper.failList(supplierId);
    }

    @Override
    public int insert(SupplierCheckFail checkFail) {
        return exMapper.insert(checkFail);
    }
}
