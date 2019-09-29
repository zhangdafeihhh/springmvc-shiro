package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.zhuanche.dto.mdbcarmanage.SupplierFeeManageDto;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;
import com.zhuanche.serv.mdbcarmanage.service.SupplierFeeService;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
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

                if(manage.getSettleStartDate() != null){
                    String start = DateUtils.formatDate(manage.getSettleStartDate(),"yyyy-MM-dd") ;
                    manage.setSettleStartDateStr(start);
                }
                if(manage.getSettleEndDate() != null){
                    String end = DateUtils.formatDate(manage.getSettleEndDate(),"yyyy-MM-dd");
                    manage.setSettleEndDateStr(end);
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
