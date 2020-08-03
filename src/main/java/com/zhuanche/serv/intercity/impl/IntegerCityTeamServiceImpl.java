package com.zhuanche.serv.intercity.impl;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import com.zhuanche.serv.intercity.IntegerCityTeamService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.mdbcarmanage.InterCityTeamMapper;
import mapper.mdbcarmanage.ex.InterCityTeamExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/13 下午4:29
 * @Version 1.0
 */
@Service
public class IntegerCityTeamServiceImpl implements IntegerCityTeamService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterCityTeamMapper teamMapper;

    @Autowired
    private InterCityTeamExMapper exMapper;


    @Override
    public AjaxResponse saveOrupdateTeam(Integer id,Integer cityId, Integer supplierId, String teamName) {
        /**校验是否已存在*/
        if(!isExist(cityId,supplierId,teamName)){
            return AjaxResponse.fail(RestErrorCode.TEAM_EXIST);
        }

        InterCityTeam team = new InterCityTeam();
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        team.setCityId(cityId);
        team.setSupplierId(supplierId);
        team.setTeamName(teamName);
        team.setCreateId(loginUser.getId());
        team.setCreateName(loginUser.getLoginName());
        team.setCreateTime(new Date());

        int code = 0;

        if(id != null && id > 0){
            team.setId(id);

            code = teamMapper.updateByPrimaryKeySelective(team);

        }else {
            code = teamMapper.insertSelective(team);
        }

        if(code > 0){
            return AjaxResponse.success(null);
        }else {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
     }

    @Override
    public InterCityTeam teamDetail(Integer id) {
        return teamMapper.selectByPrimaryKey(id);
    }


    private boolean isExist(Integer cityId, Integer supplierId, String teamName){
         /**校验是否已存在*/
         List<InterCityTeam> teamList =  exMapper.verifyTeam(cityId,supplierId,teamName);

         if(CollectionUtils.isNotEmpty(teamList)){
             logger.info("车队已存在");
             return false;
         }
         return true;
     }

}
