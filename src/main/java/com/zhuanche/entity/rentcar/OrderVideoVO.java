package com.zhuanche.entity.rentcar;

import lombok.Data;

import java.util.Date;

@Data
public class OrderVideoVO {

    public String id;

    private Date releaseTime;

    private Integer messageId;

    private String subId;

    private Date startTime;

    private String callId;

    private Integer callType;

    private Integer releaseCause;

    private Integer originNo;

    private Date callTime;

    private String peerNo;

    private String phoneNo;

    private Integer releaseDir;

    private Date ringTime;

    private String secretNo;

    private Integer isDownloadSound;

    private String soundPath;

    private String orderNo;

    private Integer channel;

    private Integer isExpire;

    private Date createDate;

    private Date updateDate;

    private String originNoStr;

    private String releaseStr;

    private String releaseCauseStr;

    private String callTimeStr;

    private String ringTimeStr;

    private String allTimeStr;

}
