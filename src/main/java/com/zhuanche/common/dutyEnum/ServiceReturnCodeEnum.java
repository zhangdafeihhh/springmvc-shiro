package com.zhuanche.common.dutyEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: bus-manage
 * @description: 车辆导入名单的枚举类型
 * @author: admin
 * @create: 2018-07-19 19:49
 **/
public enum ServiceReturnCodeEnum {
    RECODE_EXISTS("记录已存在", 2000),
    NONE_RECODE_EXISTS("记录不存在", 2000),
    DEAL_SUCCESS("操作成功", 1),
    DEAL_FAILURE("操作失败", 0);

    private String name;
    private int code;

    ServiceReturnCodeEnum(String name, int code) {
        this.name = name;
        this.code = code;
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
