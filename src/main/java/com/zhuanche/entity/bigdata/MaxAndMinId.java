package com.zhuanche.entity.bigdata;

/**
 * @Author fanht
 * @Description
 * @Date 2020/4/15 下午9:23
 * @Version 1.0
 */
public class MaxAndMinId {

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


    public MaxAndMinId() {
    }

    public MaxAndMinId(Integer minId, Integer maxId) {
        this.minId = minId;
        this.maxId = maxId;
    }
}
