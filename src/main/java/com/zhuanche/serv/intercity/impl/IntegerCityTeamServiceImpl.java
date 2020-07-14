package com.zhuanche.serv.intercity.impl;

import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import com.zhuanche.serv.intercity.IntegerCityTeamService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.mdbcarmanage.InterCityTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/13 下午4:29
 * @Version 1.0
 */
@Service
public class IntegerCityTeamServiceImpl implements IntegerCityTeamService{

    @Autowired
    private InterCityTeamMapper teamMapper;

    @Override
    public int addTeam(Integer cityId, Integer supplierId, String teamName) {

        InterCityTeam team = new InterCityTeam();

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        team.setCityId(cityId);
        team.setSupplierId(supplierId);
        team.setTeamName(teamName);
        team.setCreateId(loginUser.getId());
        team.setCreateName(loginUser.getLoginName());
        team.setCreateTime(new Date());
        return teamMapper.insertSelective(team);
    }

    @Override
    public int updateTeam(Integer id,Integer cityId, Integer supplierId, String teamName) {
        InterCityTeam team = new InterCityTeam();
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        team.setCityId(cityId);
        team.setSupplierId(supplierId);
        team.setTeamName(teamName);
        team.setCreateId(loginUser.getId());
        team.setCreateName(loginUser.getLoginName());
        team.setCreateTime(new Date());
        return teamMapper.updateByPrimaryKeySelective(team);
     }
}
