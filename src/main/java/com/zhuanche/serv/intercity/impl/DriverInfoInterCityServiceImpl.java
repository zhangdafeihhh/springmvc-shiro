package com.zhuanche.serv.intercity.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.mdbcarmanage.IntegerDriverInfoDto;
import com.zhuanche.dto.mdbcarmanage.InterDriverTeamRelDto;
import com.zhuanche.dto.rentcar.CarBizSupplierDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import com.zhuanche.serv.common.SupplierCommonService;
import com.zhuanche.serv.intercity.DriverInfoInterCityService;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import mapper.mdbcarmanage.ex.InterCityTeamDriverRelExMapper;
import mapper.mdbcarmanage.ex.InterCityTeamExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/14 上午10:27
 * @Version 1.0
 */
@Service
public class DriverInfoInterCityServiceImpl implements DriverInfoInterCityService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DriverInfoInterCityExMapper exMapper;

    @Autowired
    private InterCityTeamExMapper teamExMapper;

    @Autowired
    private InterCityTeamDriverRelExMapper relExMapper;

    @Autowired
    private SupplierCommonService commonService;

    @Override
    public PageDTO queryDriverRelTeam(Integer pageSize,
                                      Integer pageNo,
                                      DriverInfoInterCity driverInfoInterCity,
                                      Integer teamId) {

        List<Integer> teamList = null;
        if(StringUtils.isNotEmpty(driverInfoInterCity.getDriverName())
                || StringUtils.isNotEmpty(driverInfoInterCity.getDriverPhone())
                || StringUtils.isNotEmpty(driverInfoInterCity.getLicensePlates())){
            teamList  = exMapper.queryTeamIds(driverInfoInterCity.getCityId(),driverInfoInterCity.getSupplierId(),
                    driverInfoInterCity.getDriverName(),driverInfoInterCity.getDriverPhone(),driverInfoInterCity.getLicensePlates());
            if(CollectionUtils.isEmpty(teamList)){
                return new PageDTO(pageNo, pageSize, 0, null);
            }
        }


        Page page = PageHelper.startPage(pageNo, pageSize, true);

        int total = 0;

        List<InterDriverTeamRelDto> dtoList = new ArrayList<>();

        try {
            Set<Integer> setCityIds = WebSessionUtil.isSupperAdmin() ? null : WebSessionUtil.getCurrentLoginUser().getCityIds();
            Set<Integer> setSupplierIds = WebSessionUtil.isSupperAdmin() ? null : WebSessionUtil.getCurrentLoginUser().getSupplierIds();


            List<InterCityTeam>  cityTeamList = teamExMapper.queryTeamsByParam(driverInfoInterCity.getCityId(),driverInfoInterCity.getSupplierId(),driverInfoInterCity.getTeamId(),teamList,
                    setCityIds,setSupplierIds);

            if(CollectionUtils.isNotEmpty(cityTeamList)){

                Map<Integer, CarBizSupplierDTO> supplierMap =  commonService.supplierMap(cityTeamList);


                cityTeamList.forEach(cityTeam ->{

                    InterDriverTeamRelDto driverTeamRelDto = new InterDriverTeamRelDto();

                    BeanUtils.copyProperties(cityTeam,driverTeamRelDto);

                    try {
                        driverTeamRelDto.setCityName(supplierMap == null ? null:supplierMap.get(cityTeam.getSupplierId()).getCityName());
                        driverTeamRelDto.setSupplierName(supplierMap == null ? null:supplierMap.get(cityTeam.getSupplierId()).getSupplierFullName());
                        driverTeamRelDto.setSupplierId(cityTeam.getSupplierId());
                        driverTeamRelDto.setCityId(cityTeam.getCityId());
                        driverTeamRelDto.setTeamId(cityTeam.getId());
                    } catch (Exception e) {
                        logger.error("异常",e);
                    }
                    dtoList.add(driverTeamRelDto);
                });
            }

        } catch (Exception e){
            logger.error("查询异常",e);
        }finally {
            total = (int) page.getTotal();
            PageHelper.clearPage();
        }

        PageDTO pageDTO = new PageDTO(pageNo, pageSize, total, dtoList);

        return pageDTO;
    }

    @Override
    public AjaxResponse queryDriverByTeam(Integer teamId) {
        try {
            List<Integer> driverIds = relExMapper.queryDriverIds(teamId);

            if(CollectionUtils.isEmpty(driverIds)){
                logger.info("改车队下司机为空" + teamId);
                return AjaxResponse.success(null);
            }

            List<IntegerDriverInfoDto> dtoList = exMapper.driverDtoList(driverIds);

            return AjaxResponse.success(dtoList);
        } catch (Exception e) {
            logger.error("查询司机信息异常",e);
        }
        return AjaxResponse.success(null);
    }




    @Override
    public AjaxResponse queryDriverByParam(String queryParam,Integer supplierId){
        try {
            List<DriverInfoInterCity> queryDriverByParam = exMapper.queryDriverByParam(queryParam,supplierId);
            return AjaxResponse.success(queryDriverByParam);
        } catch (Exception e) {
            logger.error("查询异常",e);
        }
        return  AjaxResponse.success(null);
    }
}
