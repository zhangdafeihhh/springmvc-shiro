package com.zhuanche.constants.busManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2018-11-23 21:33
 **/
public enum EnumFuel {
    GASOLINE("1", "汽油"),
    DIESEL_OIL("2", "柴油"),
    ELECTRIC("3", "电"),
    MIXED_OIL("4", "混合油"),
    NATURAL_GAS("5", "天然气"),
    PETROLEUM_GAS("6", "液化石油气"),
    METHANOL("7", "甲醇"),
    ETHANOL("8", "乙醇"),
    SOLAR_POWER("9", "太阳能"),
    HYBRID_POWER("10", "混合动力");

    private String code;
    private String name;

    EnumFuel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getFuelNameByCode(String code) {
        for (EnumFuel fuel : EnumFuel.values()) {
            if (fuel.code.equals(code)) {
                return fuel.name;
            }
        }
        return null;
    }

    public static boolean codeIfExist(String code) {
        for (EnumFuel fuel : EnumFuel.values()) {
            if (fuel.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static String getFuelCodeByName(String name) {
        for (EnumFuel fuel : EnumFuel.values()) {
            if (fuel.name.equals(name)) {
                return fuel.code;
            }
        }
        return null;
    }

    public static List<Map<String, String>> getAllFuel() {
        List<Map<String, String>> list = new ArrayList();
        for (EnumFuel fuel : EnumFuel.values()) {
            Map<String, String> fuelMap = new HashMap<>();
            fuelMap.put("code",fuel.code);
            fuelMap.put("name",fuel.name);
            list.add(fuelMap);
        }
        return list;
    }
    public static String getAllFuelName(){
        StringBuffer sb = new StringBuffer();
        for (EnumFuel fuel : EnumFuel.values()) {
            sb.append(fuel.name).append(",");
        }
        return sb.substring(0,sb.length()-1);
    }
}
