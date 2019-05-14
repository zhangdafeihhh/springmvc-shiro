package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import com.zhuanche.serv.mdbcarmanage.service.CarBizSaasVersionService;
import mapper.mdbcarmanage.ex.CarBizSaasVersionDetailExMapper;
import mapper.mdbcarmanage.ex.CarBizSaasVersionExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: nysspring@163.com
 * @Description: saas系统版本更新记录服务，包含更新记录及附件信息的基础服务及部分业务服务
 * @Date: 18:59 2019/5/13
 */
@Service
public class CarBizSaasVersionServiceImpl implements CarBizSaasVersionService{

    @Autowired
    private CarBizSaasVersionExMapper carBizSaasVersionExMapper;

    @Autowired
    private CarBizSaasVersionDetailExMapper carBizSaasVersionDetailExMapper;


    @Override
    public int saveCarBizSaasVersion(CarBizSaasVersion record) {
        return carBizSaasVersionExMapper.insertSelective(record);
    }
}
