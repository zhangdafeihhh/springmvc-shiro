package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class CarMessagePost{
    /**主键**/
    private Long id;
    /**消息标题**/
    private String mesageTitle;
    /**消息内容**/
    private String messageContent;
    /**创建人**/
    private Integer userId;
    /**状态 1 草稿 2 发布 3撤回**/
    private Integer status;
    /**级别1；全国权限2；城市权限4；加盟商权限8；车队权限16；可以多个组合**/
    private Integer level;
    /**城市id，多个逗号分隔**/
    private String cities;
    /**加盟商id，中间逗号分隔**/
    private String suppliers;
    /**teamIds 车队id，多个 的话逗号分隔**/
    private String teamids;
    /**创建时间*/
    private Date createTime;
    /**更新时间**/
    private Date updateTime;


    public enum Status{
        draft(1),  //草稿状态
        publish(2); //发布状态


        private Integer messageStatus;

        Status(Integer messageStatus) {
            this.messageStatus = messageStatus;
        }

        public Integer getMessageStatus() {
            return messageStatus;
        }

        public void setMessageStatus(Integer messageStatus) {
            this.messageStatus = messageStatus;
        }

        public static Status getMessageStatus(int messageStatus){
            switch (messageStatus){
                case 1:
                    return draft;
                case 2:
                    return publish;
                default:
                    return draft;
            }
        }
    }

    public enum Level{


        contry(1,"全国"), //全国
        city(2,"城市"),   //城市
        suppy(4,"加盟商"),  //加盟商
        cityAndSuppy(6,"城市 加盟商"),//城市+加盟商
        team(8,"车队"),   //车队
        cityAndTeam(10,"城市 车队"), //城市+车队
        suppyAndTeam(10,"加盟商 车队"), //加盟商+车队
        cityAndSuppyAndTeam(14,"城市 加盟商 车队");//城市+加盟商+车队


        private Integer level;

        private String name;

        Level(Integer level) {
            this.level = level;
        }


        Level(Integer level, String name) {
            this.level = level;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMesageTitle() {
        return mesageTitle;
    }

    public void setMesageTitle(String mesageTitle) {
        this.mesageTitle = mesageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent == null ? null : messageContent.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities == null ? null : cities.trim();
    }

    public String getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(String suppliers) {
        this.suppliers = suppliers == null ? null : suppliers.trim();
    }

    public String getTeamids() {
        return teamids;
    }

    public void setTeamids(String teamids) {
        this.teamids = teamids == null ? null : teamids.trim();
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

    @Override
    public String toString() {
        return "CarMessagePost{" +
                "id=" + id +
                ", mesageTitle='" + mesageTitle + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", level=" + level +
                ", cities='" + cities + '\'' +
                ", suppliers='" + suppliers + '\'' +
                ", teamids='" + teamids + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }



}