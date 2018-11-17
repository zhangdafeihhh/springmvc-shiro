package com.zhuanche.util;

import com.zhuanche.request.TeamGroupRequest;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DriverUtils {

    public static String getDriverIdsByUserTeams(CarDriverTeamService carDriverTeamService , Set<Integer> teamIds){
        if(teamIds == null || teamIds.isEmpty()){
            return null;
        }
        TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
        Set<String> teamIdString = new HashSet<>();
        for(Integer item : teamIds){
            teamIdString.add(item+"");
        }
        teamGroupRequest.setTeamIds(teamIdString);
        List<Integer> driverIdList =  carDriverTeamService.queryDriverIdsByTeamIdss(teamIds);
        String driverIds = null;
        if(!CollectionUtils.isEmpty(driverIdList)){
            for(Integer driverId : driverIdList){
                if(StringUtils.isEmpty(driverIds)){
                    driverIds = "'"+driverId+"'";
                }else{
                    driverIds += ",'"+driverId+"'";
                }
            }
        }
        return driverIds;
    }

    public static Set<Integer> getDriverIdsByUserTeamsV2(CarDriverTeamService carDriverTeamService , Set<Integer> teamIds){
        if(teamIds == null || teamIds.isEmpty()){
            return null;
        }
        TeamGroupRequest teamGroupRequest = new TeamGroupRequest();
        Set<String> teamIdString = new HashSet<>();
        for(Integer item : teamIds){
            teamIdString.add(item+"");
        }
        teamGroupRequest.setTeamIds(teamIdString);
        List<Integer> driverIdList =  carDriverTeamService.queryDriverIdsByTeamIdss(teamIds);
        Set<Integer> driverIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(driverIdList)){
            for(Integer driverId : driverIdList){
                driverIdSet.add(driverId);
            }
        }
        return driverIdSet;
    }
}
