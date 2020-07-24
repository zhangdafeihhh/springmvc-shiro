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
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import com.zhuanche.serv.intercity.DriverInfoInterCityService;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import mapper.mdbcarmanage.ex.InterCityTeamDriverRelExMapper;
import mapper.mdbcarmanage.ex.InterCityTeamExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public PageDTO queryDriverRelTeam(Integer pageSize,
                                      Integer pageNo,
                                      DriverInfoInterCity driverInfoInterCity,
                                      Integer teamId) {
        Page page = PageHelper.startPage(pageNo, pageSize, true);

        List<InterDriverTeamRelDto> dtoList = null;
        int total = 0;

        try {
            dtoList = exMapper.queryDriverRelTeam(driverInfoInterCity.getCityId(), driverInfoInterCity.getSupplierId(),
                    driverInfoInterCity.getDriverName(), driverInfoInterCity.getDriverPhone(),
                    driverInfoInterCity.getLicensePlates(), teamId);
            if(CollectionUtils.isNotEmpty(dtoList)){
                List<Integer> idList = new ArrayList<>();
                dtoList.forEach(dto -> {
                    idList.add(dto.getTeamId());
                });
                List<InterCityTeam> teamNamesList = teamExMapper.listTeamByIds(idList);
                Map<Integer,String> map = Maps.newHashMap();
                teamNamesList.forEach(teamName ->{
                    map.put(teamName.getId(),String.format(Constants.TEAMNAME,teamName.getTeamName()));
                });
                logger.info("----->teamNamesList====" + JSONObject.toJSONString(teamNamesList));
                dtoList.forEach(dto ->{
                    dto.setTeamName(map.get(dto.getTeamId()));
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
    public AjaxResponse queryDriverByParam(String queryParam){
        try {
            List<DriverInfoInterCity> queryDriverByParam = exMapper.queryDriverByParam(queryParam);
            return AjaxResponse.success(queryDriverByParam);
        } catch (Exception e) {
            logger.error("查询异常",e);
        }
        return  AjaxResponse.success(null);
    }
}
