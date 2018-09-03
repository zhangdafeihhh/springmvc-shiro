package com.zhuanche.shiro.constants;

/**
 * @program: car-manage
 * @description: 巴士相关常量
 * @author: admin
 * @create: 2018-08-16 10:39
 **/
public class BusConstant {
    /**
     * 车辆导入导出的excel
     */
    public interface BusExcel {
        //表头
        String[] BUS_EXCLE_TITLE = {"序号", "城市", "供应商", "车牌号", "车辆厂牌", "核定载客数", "颜色", "车辆燃料类型", "运输证字号", "车型"};
        //sheet名
        String SHEET_NAME = "车辆信息";
        //文件名
        String FILE_NAME = "IMPORTBUSINFO";
        String FILE_POSTFIX = ".xls";

        String[] BUS_EXCLE_DATA_TITLE = {"订单ID", "订单号", "下单时间", "预定用车时间", "城市", "服务类别", "车型类别", "订单类别", "乘车人数量", "行李数数量", "预定人手机号", "乘车人", "乘车人手机号", "车牌号", "完成时间", "实际上车时间", "实际上车地点", "实际下车地点", "单程", "状态", "取消原因", "总支付金额", "预收费用", "代收费用", "违约金额", "优惠金额", "供应商", "订单来源", "预订人", "实际里程", "实际时长", "预收支付方式", "代收支付方式", "司机姓名", "司机手机号", "停车费", "高速费", "其他费", "预付费支付时间", "代收费支付时"};
        //sheet名
        String SHEET_DATA_NAME = "订单明细";
        String FILE_DATA_NAME="bus_order_detail";
    }

    /**
     * 巴士订单导出
     */
    public interface ExportOrderList {
        /**
         * 订单组
         */
        String ORDER_LIST = "/busOrder/exportOrderList";
        /**
         * 计费组
         */
        String COST_LIST = "/buss/getBusCostDetailList";
        /**
         * 支付组
         */
        String PAY_LIST = "/pay/details/bus/list";
    }

}
