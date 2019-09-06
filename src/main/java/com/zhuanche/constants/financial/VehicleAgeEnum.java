package com.zhuanche.constants.financial;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.Getter;

/**
 * ClassName: VehicleAgeEnum <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason: TODO ADD REASON(可选). <br/>  
 * date: 2019年4月23日 下午2:59:05 <br/>  
 * @author baiyunlong  
 * @version
 */
@Getter
public enum VehicleAgeEnum implements IndexedEnum{
	VEHICLEAGE(1,"新车"),
	VEHICLEAGE1(2,"0～6个月"),
	VEHICLEAGE2(3,"6～12个月"),
	VEHICLEAGE3(4,"12～24个月"),
	VEHICLEAGE4(5,"24～36个月"),
	VEHICLEAGE5(6,"36个月以上"),
	VEHICLEAGE6(7,"0~3年");
    private int index;
    private String vehicleAge;
    
    private static final List<VehicleAgeEnum> INDEXS = IndexedEnumUtil.toIndexes(VehicleAgeEnum.values());
    
	VehicleAgeEnum(int index, String vehicleAge) {
	        this.index = index;
	        this.vehicleAge = vehicleAge;
    }

    /**
     * 根据index 获取指定形式名称
     *
     * @param index
     * @return
     */
    public static VehicleAgeEnum indexOf(final int index) {
        return IndexedEnumUtil.valueOf(INDEXS, index);
    }
    
    /**
     *将该枚举全部转化成json
     * @return
     */
    public static String toJson(){
        JSONArray jsonArray = new JSONArray();
        for (VehicleAgeEnum e : VehicleAgeEnum.values()) {
            JSONObject object = new JSONObject();
            object.put("vehicleAge", e.getIndex());
            object.put("vehicleAgeName", e.getVehicleAge());
            jsonArray.add(object);
        }
        return jsonArray.toString();
    }
    
}
  
