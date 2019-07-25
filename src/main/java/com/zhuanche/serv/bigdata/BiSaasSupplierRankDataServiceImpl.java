package com.zhuanche.serv.bigdata;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.bigdata.BiSaasSupplierRankData;
import mapper.bigdata.ex.BiSaasSupplierRankDataExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BiSaasSupplierRankDataServiceImpl  implements  BiSaasSupplierRankDataService{

    @Autowired
    public BiSaasSupplierRankDataExMapper biSaasSupplierRankDataExMapper;

    private static Logger logger = LoggerFactory.getLogger(BiSaasSupplierRankDataServiceImpl.class);

    @Override
    public PageInfo<BiSaasSupplierRankData> findPage(int pageNo, int pageSize, Date createDate) {
        PageHelper.startPage(pageNo, pageSize, true);
        PageInfo<BiSaasSupplierRankData> pageInfo = null;
        try{
            //List<BiSaasSupplierRankData> list =  biSaasSupplierRankDataExMapper.findByCreateTime(createDate);
//            pageInfo = new PageInfo<>(list);

        }finally {
            PageHelper.clearPage();
        }
        return pageInfo;
    }

    @Override
    public BiSaasSupplierRankData findByParam(Integer supplierId, Date settleStartTime, Date settleEndTime) {
        return biSaasSupplierRankDataExMapper.findByParam(supplierId,   settleStartTime,   settleEndTime);
    }
}
