package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.mdbcarmanage.CarDriverFile;
import com.zhuanche.entity.rentcar.CarBizSupplierVo;
import com.zhuanche.serv.mdbcarmanage.service.CarDriverFileService;
import mapper.mdbcarmanage.CarDriverFileMapper;
import mapper.mdbcarmanage.ex.CarDriverFileExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarDriverFileServiceImpl implements CarDriverFileService {

    private Logger LOGGER = LoggerFactory.getLogger(CarDriverFileServiceImpl.class);

    @Autowired
    private CarDriverFileMapper carDriverFileMapper;

    @Autowired
    private CarDriverFileExtMapper carDriverFileExtMapper;

    @Override
    public PageInfo<CarDriverFile> find4Page(CarDriverFile queryParam, int pageNo, int pageSize) {
        LOGGER.info("查询司机头像，参数为：" + JSON.toJSONString(queryParam));
        PageHelper.startPage(pageNo, pageSize, true);
        try{
            List<CarDriverFile> list = carDriverFileExtMapper.findByParams(queryParam);
            PageInfo<CarDriverFile> pageInfo = new PageInfo<>(list);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }

    }
}
