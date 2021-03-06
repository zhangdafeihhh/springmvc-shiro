package com.zhuanche.dto.mdbcarmanage;

import java.util.Date;
import java.util.List;

/**
 * @Author fanht
 * @Description  阅读详情
 * @Date 2018/11/23 下午1:57
 * @Version 1.0
 */
public class CarMessageDetailDto {

    /**主键**/
    private Long id;
    /**消息标题**/
    private String messageTitle;
    /**消息内容**/
    private String messageContent;
    /**创建人**/
    private String createUser;
    /**状态 1 草稿 2 发布 3撤回**/
    private Integer status;
    /**级别1；全国权限2；城市权限4；加盟商权限8；车队权限16；可以多个组合**/
    private String level;
    /**等级名称**/
    private String levelName;
    /**城市id，多个逗号分隔**/
    private String cities;
    /**城市名称**/
    private String citiesName;
    /**加盟商id，中间逗号分隔**/
    private String suppliers;
    /**加盟商名称**/
    private String suppliersName;
    /**teamIds 车队id，多个 的话逗号分隔**/
    private String teamids;
    /**车队名称**/
    private String teamidsName;
    /**创建时间*/
    private Date createTime;
    /**更新时间**/
    private Date updateTime;
    /**h5专用**/
    private String levelToStr;

    /**手机号码**/
    private String phone;

    private List<ReadRecordDto> readRecord;


    /**附件对象**/
    private List<MessageDocDto> messageDocDto;

    /**发布范围1.通知分组 2 通知范围**/
    private Integer publicRange;
    /**通知分组时候选择的组ids**/
    private String messageGroupIds;



    public CarMessageDetailDto(String messageTitle, String messageContent, Date createTime, Date updateTime) {
        this.messageTitle = messageTitle;
        this.messageContent = messageContent;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(String suppliers) {
        this.suppliers = suppliers;
    }

    public String getTeamids() {
        return teamids;
    }

    public void setTeamids(String teamids) {
        this.teamids = teamids;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<MessageDocDto> getMessageDocDto() {
        return messageDocDto;
    }

    public void setMessageDocDto(List<MessageDocDto> messageDocDto) {
        this.messageDocDto = messageDocDto;
    }

    public List<ReadRecordDto> getReadRecord() {
        return readRecord;
    }

    public void setReadRecord(List<ReadRecordDto> readRecord) {
        this.readRecord = readRecord;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getCitiesName() {
        return citiesName;
    }

    public void setCitiesName(String citiesName) {
        this.citiesName = citiesName;
    }

    public String getSuppliersName() {
        return suppliersName;
    }

    public void setSuppliersName(String suppliersName) {
        this.suppliersName = suppliersName;
    }

    public String getTeamidsName() {
        return teamidsName;
    }

    public void setTeamidsName(String teamidsName) {
        this.teamidsName = teamidsName;
    }


    public String getLevelToStr() {
        return levelToStr;
    }

    public void setLevelToStr(String levelToStr) {
        this.levelToStr = levelToStr;
    }

    public Integer getPublicRange() {
        return publicRange;
    }

    public void setPublicRange(Integer publicRange) {
        this.publicRange = publicRange;
    }

    public String getMessageGroupIds() {
        return messageGroupIds;
    }

    public void setMessageGroupIds(String messageGroupIds) {
        this.messageGroupIds = messageGroupIds;
    }

    @Override
    public String toString() {
        return "CarMessageDetailDto{" +
                "id=" + id +
                ", messageTitle='" + messageTitle + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", createUser='" + createUser + '\'' +
                ", status=" + status +
                ", level='" + level + '\'' +
                ", levelName='" + levelName + '\'' +
                ", cities='" + cities + '\'' +
                ", citiesName='" + citiesName + '\'' +
                ", suppliers='" + suppliers + '\'' +
                ", suppliersName='" + suppliersName + '\'' +
                ", teamids='" + teamids + '\'' +
                ", teamidsName='" + teamidsName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", phone='" + phone + '\'' +
                ", readRecord=" + readRecord +
                ", messageDocDto=" + messageDocDto +
                '}';
    }
}
