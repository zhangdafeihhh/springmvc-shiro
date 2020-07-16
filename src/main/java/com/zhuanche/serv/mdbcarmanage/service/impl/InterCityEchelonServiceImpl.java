package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.mdbcarmanage.InterCityEchelonDto;
import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import com.zhuanche.serv.mdbcarmanage.service.InterCityEchelonService;
import com.zhuanche.util.collectionutil.TransportUtils;
import mapper.mdbcarmanage.InterCityEchelonMapper;
import mapper.mdbcarmanage.InterCityTeamMapper;
import mapper.mdbcarmanage.ex.InterCityEchelonExMapper;
import mapper.mdbcarmanage.ex.InterCityTeamExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/14 下午5:02
 * @Version 1.0
 */
@Service
public class InterCityEchelonServiceImpl implements InterCityEchelonService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterCityEchelonMapper echelonMapper;


    @Autowired
    private InterCityEchelonExMapper echelonExMapper;


    @Autowired
    private InterCityTeamExMapper teamExMapper;


    @Autowired
    private InterCityTeamMapper teamMapper;

    @Override
    public AjaxResponse addOrEdit(Integer id,
                                  Integer cityId,
                                  Integer supplierId,
                                  Integer teamId,
                                  String echelonDate,
                                  Integer sort,
                                  String echelonMonth) {

        List<InterCityEchelon> echelonList = echelonExMapper.queryTeamId(teamId, echelonMonth);
        List<String> echelonDateList = TransportUtils.listStr(echelonDate);

        /**去掉转义符*/
        String repeatDate = StringEscapeUtils.unescapeJava(this.isTrue(echelonList, echelonDateList));
        if (StringUtils.isNotEmpty(repeatDate)) {
            logger.info("同一日期仅可存在于一个梯队中”");
            return AjaxResponse.fail(RestErrorCode.SAME_ECHELON, repeatDate);
        }


        /**不同车队在同一日期*/
        List<InterCityTeam> teamList = teamExMapper.queryTeam(cityId, supplierId);
        List<Integer> teamIds = new ArrayList<>();
        teamList.forEach(team -> {
            teamIds.add(team.getId());
        });
        List<InterCityEchelon> echelonLists = echelonExMapper.queryTeamIds(teamIds, echelonMonth);

        repeatDate = StringEscapeUtils.unescapeJava(this.isTrue(echelonLists, echelonDateList));

        if (StringUtils.isNotEmpty(repeatDate)) {
            logger.info("===该日期该梯队已存在车队===");
            return AjaxResponse.fail(RestErrorCode.ECHELON_HAS_EXIST, repeatDate);
        }

        InterCityEchelon echelon = new InterCityEchelon();
        echelon.setCreateTime(new Date());
        echelon.setEchelonDate(echelonDate);
        echelon.setEchelonMonth(echelonMonth);
        echelon.setTeamId(teamId);
        echelon.setSort(sort);
        if (id != null && id > 0) {
            echelon.setId(id);
            echelonMapper.updateByPrimaryKeySelective(echelon);
        } else {
            echelonMapper.insertSelective(echelon);
        }
        return AjaxResponse.success(null);
    }

    @Override
    public int updateByPrimaryKey(InterCityEchelon record) {

        return echelonMapper.updateByPrimaryKey(record);
    }

    @Override
    public InterCityEchelonDto echelonDetail(Integer id) {
        InterCityEchelon echelon = echelonMapper.selectByPrimaryKey(id);

        InterCityEchelonDto dto = new InterCityEchelonDto();

        BeanUtils.copyProperties(echelon,dto);

        InterCityTeam team = teamMapper.selectByPrimaryKey(id);

        dto.setCityId(team.getCityId());

        dto.setSupplierId(team.getSupplierId());

        return dto;
    }


    private String isTrue(List<InterCityEchelon> echelonList, List<String> echelonDateList) {
        final String[] repeatStr = {""};

        if (CollectionUtils.isNotEmpty(echelonList)) {
            echelonList.forEach(list -> {
                String echelon = list.getEchelonDate();
                List<String> hasEchelonList = TransportUtils.listStr(echelon);

                List<String> repeatList = echelonDateList.stream().filter(t -> hasEchelonList.contains(t)).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(repeatList)) {
                    logger.info("==========该日期梯队已经被设置过=====重复的梯队日期:{}", JSONObject.toJSONString(repeatList));
                    repeatStr[0] = JSONObject.toJSONString(repeatList);
                }
            });
        }
        return repeatStr[0];
    }
}
