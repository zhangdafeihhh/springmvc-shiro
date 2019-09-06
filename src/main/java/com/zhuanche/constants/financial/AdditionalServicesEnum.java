package com.zhuanche.constants.financial;

import java.util.List;

import lombok.Getter;

/**  
 * ClassName:AdditionalServicesEnum <br/>  
 * Date:     2019年4月23日 上午10:23:33 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Getter
public enum AdditionalServicesEnum implements IndexedEnum{
	COMPULSORY_INSURANCE(1,"交强险"),
	BUSINESS_INSURANCE(2,"商业险"),
	DAILY_MAINTENANCE(3,"包日常保养"),
	MAINTENANCE(4,"包维修"),
	LICENSE_PLATE(5,"包上牌"),
	ANNUAL_INSPECTION(6,"包年检"),
	SERVICE_CHARGE(7,"包服务费"),
	PURCHASE_TAX(8,"包购置税"),
	GPS_DEVICE(9,"包GPS设备"),;
	
    private int index;
    private String serviceName;
    
    private static final List<AdditionalServicesEnum> INDEXS = IndexedEnumUtil.toIndexes(AdditionalServicesEnum.values());
    
	AdditionalServicesEnum(int index, String serviceName) {
	        this.index = index;
	        this.serviceName = serviceName;
    }

    /**
     * 根据index 获取指定形式名称
     *
     * @param index
     * @return
     */
    public static AdditionalServicesEnum indexOf(final int index) {
        return IndexedEnumUtil.valueOf(INDEXS, index);
    }
}
  
