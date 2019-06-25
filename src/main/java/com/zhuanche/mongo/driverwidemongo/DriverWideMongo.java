package com.zhuanche.mongo.driverwidemongo;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author (yangbo)
 * @Date: 2019/4/9 10:41
 * @Description:(司机信息宽表业务)
 */
@Document(collection = "driverWideMongo")
@CompoundIndexes({
        @CompoundIndex(name = "combination_index_cgc",def = "{'cityId':1,'groupId':1,'complianceType':1}",background = true),
        @CompoundIndex(name = "combination_index_cstt",def = "{'cityId':1,'supplierId':1,'teamId':1,'teamGroupId':1}",background = true)
})
public class DriverWideMongo implements Serializable{

    private static final long serialVersionUID = 8740978101679656655L;
    /**
     * 司机id
     * mongo增加唯一索引
     */
    @Indexed(unique = true,name = "un_driverId")
    private Integer driverId;

    /**
     * 司机姓名
     */
    @Indexed(background = true)
    private String driverName;

    /**
     * 司机手机号
     */
    @Indexed(background = true)
    private String driverPhone;

    /**
     * 车牌号
     */
    @Indexed(background = true)
    private String licensePlates;

    /**
     * 司机状态【有效和无效】
     */
    private Integer status;

    /**
     * 城市id
     */
    private Integer cityId;

    /***
     * 供应商id
     */
    private Integer supplierId;

    /**
     * 车队id
     */
    private Integer teamId;

    /**
     * 小组id
     */
    private Integer teamGroupId;


    /**
     * 加盟类型
     */
    private Integer cooperationType;

    /**
     * 司机imei
     */
    @Indexed(background = true)
    private String imei;

    /**
     * 合规状态
     */
    private Integer complianceStatus;

    /**
     * 合规类型
     */
    private Integer complianceType;

    /**
     * 司机身份证
     */
    @Indexed(background = true)
    private String idCard;

    /**
     * 服务类型
     */
    private Integer groupId;

    /**
     * 是否维护形象
     */
    private Integer isImage;

    /**
     * 创建时间
     */
    @Indexed(background = true)
    private Date createDate;

    /**
     * 司机基本信息对象
     */
    private DriverWideInfoMongo info;

    /**
     * 司机明细信息对象
     */
    private DriverWideDetailMongo detail;

    /**
     * 司机停运状态  1停运 2正常
     */
    private Integer isOutOfService;

    /***
     * 司机等级
     */
    private Integer membershipRank;

    /**
     * 车型ID
     */
    private Integer modelId;

    /**
     * 司机当前GPS位置（心跳请求） 116.1312354,89.354658
     */
    private String currentGPSPoint;

    /**
     * 百度坐标（心跳请求） 116.1312354,89.354658
     */
    private String baiduCurrentGPSPoint;

    /**
     * 服务状态 1待服务2服务中3异动4锁定中
     */
    private Integer serviceStatus;

    /**
     * 上班状态
     */
    private Integer dutyStatus;

    /**
     * 在线状态
     */
    private Integer onlineStatus;

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getCurrentGPSPoint() {
        return currentGPSPoint;
    }

    public void setCurrentGPSPoint(String currentGPSPoint) {
        this.currentGPSPoint = currentGPSPoint;
    }

    public String getBaiduCurrentGPSPoint() {
        return baiduCurrentGPSPoint;
    }

    public void setBaiduCurrentGPSPoint(String baiduCurrentGPSPoint) {
        this.baiduCurrentGPSPoint = baiduCurrentGPSPoint;
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Integer getDutyStatus() {
        return dutyStatus;
    }

    public void setDutyStatus(Integer dutyStatus) {
        this.dutyStatus = dutyStatus;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Integer getIsOutOfService() {
        return isOutOfService;
    }

    public void setIsOutOfService(Integer isOutOfService) {
        this.isOutOfService = isOutOfService;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getTeamGroupId() {
        return teamGroupId;
    }

    public void setTeamGroupId(Integer teamGroupId) {
        this.teamGroupId = teamGroupId;
    }

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Integer getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(Integer complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public Integer getComplianceType() {
        return complianceType;
    }

    public void setComplianceType(Integer complianceType) {
        this.complianceType = complianceType;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getIsImage() {
        return isImage;
    }

    public void setIsImage(Integer isImage) {
        this.isImage = isImage;
    }

    public DriverWideInfoMongo getInfo() {
        return info;
    }

    public void setInfo(DriverWideInfoMongo info) {
        this.info = info;
    }

    public DriverWideDetailMongo getDetail() {
        return detail;
    }

    public void setDetail(DriverWideDetailMongo detail) {
        this.detail = detail;
    }

    public Integer getMembershipRank() {
        return membershipRank;
    }

    public void setMembershipRank(Integer membershipRank) {
        this.membershipRank = membershipRank;
    }
}
