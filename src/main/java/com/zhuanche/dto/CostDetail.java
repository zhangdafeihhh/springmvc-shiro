package com.zhuanche.dto;
import java.util.List;

public class CostDetail {
    private String desc;
    private Integer isRed;
    private String name;
    private String value;
    private List<SubDetail> subDetail;
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Integer getIsRed() {
		return isRed;
	}
	public void setIsRed(Integer isRed) {
		this.isRed = isRed;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<SubDetail> getSubDetail() {
		return subDetail;
	}
	public void setSubDetail(List<SubDetail> subDetail) {
		this.subDetail = subDetail;
	}
    
}
