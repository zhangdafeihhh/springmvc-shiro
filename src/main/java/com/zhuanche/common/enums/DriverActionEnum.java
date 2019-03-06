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
    GPS_TURN_OFF(33, "GPS关闭"),
    NAVIGATION_TURN_ON(40, "开启导航"),
    NAVIGATION_TURN_OFF(41, "关闭导航"),
    TIME_LESS_ROAD(42, "时间最短线路"),
    DISTANCE_LESS_ROAD(43, "距离最短线路"),
    COST_LESS_ROAD(44, "走最少费路"),
    IMMEDIATELY_ORDER_TURN_ON(45, "开启即时单绑单"),
    IMMEDIATELY_ORDER_TURN_OFF(46, "关闭即时单绑单"),
    CROSS_ORDER_TURN_ON(47, "开启跨级接单"),
    CROSS_ORDER_TURN_OFF(48, "关闭跨级接单"),
    AUTO_SERVICE(49, "自动开始服务"),
    SINGLE_PATH_SPEED_FIRST(50, "默认导航策略：单路径，速度优先"),
    SINGLE_ROAD_COST(51, "单路径-费用优先,尽量避开收费道路，有可能起始点间必有收费路"),
    SINGLE_ROAD_DISTANCE(52, "单路径-距离优先,距离最短"),
    SINGLE_ROAD_NORMAL(53, "单路径-普通路优先,不走快速路，包含高速路"),
    SINGLE_ROAD_TIME(54, "单路径,时间优先 规避拥堵的路线,考虑实时路况"),
    ALL_STRATEGY(55, "多策略,同时使用速度优先、费用优先、距离优先三个策略计算路径"),
    SINGLE_ROAD_NO_FAST(56, "单路径,不走高速"),
    SINGLE_ROAD_NO_FAST_NO_CHARGE(57, "单路径,不走高速,躲避收费"),
    SINGLE_ROAD_NO_CHARGE_NO_CROWDING(58, "单路径,躲避收费,躲避拥堵"),
    SINGLE_ROAD_NO_FAST_NO_CHARGE_NO_CROWDING(59, "单路径,不走高速,躲避收费,躲避拥堵"),
    MULTI_ROAD(60, "多路径,默认无策略"),
    MULTI_ROAD_TIMELESS_SHORTEST(61, "多路径,时间最短，距离最短"),
    MULTI_ROAD_NO_CROWDING_SHORTEST(62, "多路径,躲避拥堵,考虑路况"),
    MULTI_ROAD_NO_FAST(63, "多路径,不走高速"),
    MULTI_ROAD_NO_CHARGE(64, "多路径,躲避收费"),
    MULTI_ROAD_NO_FAST_NO_CROWDING(65, "多路径,不走高速,躲避拥堵"),
    MULTI_ROAD_NO_FAST_NO_CHARGE(66, "多路径,不走高速,躲避收费"),
    MULTI_ROAD_NO_CHARGE_NO_CROWDING(67, "多路径,躲避收费,躲避拥堵"),
    MULTI_ROAD_NO_FAST_NO_CHARGE_NO_CROWDING(68, "多路径,不走高速,躲避收费,躲避拥堵"),
    MULTI_ROAD_FAST(69, "多路径,高速优先"),
    MULTI_ROAD_FAST_NO_CROWDING(70, "多路径,高速优先,躲避拥堵"),
    AUTO_CHARGING_WAIT(80, "自动开始计费等候"),
    MANUAL_CHARGING_WAIT(81, "手动开始计费等候"),
    MANUAL_CHARGING_END(82, "手动结束计费等候"),
    AUTO_CHARGING_END(83, "自动结束计费等候"),
    PASSENGER_ABOARD(84, "接到乘客"),
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

    public static Map<Integer, String> getMap(){
        return map;
    }


}
