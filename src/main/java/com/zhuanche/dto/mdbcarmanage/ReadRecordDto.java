package com.zhuanche.dto.mdbcarmanage;

import java.util.Date;

/**
 * @Author fanht
 * @Description
 * @Date 2018/11/23 下午2:01
 * @Version 1.0
 */
public class ReadRecordDto {


    private String name;

    private Date readTime;

    public ReadRecordDto(String name, Date readTime) {
        this.name = name;
        this.readTime = readTime;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    @Override
    public String toString() {
        return "ReadRecordDto{" +
                "name='" + name + '\'' +
                ", readTime=" + readTime +
                '}';
    }
}
