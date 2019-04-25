package com.zhuanche.constants.financial;

import java.util.List;


import lombok.Getter;

/**  
 * ClassName:EnergyTypeEnum <br/>  
 * Date:     2019年4月25日 下午6:04:05 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Getter
public enum EnergyTypeEnum implements IndexedEnum{
	ENERGY_TYPE_ELECTRICITY(1,"纯电"),
	ENERGY_TYPE_MIXING(2,"混动"),
	ENERGY_TYPE_GAAOLINE(3,"汽油"),
	ENERGY_TYPE_DIESEL_OIL(4,"柴油"),;
	
	
    private int index;
    private String energyTypeName;
    
    private static final List<EnergyTypeEnum> INDEXS = IndexedEnumUtil.toIndexes(EnergyTypeEnum.values());
    
    EnergyTypeEnum(int index, String energyTypeName) {
	        this.index = index;
	        this.energyTypeName = energyTypeName;
    }

    /**
     * 根据index 获取指定形式名称
     *
     * @param index
     * @return
     */
    public static EnergyTypeEnum indexOf(final int index) {
        return IndexedEnumUtil.valueOf(INDEXS, index);
    }

}
  
