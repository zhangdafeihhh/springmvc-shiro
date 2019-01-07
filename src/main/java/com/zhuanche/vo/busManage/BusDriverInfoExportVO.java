package com.zhuanche.vo.busManage;

import java.util.Date;

/**
 * @ClassName: BusDriverInfoExportVO
 * @Description: 巴士司机信息VO
 * @author: yanyunpeng
 * @date: 2018年11月22日 下午5:37:44
 * 
 */
public class BusDriverInfoExportVO {
	/** 司机ID*/
	private Integer driverId;
	/** 服务城市 */
	private Integer cityId;

	/** 供应商 **/
	private Integer supplierId;

	/** 车型类别 **/
	private String groupName;

	/** 司机姓名 **/
	private String name;

	/** 司机性别，[1.男0.女] **/
	private Integer gender;

	/** 身份证号 **/
	private String idCardNo;

	/** 手机号 **/
	private String phone;

	/** 出生日期 **/
	private String birthDay;

	/** 驾照类型 **/
	private String drivingLicenseType;

	/** 机动车驾驶证号 **/
	private String driverlicensenumber;

	/** 驾照领证日期 **/
	private Date issueDate;

	/** 道路运输从业资格证编号(巡游出租汽车驾驶员资格证号,巴士业务无“巡游...”业务，复用此字段) **/
	private String xyDriverNumber;

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getDrivingLicenseType() {
		return drivingLicenseType;
	}

	public void setDrivingLicenseType(String drivingLicenseType) {
		this.drivingLicenseType = drivingLicenseType;
	}

	public String getDriverlicensenumber() {
		return driverlicensenumber;
	}

	public void setDriverlicensenumber(String driverlicensenumber) {
		this.driverlicensenumber = driverlicensenumber;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getXyDriverNumber() {
		return xyDriverNumber;
	}

	public void setXyDriverNumber(String xyDriverNumber) {
		this.xyDriverNumber = xyDriverNumber;
	}

}
