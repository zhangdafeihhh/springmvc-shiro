package com.zhuanche.serv.mdbcarmanage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import com.zhuanche.serv.mdbcarmanage.service.InterCityEchelonService;
import com.zhuanche.util.collectionutil.TransportUtils;
import mapper.mdbcarmanage.InterCityEchelonMapper;
import mapper.mdbcarmanage.ex.InterCityEchelonExMapper;
import mapper.mdbcarmanage.ex.InterCityTeamExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
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
public class InterCityEchelonServiceImpl implements InterCityEchelonService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterCityEchelonMapper echelonMapper;


    @Autowired
    private InterCityEchelonExMapper echelonExMapper;


    @Autowired
    private InterCityTeamExMapper teamExMapper;

    @Override
    public AjaxResponse insertSelective(Integer id,
                                        Integer cityId,
                                        Integer supplierId,
                                        Integer teamId,
                                        String echelonDate,
                                        Integer sort,
                                        String echelonMonth) {

        List<InterCityEchelon> echelonList = echelonExMapper.queryTeamId(teamId,echelonMonth);
        List<Integer> echelonDateList = TransportUtils.listInteger(echelonDate);

        boolean bl = this.isTrue(echelonList,echelonDateList);

        if(!bl){
            logger.info("同一日期仅可存在于一个梯队中”");
            return AjaxResponse.fail(RestErrorCode.SAME_ECHELON);
        }


        /**不同车队在同一日期*/
        List<InterCityTeam> teamList = teamExMapper.queryTeam(cityId,supplierId);
        List<Integer> teamIds = new ArrayList<>();
        teamList.forEach(team->{
            teamIds.add(team.getId());
        });
        List<InterCityEchelon> echelonLists = echelonExMapper.queryTeamIds(teamIds,echelonMonth);

        bl = this.isTrue(echelonLists,echelonDateList);

        if(!bl){
            logger.info("===该日期该梯队已存在车队===");
            return AjaxResponse.fail(RestErrorCode.ECHELON_HAS_EXIST);
        }

        InterCityEchelon echelon = new InterCityEchelon();
        echelon.setCreateTime(new Date());
        echelon.setEchelonDate(echelonDate);
        echelon.setEchelonMonth(echelonMonth);
        echelon.setTeamId(teamId);
        echelon.setSort(sort);
        if(id != null && id>0){
            echelon.setId(id);
            echelonMapper.updateByPrimaryKeySelective(echelon);
        }else {
            echelonMapper.insertSelective(echelon);
        }
        return AjaxResponse.success(null);
    }

    @Override
    public int updateByPrimaryKey(InterCityEchelon record) {


        return echelonMapper.updateByPrimaryKey(record);
    }

    @Override
    public InterCityEchelon echelonDetail(Integer id) {
        return echelonMapper.selectByPrimaryKey(id);
    }


    private boolean isTrue(List<InterCityEchelon> echelonList,List<Integer> echelonDateList){
        final boolean[] bl = {true};

        if(CollectionUtils.isNotEmpty(echelonList)){
            echelonList.forEach(list ->{
                String echelon = list.getEchelonDate();
                List<Integer> hasEchelonList = TransportUtils.listInteger(echelon);

                List<Integer> repeatList = echelonDateList.stream().filter(t->hasEchelonList.contains(t)).collect(Collectors.toList());

                if(CollectionUtils.isNotEmpty(repeatList)){
                    logger.info("==========该日期梯队已经被设置过=====梯队日期:{}", JSONObject.toJSONString(repeatList));
                    bl[0] = false;
                }
            });
        }
        return bl[0];
    }
}
