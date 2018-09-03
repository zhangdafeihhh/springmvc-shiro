package com.zhuanche.util;

import java.util.ArrayList;
import java.util.List;

public class ImportTempletUtils {
	
	// 模板字段
	private static List<String> busDriverTempletFields = new ArrayList<String>();
	
	// 初始化模板字段
	static {
		initBusTempletFields();
	}
	
	/**
	 * 初始化巴士模板字段
	 */
	private static void initBusTempletFields() {
		// 必填
		busDriverTempletFields.add("机动车驾驶员姓名");
		busDriverTempletFields.add("驾驶员身份证号");
		busDriverTempletFields.add("出生日期");
		busDriverTempletFields.add("驾驶员性别");
		busDriverTempletFields.add("驾驶员手机号");
		busDriverTempletFields.add("驾照类型");
		busDriverTempletFields.add("驾照领证时间");
		busDriverTempletFields.add("机动车驾驶证号");
		busDriverTempletFields.add("驾驶员合同（或协议）签署公司");
		busDriverTempletFields.add("网络预约出租汽车驾驶员资格证号");
		busDriverTempletFields.add("车牌号");
		busDriverTempletFields.add("车型类别");
	}
	
	/**
	 * 模板类型
	 */
	public enum TempletType{
		BUS_DRIVER(1, "巴士司机模板");
		
		private Integer value;
		private String desp;
		
		private TempletType(Integer value, String desp) {
			this.value = value;
			this.desp = desp;
		}
		public Integer getValue() {
			return value;
		}
		public void setValue(Integer value) {
			this.value = value;
		}
		public String getDesp() {
			return desp;
		}
		public void setDesp(String desp) {
			this.desp = desp;
		}
	}

	/**
	 * 获取指定模板
	 * @return
	 */
	public static List<String> getTempletFields(TempletType type){
		switch (type.value) {
		case 1:
			return busDriverTempletFields;
		default:
			return null;
		}
	}
	
}
