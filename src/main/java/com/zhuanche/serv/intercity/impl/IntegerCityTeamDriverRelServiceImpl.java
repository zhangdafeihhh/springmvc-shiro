package com.zhuanche.serv.intercity.impl;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.mdbcarmanage.IntercityTeamDriverRel;
import com.zhuanche.serv.intercity.IntegerCityTeamDriverRelService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.collectionutil.TransportUtils;
import mapper.mdbcarmanage.IntercityTeamDriverRelMapper;
import mapper.mdbcarmanage.ex.InterCityTeamDriverRelExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterCityTeamDriverRelExMapper relExMapper;

    @Override
    public AjaxResponse addDriver(String driverIds, Integer teamId) {

        List<IntercityTeamDriverRel> relList = new ArrayList<>();
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        /**批量加入前先删*/
        relExMapper.delByTeamId(teamId);
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
         relExMapper.insertDriversBatch(relList);
         return AjaxResponse.success(null);

    }

    @Override
    public int del(Integer driverId, Integer teamId) {
        return relExMapper.deleteDriver(driverId,teamId);
    }
}
