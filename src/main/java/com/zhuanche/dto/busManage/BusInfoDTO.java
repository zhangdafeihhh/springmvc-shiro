package com.zhuanche.dto.busManage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zhuanche.dto.BaseDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @program: mp-manage
 * @description: 用于接收车辆请求参数
 * @author: niuzilian
 * @create: 2018-11-22 16:30
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusInfoDTO extends BaseDTO{
    /**城市ID*/
    private Integer cityId;
    /**供应商ID*/
    private Integer supplierId;
    /**车型类别ID*/
    private Integer groupId;
    /**车牌号*/
    private String licensePlates;
    /**状态 0无效，1有效，2逻辑删除，3三方车辆数据*/
    private Integer status;
    /**创建时间（开始）*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDateStart;
    /**创建时间（结束）*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDateEnd;

}
