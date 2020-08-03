package com.zhuanche.serv.common;

import com.google.common.collect.Maps;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.rentcar.CarBizSupplierDTO;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/27 下午8:38
 * @Version 1.0
 */
@Service
public class SupplierCommonService {


    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 根据供应商id获取城市名称和供应商名称
     * @param cityTeamList
     * @return
     */
    public Map<Integer, CarBizSupplierDTO> supplierMap(List<InterCityTeam> cityTeamList){
        try {
            StringBuilder supplierIds = new StringBuilder();

            cityTeamList.forEach(cityTeam ->{
                supplierIds.append(cityTeam.getSupplierId()).append(Constants.SEPERATER);
            });
            String querySupplierIds = supplierIds.toString().substring(0,supplierIds.toString().length()-1);

            Map<Integer, CarBizSupplierDTO> supplierMap = Maps.newHashMap();
            if (StringUtils.isNotEmpty(querySupplierIds)) {
                List<CarBizSupplierDTO> supplierList = carBizSupplierExMapper.queryNameBySupplierIds(querySupplierIds);
                if (CollectionUtils.isNotEmpty(supplierList)) {
                    supplierList.forEach(supplierDTO -> {
                        supplierMap.put(supplierDTO.getSupplierId(), supplierDTO);
                    });
                }
            }
            return supplierMap;
        } catch (Exception e) {
            logger.error("获取名称异常");
        }
        return null;

    }
}
