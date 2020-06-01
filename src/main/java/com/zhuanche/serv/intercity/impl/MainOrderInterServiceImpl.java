package com.zhuanche.serv.intercity.impl;

import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.serv.intercity.MainOrderInterService;
import mapper.mdbcarmanage.ex.MainOrderInterCityExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/24 下午6:51
 * @Version 1.0
 */
@Service
public class MainOrderInterServiceImpl implements MainOrderInterService {

    @Autowired
    private MainOrderInterCityExMapper exMapper;

    @Override
    public int updateMainTime(String mainOrderNo, String mainTime) {
        return exMapper.updateMainTime(mainOrderNo,mainTime);
    }

    @Override
    public int addMainOrderNo(MainOrderInterCity record) {
        return exMapper.addMainOrderNo(record);
    }

    @Override
    public MainOrderInterCity queryMainOrder(String mainOrderNo) {
        return exMapper.queryMainOrder(mainOrderNo);
    }

    @Override
    public int updateMainOrderState(String mainOrderNo,Integer status,String phone) {
        return exMapper.updateMainOrderState(mainOrderNo,status,phone);
    }
}
