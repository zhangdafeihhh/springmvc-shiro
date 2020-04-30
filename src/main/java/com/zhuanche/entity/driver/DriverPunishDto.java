package com.zhuanche.entity.driver;

/**
 * @Author:qxx
 * @Date:2020/4/9
 * @Description:
 */
public class DriverPunishDto extends  DriverPunish{


    private Integer minId;

    private Integer maxId;

    public Integer getMinId() {
        return minId;
    }

    public void setMinId(Integer minId) {
        this.minId = minId;
    }

    public Integer getMaxId() {
        return maxId;
    }

    public void setMaxId(Integer maxId) {
        this.maxId = maxId;
    }
}
