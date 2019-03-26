
package com.zhuanche.constants.busManage;
/**
 * ClassName: EnumOrder 
 * date: 2017年9月4日 上午11:15:17 
 *
 * @author zhulingling
 * @version
 * @since JDK 1.6 
 */
public enum EnumOrder {

    ORDER_YUDINGZHONG(10100, "预定中/待支付"),
    ORDER_YISHOULI(10103, "支付成功/待接单"),
    ORDER_DAIFUWU(10105, "待服务"),
    ORDER_YICHUFA(10200, "已出发"),
    ORDER_YIDAODA(10205, "已到达"),
    ORDER_FUWUZHONG(10300, "服务中"),
    ORDER_FUWUJIESHU(10305, "服务结束"),
    ORDER_DAIJIESUAN(10400, "待结算"),
    ORDER_ZHIFUZHONG(10402,"支付中"),
    ORDER_KOUKUANZHONG(10403,"扣款中"),
    ORDER_DAIZHIFU(10404,"后付"),
    ORDER_YIJIESUAN(10405, "已结算"),
    ORDER_YIWANCHENG(10500, "已完成"),
    ORDER_YIYI(10505, "订单异议"),
    ORDER_YIQUXIAO(10600, "已取消");

    private int value;
    private String i18n;

    EnumOrder(int value, String i18n) {
        this.value = value;
        this.i18n = i18n;
    }

    public String getI18n() {
        return this.i18n;
    }

    public String getValueStr() {
        return String.valueOf(value);
    }
    // 普通方法
    public static String getDis(int value) {
        for (EnumOrder status : EnumOrder.values()) {
            if (status.value == value) {
                return status.getI18n();
            }
        }
        return null;
    }
}