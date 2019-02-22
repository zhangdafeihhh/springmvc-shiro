package com.zhuanche.common.enums;

import com.zhuanche.constant.MenuConstants;

public enum MenuEnum {

    //权限管理接口
    PERMISSION_ADD(MenuConstants.SYSTEM_MANAGE, MenuConstants.PERMISSION_MANAGEMENT, MenuConstants.PERMISSION_LIST, "增加一个权限"),
    PERMISSION_DISABLE(MenuConstants.SYSTEM_MANAGE, MenuConstants.PERMISSION_MANAGEMENT, MenuConstants.PERMISSION_LIST, "禁用一个权限"),
    PERMISSION_ENABLE(MenuConstants.SYSTEM_MANAGE, MenuConstants.PERMISSION_MANAGEMENT, MenuConstants.PERMISSION_LIST, "启用一个权限"),
    PERMISSION_UPDATE(MenuConstants.SYSTEM_MANAGE, MenuConstants.PERMISSION_MANAGEMENT, MenuConstants.PERMISSION_LIST, "修改一个权限"),
    PERMISSION_DELETE(MenuConstants.SYSTEM_MANAGE, MenuConstants.PERMISSION_MANAGEMENT, MenuConstants.PERMISSION_LIST, "删除一个权限"),
    PERMISSION_LIST(MenuConstants.SYSTEM_MANAGE, MenuConstants.PERMISSION_MANAGEMENT, MenuConstants.PERMISSION_LIST, "权限列表查询"),
    //角色管理接口
    ROLE_ADD(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "增加一个角色"),
    ROLE_DISABLE(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "禁用一个角色"),
    ROLE_ENABLE(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "启用一个角色"),
    ROLE_UPDATE(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "修改一个角色"),
    ROLE_LIST(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "查询角色列表"),
    ROLE_DELETE(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "删除一个角色"),
    ROLE_PERMISSION_LIST(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "查询一个角色中的权限"),
    ROLE_PERMISSION_IDS(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "查询一个角色中的权限ID"),
    ROLE_PERMISSION_SAVE(MenuConstants.SYSTEM_MANAGE, MenuConstants.ROLE_MANAGEMENT, MenuConstants.ROLE_LIST, "保存一个角色中的权限ID"),
    //用户管理接口
    USER_ADD(MenuConstants.SYSTEM_MANAGE, MenuConstants.USER_MANAGEMENT, MenuConstants.USER_LIST, "增加一个用户"),
    USER_DISABLE(MenuConstants.SYSTEM_MANAGE, MenuConstants.USER_MANAGEMENT, MenuConstants.USER_LIST, "禁用一个用户"),
    USER_ENABLE(MenuConstants.SYSTEM_MANAGE, MenuConstants.USER_MANAGEMENT, MenuConstants.USER_LIST, "启用一个用户"),
    USER_UPDATE(MenuConstants.SYSTEM_MANAGE, MenuConstants.USER_MANAGEMENT, MenuConstants.USER_LIST, "修改一个用户"),
    USER_ROLE_LIST(MenuConstants.SYSTEM_MANAGE, MenuConstants.USER_MANAGEMENT, MenuConstants.USER_LIST, "查询一个用户中的角色ID"),
    USER_ROLE_SAVE(MenuConstants.SYSTEM_MANAGE, MenuConstants.USER_MANAGEMENT, MenuConstants.USER_LIST, "保存一个用户中的角色ID"),
    USER_LIST(MenuConstants.SYSTEM_MANAGE, MenuConstants.USER_MANAGEMENT, MenuConstants.USER_LIST, "查看用户列表"),
    USER_RESET_PASSWORD(MenuConstants.SYSTEM_MANAGE, MenuConstants.USER_MANAGEMENT, MenuConstants.USER_LIST, "重置用户密码"),
    //加盟商管理接口
    SUPPLIER_LIST(MenuConstants.CAR_MANAGE, MenuConstants.SUPPLIER_MANAGE, MenuConstants.SUPPLIER_MANAGE, "加盟商查询"),
    SUPPLIER_ADD(MenuConstants.CAR_MANAGE, MenuConstants.SUPPLIER_MANAGE, MenuConstants.SUPPLIER_MANAGE, "新增加盟商"),
    SUPPLIER_UPDATE(MenuConstants.CAR_MANAGE, MenuConstants.SUPPLIER_MANAGE, MenuConstants.SUPPLIER_MANAGE, "修改加盟商"),
    SUPPLIER_DETAIL(MenuConstants.CAR_MANAGE, MenuConstants.SUPPLIER_MANAGE, MenuConstants.SUPPLIER_MANAGE, "加盟商详情"),
    SUPPLIER_COOPERATION_TYPE(MenuConstants.CAR_MANAGE, MenuConstants.SUPPLIER_MANAGE, MenuConstants.SUPPLIER_MANAGE, "加盟商加盟类型"),
    SUPPLIER_CHECK_NAME(MenuConstants.CAR_MANAGE, MenuConstants.SUPPLIER_MANAGE, MenuConstants.SUPPLIER_MANAGE, "加盟商名称是否存在"),
    //司机加盟管理接口
    DRIVER_JOIN_PROMOTE_LIST(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_PROMOTE, "加盟司机信息查询"),
    DRIVER_JOIN_PROMOTE_DETAIL(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_PROMOTE, "加盟司机信息详情查看"),
    DRIVER_JOIN_PROMOTE_EXPORT(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_PROMOTE, "加盟司机信息导出"),
    DRIVER_JOIN_PROMOTE_INVITE(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_PROMOTE, "重置用户密码"),
    DRIVER_JOIN_PROMOTE_IMAGE(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_PROMOTE, "加盟司机证件信息查询"),
    DRIVER_JOIN_PROMOTE_RECORD(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_PROMOTE, "加盟司机记录查询"),

    DRIVER_JOIN_APPLY_LIST(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_APPLY, "司机加盟申请查询"),
    DRIVER_JOIN_APPLY_DELETE(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_APPLY, "司机加盟申请删除"),
    DRIVER_JOIN_APPLY_TEMPLATE_DOWNLOAD(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_APPLY, "司机加盟申请模板下载"),
    DRIVER_JOIN_APPLY_IMPORT(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_APPLY, "司机加盟申请导入"),
    DRIVER_JOIN_APPLY_UPDATE_INFO(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_APPLY, "司机加盟申请详情查询"),
    DRIVER_JOIN_APPLY_UPDATE(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_APPLY, "司机加盟申请修改"),
    DRIVER_JOIN_APPLY_ADD(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_APPLY, "司机加盟申请新增"),
    DRIVER_JOIN_APPLY_LICENSE_PLATES(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_JOIN_MANAGE, MenuConstants.DRIVER_JOIN_APPLY, "司机加盟申请加盟商车牌查询"),
    //司机信息管理接口
    DRIVER_INFO_LIST(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_LIST, "司机信息查询"),
    DRIVER_INFO_LIST_EXPORT(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_LIST, "司机信息导出"),
    DRIVER_INFO_LIST_DETAIL(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_LIST, "司机信息详情"),
    DRIVER_INFO_LIST_RESET_IMEI(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_LIST, "重置司机IMEI"),
    DRIVER_INFO_LIST_IMPORT(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_LIST, "司机信息导入"),

    DRIVER_INFO_CHANGE_APPLY_LIST(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_CHANGE_APPLY, "司机信息/车辆修改申请查询"),
    DRIVER_INFO_CHANGE_APPLY(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_CHANGE_APPLY, "司机信息修改申请导入"),
    DRIVER_INFO_CHANGE_APPLY_DETAIL(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_CHANGE_APPLY, "司机信息/车辆修改申请详情"),
    DRIVER_INFO_CHANGE_APPLY_ADD(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_INFO_MANAGE, MenuConstants.DRIVER_INFO_CHANGE_APPLY, "司机信息修改申请新增"),

    //司机停运管理
    DRIVER_TEMPORARY_STOP_MANAGE(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_STOP_MANAGE, MenuConstants.DRIVER_TEMPORARY_STOP_MANAGE, "司机临时停运查询"),

    DRIVER_FOREVER_STOP_MANAGE(MenuConstants.CAR_MANAGE, MenuConstants.DRIVER_STOP_MANAGE, MenuConstants.DRIVER_FOREVER_STOP_MANAGE, "司机永久停运查询"),

    //车辆加盟管理接口
    CAR_JOIN_APPLY_LIST(MenuConstants.CAR_MANAGE, MenuConstants.CAR_JOIN_MANAGE, MenuConstants.CAR_JOIN_MANAGE_APPLY, "车辆加盟申请查询"),
    CAR_JOIN_APPLY_IMPORT(MenuConstants.CAR_MANAGE, MenuConstants.CAR_JOIN_MANAGE, MenuConstants.CAR_JOIN_MANAGE_APPLY, "车辆加盟申请导入"),
    CAR_JOIN_APPLY_DETAIL(MenuConstants.CAR_MANAGE, MenuConstants.CAR_JOIN_MANAGE, MenuConstants.CAR_JOIN_MANAGE_APPLY, "车辆加盟申请详情"),
    CAR_JOIN_APPLY_ADD(MenuConstants.CAR_MANAGE, MenuConstants.CAR_JOIN_MANAGE, MenuConstants.CAR_JOIN_MANAGE_APPLY, "车辆加盟申请新增"),
    CAR_JOIN_APPLY_DELETE(MenuConstants.CAR_MANAGE, MenuConstants.CAR_JOIN_MANAGE, MenuConstants.CAR_JOIN_MANAGE_APPLY, "车辆加盟申请删除"),
    CAR_JOIN_APPLY_UPDATE(MenuConstants.CAR_MANAGE, MenuConstants.CAR_JOIN_MANAGE, MenuConstants.CAR_JOIN_MANAGE_APPLY, "车辆加盟申请修改"),
    CAR_JOIN_APPLY_TEMPLATE_DOWNLOAD(MenuConstants.CAR_MANAGE, MenuConstants.CAR_JOIN_MANAGE, MenuConstants.CAR_JOIN_MANAGE_APPLY, "车辆加盟申请导入模板下载"),

    //车辆信息管理接口
    CAR_INFO_MANAGE_LIST(MenuConstants.CAR_MANAGE, MenuConstants.CAR_INFO_MANAGE, MenuConstants.CAR_INFO_LIST, "车辆信息查询"),
    CAR_INFO_MANAGE_EXPORT(MenuConstants.CAR_MANAGE, MenuConstants.CAR_INFO_MANAGE, MenuConstants.CAR_INFO_LIST, "车辆信息导出"),
    CAR_INFO_MANAGE_DETAIL(MenuConstants.CAR_MANAGE, MenuConstants.CAR_INFO_MANAGE, MenuConstants.CAR_INFO_LIST, "车辆信息详情"),

    CAR_INFO_CHANGE_APPLY_ADD(MenuConstants.CAR_MANAGE, MenuConstants.CAR_INFO_MANAGE, MenuConstants.CAR_INFO_CHANGE_APPLY, "车辆信息修改申请新增"),

    //司机工作管理接口
    DRIVER_DUTY_MANAGE_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_DUTY_MANAGE, "司机排班信息查询"),
    DRIVER_DUTY_MANAGE_IMPORT(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_DUTY_MANAGE, "司机排班信息导入"),
    DRIVER_DUTY_MANAGE_EXPORT(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_DUTY_MANAGE, "司机排班信息导出"),
    DRIVER_DUTY_MANAGE_TEMPLATE_DOWNLOAD(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_DUTY_MANAGE, "司机排班信息模板下载"),
    DRIVER_DUTY_MANAGE_UPDATE(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_DUTY_MANAGE, "司机排班信息更新"),

    DRIVER_WORK_REPORT_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_WORK_REPORT,"司机工作报告查询"),
    DRIVER_WORK_REPORT_EXPORT(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_WORK_REPORT,"司机工作报告导出"),
    DRIVER_WORK_REPORT_DETAIL(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_WORK_REPORT,"司机工作报告详情"),

    DRIVER_ATTENDANCE_REPORT_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_ATTENDANCE_REPORT,"司机考勤报告查询"),
    DRIVER_ATTENDANCE_REPORT_EXPORT(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_ATTENDANCE_REPORT,"司机考勤报告导出"),
    DRIVER_ATTENDANCE_REPORT_DETAIL(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_ATTENDANCE_REPORT,"司机考勤报告详情"),

    ORDER_PREPARATION_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.ORDER_PREPARATION,"订单报备查询"),
    ORDER_PREPARATION_DETAIL(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.ORDER_PREPARATION,"订单报备详情"),

    DRIVER_RANK_INTEGRAL_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_RANK_INTEGRAL,"司机等级积分查询"),
    DRIVER_RANK_INTEGRAL_EXPORT(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE, MenuConstants.DRIVER_RANK_INTEGRAL,"司机等级积分导出"),

    DRIVER_ACTION_ENUM(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE,MenuConstants.DRIVER_ACTION, "司机事件枚举列表"),
    DRIVER_ACTION_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.DRIVER_WORK_MANAGE,MenuConstants.DRIVER_ACTION, "司机事件列表查询"),
    //车队班组管理
    TEAM_GROUP_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_MANAGE,"车队班组信息查询"),
    TEAM_GROUP_ADD(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_MANAGE,"车队班组信息添加"),
    TEAM_GROUP_UPDATE(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_MANAGE,"车队班组信息修改"),
    TEAM_GROUP_ADD_DRIVER_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_MANAGE,"车队班组可添加司机列表"),
    TEAM_GROUP_ADD_DRIVER(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_MANAGE,"添加司机到车队班组"),
    TEAM_GROUP_DRIVER_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_MANAGE,"车队班组司机信息查询"),
    TEAM_GROUP_DRIVER_REMOVE(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_MANAGE,"车队班组司机移除"),

    TEAM_GROUP_DUTY_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_DUTY_MANAGE,"车队班组强制上班配置查询"),
    TEAM_GROUP_DUTY_SAVE(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_DUTY_MANAGE,"车队班组强制上班配置保存"),

    TEAM_GROUP_DURATION_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_DURATION_MANAGE,"车队排班时长查询"),
    TEAM_GROUP_DURATION_SAVE(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_DURATION_MANAGE,"车队排班时长保存"),
    TEAM_GROUP_DURATION_DETAIL(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.TEAM_GROUP_DURATION_MANAGE,"车队排班时长详情"),

    DRIVER_MUST_DUTY_TIME_FIELD(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.DRIVER_DUTY_TIME_MANAGE,"车队强制排班时间段查询"),
    DRIVER_DUTY_TIME_FIELD(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.DRIVER_DUTY_TIME_MANAGE,"车队排班时间段查询"),
    DRIVER_DUTY_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.DRIVER_DUTY_TIME_MANAGE,"车队排班司机列表查询"),
    DRIVER_DUTY_TIME_SAVE(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.DRIVER_DUTY_TIME_MANAGE,"车队司机日排班保存"),

    DRIVER_DUTY_PUBLISH_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.DRIVER_DUTY_PUBLISH,"司机排班列表查询"),
    DRIVER_DUTY_PUBLISH(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.DRIVER_DUTY_PUBLISH,"发布司机排班"),

    DRIVER_DUTY_EXPORT(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.TEAM_GROUP_MANAGE, MenuConstants.DRIVER_DUTY_LIST,"司机排班列表导出"),

    //车辆运行管理
    CAR_RUNNING_DRIVER_INFO(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.CAR_RUNNING_MANAGE, MenuConstants.CAR_TRAIL_MANAGE,"车牌号司机信息查询"),
    CAR_RUNNING_DRIVER_INDATE(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.CAR_RUNNING_MANAGE, MenuConstants.CAR_TRAIL_MANAGE,"司机指标汇总查询"),
    CAR_RUNNING_ORDER_LIST(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.CAR_RUNNING_MANAGE, MenuConstants.CAR_TRAIL_MANAGE,"轨迹订单列表"),
    CAR_RUNNING_TRAIL(MenuConstants.TRANSPORT_CAPACITY, MenuConstants.CAR_RUNNING_MANAGE, MenuConstants.CAR_TRAIL_MANAGE,"车辆轨迹查询"),


    //订单管理
    ORDER_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.ORDER_MANAGE, MenuConstants.ORDER_LIST,"订单明细查询"),
    ORDER_LIST_EXPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.ORDER_MANAGE, MenuConstants.ORDER_LIST,"订单明细导出"),
    ORDER_DETAIL(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.ORDER_MANAGE, MenuConstants.ORDER_LIST,"订单详情查询"),
    COMPLETE_ORDER_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.ORDER_MANAGE, MenuConstants.COMPLETE_ORDER_LIST,"完成订单明细查询"),
    COMPLETE_ORDER_LIST_EXPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.ORDER_MANAGE, MenuConstants.COMPLETE_ORDER_LIST,"完成订单明细导出"),
    CANCEL_ORDER_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.ORDER_MANAGE, MenuConstants.CANCEL_ORDER_LIST,"取消订单明细查询"),
    CANCEL_ORDER_LIST_EXPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.ORDER_MANAGE, MenuConstants.CANCEL_ORDER_LIST,"取消订单明细导出"),

    //风控管理
    RISK_ORDER_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.RISK_MANAGE, MenuConstants.RISK_ORDER,"风控订单列表查询"),
    RISK_ORDER_APPEAL(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.RISK_MANAGE, MenuConstants.RISK_ORDER,"风控订单申诉"),
    RISK_ORDER_DETAIL(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.RISK_MANAGE, MenuConstants.RISK_ORDER,"风控订单申诉详情查询"),

    //投诉评分
    ORDER_RANK_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.COMPLAIN_AND_RANK, MenuConstants.ORDER_RANK,"订单评分列表查询"),
    OFFLINE_ORDER_RANK_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.COMPLAIN_AND_RANK, MenuConstants.ORDER_RANK,"停运司机订单评分查询"),
    ORDER_RANK_EXPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.COMPLAIN_AND_RANK, MenuConstants.ORDER_RANK,"订单评分导出"),
    DRIVER_RANK_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.COMPLAIN_AND_RANK, MenuConstants.DRIVER_RANK,"司机评分列表查询"),
    DRIVER_RANK_EXPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.COMPLAIN_AND_RANK, MenuConstants.DRIVER_RANK,"司机评分导出"),
    DRIVER_RANK_DETAIL(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.COMPLAIN_AND_RANK, MenuConstants.DRIVER_RANK,"司机评分详情查询"),

    //统计分析
    DRIVER_ANALYSIS_DATA(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.DRIVER_ANALYSIS,"司机运营统计结果查询"),
    DRIVER_ANALYSIS_GRAPH(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.DRIVER_ANALYSIS,"司机运营统计图表查询"),
    CAR_DETAIL_ANALYSIS(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.CAR_ANALYSIS,"车辆分析趋势图明细查询"),
    CAR_TABLE_ANALYSIS(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.CAR_ANALYSIS,"车辆分析趋势表格查询"),
    DRIVER_EVALUATE_DETAIL(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.DRIVER_EVALUATE_DETAIL,"司机评价详情查询"),
    DRIVER_EVALUATE_EXPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.DRIVER_EVALUATE_DETAIL,"司机评价详情导出"),
    DRIVER_OPERATION_DETAIL(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.DRIVER_OPERATION_DETAIL,"司机运营详情查询"),
    DRIVER_OPERATION_EXPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.DRIVER_OPERATION_DETAIL,"司机运营详情导出"),
    DRIVER_WORK_ASSESSMENT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.DRIVER_WORK_ASSESSMENT,"司机工作考核列表查询"),
    DRIVER_WORK_ASSESSMENT_EXPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.STATISTIC_ANALYSIS, MenuConstants.DRIVER_WORK_ASSESSMENT,"司机工作考核列表导出"),

    REPORT_TYPE_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.DATA_REPORT, MenuConstants.DATA_REPORT_SUBSCRIPTION, "报表名称列表查询"),
    REPORT_CONFIGURE(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.DATA_REPORT, MenuConstants.DATA_REPORT_SUBSCRIPTION, "报表订阅配置查询"),
    REPORT_CONFIGURE_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.DATA_REPORT, MenuConstants.DATA_REPORT_SUBSCRIPTION, "报表订阅配置列表查询"),
    SUBSCRIPTION_REPORT(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.DATA_REPORT, MenuConstants.DATA_REPORT_SUBSCRIPTION, "数据报表订阅"),
    REPORT_DOWNLOAD_LIST(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.DATA_REPORT, MenuConstants.DATA_REPORT_DOWNLOAD, "订阅报表下载列表查询"),
    REPORT_DOWNLOAD(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.DATA_REPORT, MenuConstants.DATA_REPORT_DOWNLOAD, "加盟商名称是否存在"),

    PROBLEM_FEED_BACK_QUERY(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.PROBLEM_FEED_BACK,MenuConstants.PROBLEM_FEED_BACKS,"问题受理查询"),
    PROBLEM_FEED_BACK_ADD(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.PROBLEM_FEED_BACK,MenuConstants.PROBLEM_FEED_BACKS,"问题受理新增"),
    PROBLEM_FEED_BACK_MANAGE(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.PROBLEM_FEED_BACK,MenuConstants.PROBLEM_FEED_BACKS,"问题受理处理"),
    PROBLEM_FEED_BACK_DOWNLOAD(MenuConstants.OPERATION_MANAGEMENT, MenuConstants.PROBLEM_FEED_BACK,MenuConstants.PROBLEM_FEED_BACKS,"问题受理文件下载"),
    ;

    private String levelOne;
    private String levelTwo;
    private String levelThree;
    private String levelFour;

    MenuEnum(String levelOne, String levelTwo, String levelThree, String levelFour) {
        this.levelOne = levelOne;
        this.levelTwo = levelTwo;
        this.levelThree = levelThree;
        this.levelFour = levelFour;
    }

    public String getLevelOne() {
        return levelOne;
    }

    public String getLevelTwo() {
        return levelTwo;
    }

    public String getLevelThree() {
        return levelThree;
    }

    public String getLevelFour() {
        return levelFour;
    }
}
