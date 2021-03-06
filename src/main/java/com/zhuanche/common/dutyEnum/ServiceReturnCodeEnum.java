package com.zhuanche.common.dutyEnum;

import com.zhuanche.util.Check;

/**
 * @program: bus-manage
 * @description: 车辆导入名单的枚举类型
 * @author: admin
 * @create: 2018-07-19 19:49
 **/
public enum ServiceReturnCodeEnum {
    RECODE_EXISTS("记录已存在", -100),
    NONE_RECODE_EXISTS("记录不存在", -200),
    NONE_CITY_EXISTS("请选择城市", -1000),
    NONE_SUPPLIER_EXISTS("请选择供应商", -2000),
    NONE_TEAM_EXISTS("请选择车队", -3000),
    DEAL_SUCCESS("操作成功", 1),
    DEAL_SUCCESS_MSG("正在操作稍后查看", 2),
    DEAL_FAILURE("操作失败", 0);

    private String name;
    private int code;

    ServiceReturnCodeEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    /**
     * 获取当前的类型
     * @author afi
     * @param code
     * @return
     */
    public static ServiceReturnCodeEnum getTypeByCode(Integer code) {
        if (Check.NuNObj(code)){
            return null;
        }
        ServiceReturnCodeEnum[] enums = ServiceReturnCodeEnum.values();
        for(ServiceReturnCodeEnum enumtype:enums) {
            if(enumtype.getCode() == code) {
                return enumtype;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
