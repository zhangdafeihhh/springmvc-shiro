package com.zhuanche.constants.financial;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.Getter;

/**
 * ClassName: MileageEnum <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason: TODO ADD REASON(可选). <br/>  
 * date: 2019年4月23日 下午3:05:52 <br/>  
 * @author baiyunlong  
 * @version
 */
@Getter
public enum MileageEnum implements IndexedEnum{
	Mileage(1,"0～1000公里"),
	Mileage1(2,"1000～10000公里"),
	Mileage2(3,"10000～20000公里"),
	Mileage3(4,"20000～100000公里"),
	Mileage4(5,"100000公里以上"),
	Mileage5(6,"0～20000公里");
    private int index;
    private String mileage;
    
    private static final List<MileageEnum> INDEXS = IndexedEnumUtil.toIndexes(MileageEnum.values());
    
	MileageEnum(int index, String mileage) {
	        this.index = index;
	        this.mileage = mileage;
    }

    /**
     * 根据index 获取指定形式名称
     *
     * @param index
     * @return
     */
    public static MileageEnum indexOf(final int index) {
        return IndexedEnumUtil.valueOf(INDEXS, index);
    }
    
    /**
     *将该枚举全部转化成json
     * @return
     */
    public static String toJson(){
        JSONArray jsonArray = new JSONArray();
        for (MileageEnum e : MileageEnum.values()) {
            JSONObject object = new JSONObject();
            object.put("mileage", e.getIndex());
            object.put("mileageName", e.getMileage());
            jsonArray.add(object);
        }
        return jsonArray.toString();
    }
}
  
