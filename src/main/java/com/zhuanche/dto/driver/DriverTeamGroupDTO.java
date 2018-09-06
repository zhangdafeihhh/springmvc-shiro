package com.zhuanche.dto.driver;

import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;

import java.util.List;

/**
  * @description: 车队封装扩展类
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
  * @create: 2018-09-06 10:52
  *
*/

public class DriverTeamGroupDTO extends CarDriverTeam {

    private static final long serialVersionUID = 5047475786202110125L;

    private List<CarDriverTeam>  groups;

    public List<CarDriverTeam> getGroups() {
        return groups;
    }

    public void setGroups(List<CarDriverTeam> groups) {
        this.groups = groups;
    }
}
