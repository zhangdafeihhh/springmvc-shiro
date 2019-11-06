package com.zhuanche.dto.mdbcarmanage;

import com.zhuanche.common.syslog.Column;
import lombok.Data;

import java.io.Serializable;
@Data
public class CarAdmUserDto implements Serializable {

    @Column(desc="用户ID")
    private Integer userId;
    @Column(desc="用户名")
    private String userName;

    @Column(desc="登录账号")
    private String account;

    @Column(desc="手机号")
    private String phone;

    @Column(desc="可见的城市列表")
    private String cityIds;

    @Column(desc="可见的厂商列表")
    private String supplierIds;

    @Column(desc="可见的车队列表")
    private String teamIds;

    @Column(desc="可见的班组列表")
    private String groupIds;


}
