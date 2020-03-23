package com.zhuanche.serv.bigdata;

import lombok.extern.slf4j.Slf4j;
import mapper.bigdata.BigDataCarBizDriverInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
@Slf4j
public class BigDataDriverInfoService {
    @Autowired
    private BigDataCarBizDriverInfoMapper bigDataCarBizDriverInfoMapper;
}
