package com.zhuanche.common.enums;

public enum PermissionLevelEnum {
    ALL("全国",1),CITY("城市",2),SUPPLIER("合作商",4),TEAM("车队",8),GROUP("班组",16);

    private String name;
    private Integer code;

    PermissionLevelEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "{" +
                "\"levelName\":\"" + name + '\"' +
                ", \"code\":" + code +
                '}';
    }

    public static PermissionLevelEnum getEnumByCode(Integer code){
        for (PermissionLevelEnum levelEnum: values()){
            if (levelEnum.code.equals(code)){
                return levelEnum;
            }
        }
        return null;
    }
}
