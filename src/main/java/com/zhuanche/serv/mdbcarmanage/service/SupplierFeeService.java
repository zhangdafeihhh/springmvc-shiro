package com.zhuanche.serv.mdbcarmanage.service;

import com.zhuanche.dto.mdbcarmanage.SupplierFeeManageDto;
import com.zhuanche.entity.mdbcarmanage.SupplierFeeManage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/9/11 下午8:04
 * @Version 1.0
 */
public interface SupplierFeeService {

    List<SupplierFeeManage> queryListData(SupplierFeeManageDto manageDto);

    SupplierFeeManage queryByOrderNo(@Param("feeOrderNO") String feeOrderNo);

    int updateStatusByFeeOrderNo(String feeOrderNo,int amountStatus);
}
