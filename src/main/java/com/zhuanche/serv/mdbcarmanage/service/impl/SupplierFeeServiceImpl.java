package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.zhuanche.dto.mdbcarmanage.SupplierFeeManageDto;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;
import com.zhuanche.serv.mdbcarmanage.SupplierFeeService;
import mapper.mdbcarmanage.ex.SupplierFeeManageExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/9/11 下午8:05
 * @Version 1.0
 */
@Service
public class SupplierFeeServiceImpl implements SupplierFeeService{

    @Autowired
    private SupplierFeeManageExMapper exMapper;

    @Override
    public List<SupplierFeeManage> queryListData(SupplierFeeManageDto manageDto) {
        return exMapper.feeManageList(manageDto);
    }

    @Override
    public SupplierFeeManage queryByOrderNo(String feeOrderNo) {
        return exMapper.feeOrderDetail(feeOrderNo);
    }


}
