package com.zhuanche.serv.bigdata;

import com.zhuanche.dto.disinfect.DisinfectParamDTO;
import com.zhuanche.dto.disinfect.DisinfectResultDTO;
import lombok.extern.slf4j.Slf4j;
import mapper.bigdata.BigDataCarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Service
@Slf4j
public class BigDataDriverInfoService {
    @Autowired
    private BigDataCarBizDriverInfoMapper bigDataCarBizDriverInfoMapper;
    @Autowired
    private CarBizCityExMapper carBizCityExMapper;
    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    public List<DisinfectResultDTO> list(DisinfectParamDTO disinfectParamDTO) {
        List<DisinfectResultDTO> list = bigDataCarBizDriverInfoMapper.list(disinfectParamDTO);
        list.forEach(dto -> {
            if (StringUtils.isBlank(dto.getCityName())) {
                dto.setCityName(carBizCityExMapper.queryNameById(dto.getCityId()));
            }
            dto.setSupplierName(carBizSupplierExMapper.getSupplierNameById(dto.getSupplierId()));
        });
        return list;
    }
}
