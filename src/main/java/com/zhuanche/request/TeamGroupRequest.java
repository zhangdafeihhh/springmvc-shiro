package com.zhuanche.request;

import java.util.Set;

/**
  * @description: 接口请求参数封装类
  *
  * <PRE>
  * <BR>	修改记录
  * <BR>-----------------------------------------------
  * <BR>	修改日期			修改人			修改内容
  * </PRE>
  *
  * @author lunan
  * @version 1.0
  * @since 1.0
  * @create: 2018-08-30 10:10
  *
*/

public class TeamGroupRequest extends PageRequest{

    private static final long serialVersionUID = 5950495211900158496L;

    private String relationId;

    private String teamId;

    private String driverId;

    private String teamName;

    private String groupId;

    private String groupName;

    private Set<String> groupIds;

    private Set<String> teamIds;

    public Set<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<String> groupIds) {
        this.groupIds = groupIds;
    }

    public Set<String> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(Set<String> teamIds) {
        this.teamIds = teamIds;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
