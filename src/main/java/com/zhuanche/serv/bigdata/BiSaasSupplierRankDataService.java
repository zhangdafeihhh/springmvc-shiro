package com.zhuanche.serv.bigdata;

import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.bigdata.BiSaasSupplierRankData;
import com.zhuanche.entity.driver.SupplierLevel;

import java.util.Date;

public interface BiSaasSupplierRankDataService {

    public PageInfo<BiSaasSupplierRankData> findPage(int pageNo,int pageSize,  String month);

    BiSaasSupplierRankData findByParam(Integer supplierId,Date settleStartTime,Date settleEndTime);
}
