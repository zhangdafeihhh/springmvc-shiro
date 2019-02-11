package com.zhuanche.request;

import java.util.Map;
import java.util.Set;


public class DriverMonthDutyRequest extends PageRequest{

	private static final long serialVersionUID = 8535141576127030598L;

	private Integer id;

	private String monitorDate;//考勤日期

	private Integer driverId; //司机ID

	private String driverName;//司机姓名

	private String driverPhone;//司机手机号

	private String licensePlates;//车牌号
	
	private String data;//该月排班详情
	
	private Map<String,String> map;//
	
	private String monitorDateBegin;//查询，开始时间

	private String monitorDateEnd;//查询结束时间
	
	private int status;

	private String cityId;// 城市

	private String cityName;

	private String supplierId;// 厂商

	private String teamName;

	private String groupId;// 小组

	private String groupIds;

	private String groupName;

	private String supplierName;

	private String teamId;// 车队

	// 权限
	private String suppliers;
	private String cities;

	private Set<Integer> cityIds;//普通管理员可以管理的所有城市ID

	private Set<Integer> supplierIds;//普通管理员可以管理的所有供应商ID

	private Set<Integer> teamIds;//普通管理员可以管理的所有车队ID

	private Set<Integer> driverIds;//车队长可以看到的司机id列表
	
	private String value;
	private String day1;
	private String day2;
	private String day3;
	private String day4;
	private String day5;
	private String day6;
	private String day7;
	private String day8;
	private String day9;
	private String day10;
	
	private String day11;
	private String day12;
	private String day13;
	private String day14;
	private String day15;
	private String day16;
	private String day17;
	private String day18;
	private String day19;
	private String day20;
	
	private String day21;
	private String day22;
	private String day23;
	private String day24;
	private String day25;
	private String day26;
	private String day27;
	private String day28;
	private String day29;
	private String day30;
	
	private String day31;
	
	private String fileName;//批量导入上传文件名称

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<Integer> getCityIds() {
		return cityIds;
	}

	public void setCityIds(Set<Integer> cityIds) {
		this.cityIds = cityIds;
	}

	public Set<Integer> getSupplierIds() {
		return supplierIds;
	}

	public void setSupplierIds(Set<Integer> supplierIds) {
		this.supplierIds = supplierIds;
	}

	public Set<Integer> getTeamIds() {
		return teamIds;
	}

	public void setTeamIds(Set<Integer> teamIds) {
		this.teamIds = teamIds;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @param
	 * @param
	 * @param
	 */
	public static void main(String[] args) {
		String key = "03-11";
		int day = Integer.parseInt(key.substring(3, 5));
	}
	public void setValue(String key,String val) {
		int day = Integer.parseInt(key.substring(3, 5));
		if(day==1){
			setDay1(val);
		}else if(day==2){
			setDay2(val);
		}else if(day==3){
			setDay3(val);
		}else if(day==4){
			setDay4(val);
		}else if(day==5){
			setDay5(val);
		}else if(day==6){
			setDay6(val);
		}else if(day==7){
			setDay7(val);
		}else if(day==8){
			setDay8(val);
		}else if(day==9){
			setDay9(val);
		}else if(day==10){
			setDay10(val);
		}else if(day==11){
			setDay11(val);
		}else if(day==12){
			setDay12(val);
		}else if(day==13){
			setDay13(val);
		}else if(day==14){
			setDay14(val);
		}else if(day==15){
			setDay15(val);
		}else if(day==16){
			setDay16(val);
		}else if(day==17){
			setDay17(val);
		}else if(day==18){
			setDay18(val);
		}else if(day==19){
			setDay19(val);
		}else if(day==20){
			setDay20(val);
		}else if(day==21){
			setDay21(val);
		}else if(day==22){
			setDay22(val);
		}else if(day==23){
			setDay23(val);
		}else if(day==24){
			setDay24(val);
		}else if(day==25){
			setDay25(val);
		}else if(day==26){
			setDay26(val);
		}else if(day==27){
			setDay27(val);
		}else if(day==28){
			setDay28(val);
		}else if(day==29){
			setDay29(val);
		}else if(day==30){
			setDay30(val);
		}else{
			setDay31(val);
		}
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDay1() {
		return day1;
	}
	public void setDay1(String day1) {
		this.day1 = day1;
	}
	public String getDay2() {
		return day2;
	}
	public void setDay2(String day2) {
		this.day2 = day2;
	}
	public String getDay3() {
		return day3;
	}
	public void setDay3(String day3) {
		this.day3 = day3;
	}
	public String getDay4() {
		return day4;
	}
	public void setDay4(String day4) {
		this.day4 = day4;
	}
	public String getDay5() {
		return day5;
	}
	public void setDay5(String day5) {
		this.day5 = day5;
	}
	public String getDay6() {
		return day6;
	}
	public void setDay6(String day6) {
		this.day6 = day6;
	}
	public String getDay7() {
		return day7;
	}
	public void setDay7(String day7) {
		this.day7 = day7;
	}
	public String getDay8() {
		return day8;
	}
	public void setDay8(String day8) {
		this.day8 = day8;
	}
	public String getDay9() {
		return day9;
	}
	public void setDay9(String day9) {
		this.day9 = day9;
	}
	public String getDay10() {
		return day10;
	}
	public void setDay10(String day10) {
		this.day10 = day10;
	}
	public String getDay11() {
		return day11;
	}
	public void setDay11(String day11) {
		this.day11 = day11;
	}
	public String getDay12() {
		return day12;
	}
	public void setDay12(String day12) {
		this.day12 = day12;
	}
	public String getDay13() {
		return day13;
	}
	public void setDay13(String day13) {
		this.day13 = day13;
	}
	public String getDay14() {
		return day14;
	}
	public void setDay14(String day14) {
		this.day14 = day14;
	}
	public String getDay15() {
		return day15;
	}
	public void setDay15(String day15) {
		this.day15 = day15;
	}
	public String getDay16() {
		return day16;
	}
	public void setDay16(String day16) {
		this.day16 = day16;
	}
	public String getDay17() {
		return day17;
	}
	public void setDay17(String day17) {
		this.day17 = day17;
	}
	public String getDay18() {
		return day18;
	}
	public void setDay18(String day18) {
		this.day18 = day18;
	}
	public String getDay19() {
		return day19;
	}
	public void setDay19(String day19) {
		this.day19 = day19;
	}
	public String getDay20() {
		return day20;
	}
	public void setDay20(String day20) {
		this.day20 = day20;
	}
	public String getDay21() {
		return day21;
	}
	public void setDay21(String day21) {
		this.day21 = day21;
	}
	public String getDay22() {
		return day22;
	}
	public void setDay22(String day22) {
		this.day22 = day22;
	}
	public String getDay23() {
		return day23;
	}
	public void setDay23(String day23) {
		this.day23 = day23;
	}
	public String getDay24() {
		return day24;
	}
	public void setDay24(String day24) {
		this.day24 = day24;
	}
	public String getDay25() {
		return day25;
	}
	public void setDay25(String day25) {
		this.day25 = day25;
	}
	public String getDay26() {
		return day26;
	}
	public void setDay26(String day26) {
		this.day26 = day26;
	}
	public String getDay27() {
		return day27;
	}
	public void setDay27(String day27) {
		this.day27 = day27;
	}
	public String getDay28() {
		return day28;
	}
	public void setDay28(String day28) {
		this.day28 = day28;
	}
	public String getDay29() {
		return day29;
	}
	public void setDay29(String day29) {
		this.day29 = day29;
	}
	public String getDay30() {
		return day30;
	}
	public void setDay30(String day30) {
		this.day30 = day30;
	}
	public String getDay31() {
		return day31;
	}
	public void setDay31(String day31) {
		this.day31 = day31;
	}
	public Map<String, String> getMap() {
		return map;
	}
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getMonitorDate() {
		return monitorDate;
	}
	public void setMonitorDate(String monitorDate) {
		this.monitorDate = monitorDate;
	}
	public String getMonitorDateBegin() {
		return monitorDateBegin;
	}
	public void setMonitorDateBegin(String monitorDateBegin) {
		this.monitorDateBegin = monitorDateBegin;
	}
	public String getMonitorDateEnd() {
		return monitorDateEnd;
	}
	public void setMonitorDateEnd(String monitorDateEnd) {
		this.monitorDateEnd = monitorDateEnd;
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
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCities() {
		return cities;
	}
	public void setCities(String cities) {
		this.cities = cities;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getSuppliers() {
		return suppliers;
	}
	public void setSuppliers(String suppliers) {
		this.suppliers = suppliers;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Set<Integer> getDriverIds() {
		return driverIds;
	}

	public void setDriverIds(Set<Integer> driverIds) {
		this.driverIds = driverIds;
	}
}
