package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.zhuanche.dto.mdbcarmanage.SupplierFeeManageDto;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeService;
import mapper.mdbcarmanage.ex.SupplierFeeManageExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/9/11 下午8:05
 * @Version 1.0
 */
@Service
public class SupplierFeeServiceImpl implements SupplierFeeService {

    @Autowired
    private SupplierFeeManageExMapper exMapper;

    @Override
    public List<SupplierFeeManage> queryListData(SupplierFeeManageDto manageDto) {
        List<SupplierFeeManage> manageList = exMapper.feeManageList(manageDto);
        if(!CollectionUtils.isEmpty(manageList)){
            for(SupplierFeeManage manage : manageList){
                if(manage.getPaymentTime().getTime()<0){
                    manage.setPaymentTime(null);
                }

                if(manage.getAmountStatusTime().getTime() <0){
                    manage.setAmountStatusTime(null);
                }
            }
        }
        return manageList;
    }

    @Override
    public SupplierFeeManage queryByOrderNo(String feeOrderNo) {
        return exMapper.feeOrderDetail(feeOrderNo);
    }

    @Override
    public int updateStatusByFeeOrderNo(String feeOrderNo,int amountStatus) {
        return exMapper.updateStatusByFeeOrderNo(feeOrderNo,amountStatus);
    }


}
