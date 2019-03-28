package com.zhuanche.vo.busManage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BusDriverDetailInfoVO implements Serializable {

	private static final long serialVersionUID = 3499280025355834858L;

	private String id;
	/** 司机ID **/
	private Integer driverId;

	/** 司机手机号 **/
	private String phone;

	/** 司机性别 (1.男;0.女) **/
	private Integer gender;

	/** 司机姓名 **/
	private String name;

	/** 司机年龄 **/
	private Integer age;
	/** 城市id **/
	private Integer cityId;

	/** 城市ID **/
	private Integer serviceCity;

	/** 城市名称 **/
	private String cityName;

	/** 供应商ID **/
	private Integer supplierId;

	/** 供应商名称 **/
	private String supplierName;

	/** 加盟类型ID **/
	private Byte cooperationType;

	/** 加盟类型名称 **/
	private String cooperationTypeName;

	/** 服务类型ID **/
	private Integer groupId;

	/** 服务类型名称 **/
	private String groupName;

	/** 现住址 **/
	private String currentAddress;

	/** 紧急联系人 **/
	private String emergencyContactPerson;

	/** 紧急联系方式 **/
	private String emergencyContactNumber;

	/** 身份证号 **/
	private String idCardNo;

	/** 服务监督码 **/
	private String superintendNo;

	/** 服务监督码链接 **/
	private String superintendUrl;

	/** 驾照类型,例：A1,B1,C1 **/
	private String drivingLicenseType;

	/** 驾龄 **/
	private Integer drivingYears;

	/** 档案编号 **/
	private String archivesNo;

	/** 领证日期 yyyy-MM-dd **/
	private Date issueDate;

	/** 驾照到期时间 yyyy-MM-dd **/
	private Date expireDate;

	/** 司机车牌号 **/
	private String licensePlates;

	/** 状态（1.有效 0.无效） **/
	private Integer status;

	/** 司机头像,图片url **/
	private String photosrct;

	/** 是否绑定信用卡 1绑定 0未绑定 **/
	private Integer isBindingCreditCard;

	/** 信用卡号 **/
	private String creditCardNo;

	/** 信用卡短卡号（块钱接口返回） **/
	private String storableCardNo;

	/** 信用卡开户行 **/
	private String creditOpenAccountBank;

	/** 快钱customerID,用于建立司机与信用卡的绑定关系，不能使用电话号码 **/
	private String quickpayCustomerid;

	/** imei **/
	private String imei;

	/** 机动车驾驶证号 **/
	private String driverlicensenumber;

	/** 驾驶证扫描件URL **/
	private String drivinglicenseimg;

	/** 初次领取驾驶证日期 yyyy-MM-dd **/
	private String firstdrivinglicensedate;

	/** 网络预约出租汽车驾驶员证初领日期 yyyy-MM-dd **/
	private String firstmeshworkdrivinglicensedate;

	/** 国籍 **/
	private String nationality;

	/** 户籍 **/
	private String householdregister;

	/** 驾驶员民族 **/
	private String nation;

	/** 驾驶员婚姻状况 **/
	private String marriage;

	/** 外语能力 (0无（默认） 1 英语 2 德语 3 法语 4 其他) **/
	private String foreignlanguage;

	/** 驾驶员通信地址 **/
	private String address;

	/** 驾驶员学历 **/
	private String education;

	/** 驾驶员合同（或协议）签署公司标识 yyyy-MM-dd **/
	private String corptype;

	/** 签署日期 yyyy-MM-dd **/
	private String signdate;

	/** 合同（或协议）到期时间 yyyy-MM-dd **/
	private String signdateend;

	/** 有效合同时间 yyyy-MM-dd **/
	private String contractdate;

	/** 是否巡游出租汽车驾驶员(1.是 0.否) **/
	private Integer isxydriver;

	/** 专职或兼职司机 **/
	private String parttimejobdri;

	/** 司机手机型号 **/
	private String phonetype;

	/** 司机手机运营商 **/
	private String phonecorp;

	/** 使用地图类型 **/
	private String maptype;

	/** 紧急情况联系人通讯地址 **/
	private String emergencycontactaddr;

	/** 评估，驾驶员服务质量信誉考核结果 **/
	private String assessment;

	/** 资格证有效起始日期，驾驶员证发证日期 yyyy-MM-dd **/
	private String driverlicenseissuingdatestart;

	/** 资格证有效截止日期，驾驶员证有效期止 yyyy-MM-dd **/
	private String driverlicenseissuingdateend;

	/** 网络预约出租汽车驾驶员证发证机构,驾驶员证发证机构 **/
	private String driverlicenseissuingcorp;

	/** 网络预约出租汽车驾驶员资格证号,网络预约出租汽车驾驶员证编号 **/
	private String driverlicenseissuingnumber;

	/** 巡游出租汽车驾驶员资格证号 **/
	private String xyDriverNumber;

	/** 注册日期 yyyy-MM-dd **/
	private String driverLicenseIssuingRegisterDate;

	/** 初次领取资格证日期 yyyy-MM-dd **/
	private String driverLicenseIssuingFirstDate;

	/** 资格证发证日期 yyyy-MM-dd **/
	private String driverLicenseIssuingGrantDate;

	/** 出生日期 yyyy-MM-dd **/
	private String birthDay;

	/** 户口登记机关名称 **/
	private String houseHoldRegisterPermanent;

	/** 司机是否维护形象 (0.未维护 1.已维护) **/
	private Byte isImage;

	/** 备注 **/
	private String memo;

	/** 创建人ID **/
	private Integer createBy;
	/** 创建人名称 **/
	private String createName;
	/** 创建时间 **/
	private Date createDate;
	/** 修改人ID **/
	private Integer updateBy;
	/** 修改人名称 **/
	private String updateName;
	/** 修改时间 **/
	private Date updateDate;

	/** 车队ID **/
	private Integer teamId;
	/** 车队名称 **/
	private String teamName;
	/** 车队下小组ID **/
	private Integer teamGroupId;
	/** 车队下小组名称 **/
	private String teamGroupName;

	/** 银行卡开户行 **/
	private String bankCardBank;
	/** 银行卡卡号 **/
	private String bankCardNumber;

	/** 高德IOS_IMEI **/
	private String ext6;

}