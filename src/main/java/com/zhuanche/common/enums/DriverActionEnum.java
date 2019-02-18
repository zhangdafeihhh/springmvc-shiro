package com.zhuanche.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Getter
public enum DriverActionEnum {
    DRIVER_LOGIN(1, "司机登录"),
    DRIVER_ONLINE(2, "司机上线"),
    DRIVER_OFFLINE(3, "司机下线"),
    DRIVER_START(4, "司机出发"),
    DRIVER_ARRIVE(5, "司机达到"),
    DRIVER_START_SERVICE(6, "开始服务"),
    DRIVER_END_SERVICE(7, "服务结束"),
    DRIVER_COLLECTION(8, "司机代收"),
    DRIVER_LOGOUT(9, "司机退出"),
    APP_BACKGROUND(10, "司机后台运行"),
    APP_SIGN_INTERRUPT(11, "司机信号中断"),
    APP_SIGN_RESUME(12, "司机信号回复"),
    ORDER_GENERATE(13, "生成账单"),
    BALANCE_START(14, "开始结算"),
    INVOICE_PRINT(15, "打印发票"),
    APP_ORDER(16, "APP下单"),
    BACK_ORDER(17, "后台下单"),
    SYSTEM_CANCEL_ORDER(18, "系统取消订单"),
    APP_CANCEL_ORDER(19, "APP取消订单"),
    BACK_CANCEL_ORDER(20, "后台取消订单"),
    ORDER_SYNC(21, "订单同步"),
    HOME_MODE_OFF(22, "司机回家模式关闭"),
    HOME_MODE_ON(23, "司机回家模式开启"),
    AUTO_JOURNEYS_END(24, "自动行程结束"),
    ORDER_(25, "点击查看订单"),
    RECEIVE_ORDER(26, "确认：收到订单"),
    CANCEL_ORDER(27, "确认：取消订单"),
    SYSTEM_AUTO_START(28, "系统自动出发"),
    TOO_LONG_CANCEL_ORDER(29, "司机出发60分钟未到达,取消订单"),
    CHARGING_WAIT(30, "计费等候"),
    PASSENGER_NOT_ARRIVE_CANCEL_ORDER(31, "乘客未上车,司机结束订单"),
    GPS_TURN_ON(32, "GPS打开"),
    GPS_TURN_OFF(33, "GPS关闭")
    ;

    private int actionId;
    private String actionName;
    private static Map<Integer, String> map;

    static {
        map = new HashMap<>();
        for (DriverActionEnum actionEnum : values() ){
            map.put(actionEnum.actionId, actionEnum.actionName);
        }
    }

    DriverActionEnum(int actionId, String actionName) {
        this.actionId = actionId;
        this.actionName = actionName;
    }

    public static String getActionNameById(int actionId){
        String name = map.get(actionId);
        return name == null ? "other" : name;
    }


}
