package com.zhuanche.dto.driverDuty;

import com.zhuanche.entity.mdbcarmanage.CarDutyDuration;

public class CarDriverDurationDTO extends CarDutyDuration {

    private static final long serialVersionUID = 8479238538627739203L;

    private String  createName;
    private String  updateName;
    private String  teamName;

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}