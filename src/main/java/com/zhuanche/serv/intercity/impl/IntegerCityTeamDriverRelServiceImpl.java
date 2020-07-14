package com.zhuanche.serv.intercity.impl;

import com.zhuanche.entity.mdbcarmanage.IntercityTeamDriverRel;
import com.zhuanche.serv.intercity.IntegerCityTeamDriverRelService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.collectionutil.TransportUtils;
import mapper.mdbcarmanage.IntercityTeamDriverRelMapper;
import mapper.mdbcarmanage.ex.InterCityTeamDriverRelExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/13 下午5:09
 * @Version 1.0
 */
@Service
public class IntegerCityTeamDriverRelServiceImpl implements IntegerCityTeamDriverRelService{

    @Autowired
    private IntercityTeamDriverRelMapper relMapper;

    @Autowired
    private InterCityTeamDriverRelExMapper relExMapper;

    @Override
    public int addDriver(String driverIds, Integer teamId) {
        List<IntercityTeamDriverRel> relList = new ArrayList<>();
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        List<Integer> driverIdList = TransportUtils.listInteger(driverIds);
        driverIdList.forEach(driverId ->{
            IntercityTeamDriverRel rel = new IntercityTeamDriverRel();
            rel.setCreateId(loginUser.getId());
            rel.setCreateTime(new Date());
            rel.setDriverId(driverId);
            rel.setTeamId(teamId);
            rel.setCreateUser(loginUser.getLoginName());
            relList.add(rel);
        });
        return relExMapper.insertDriversBatch(relList);
    }

    @Override
    public int del(Integer driverId, Integer teamId) {
        return relExMapper.deleteDriver(driverId,teamId);
    }
}
