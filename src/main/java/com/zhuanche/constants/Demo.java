package com.zhuanche.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2019-01-03 18:04
 **/
public class Demo {
    public static void main(String[] args) {
        String[] TEMPLATE_HEAD={"城市","供应商","车牌号","车型类别名称","车辆颜色","燃料类别","运输证字号","车辆厂牌","具体车型（选填）","下次车检时间（选填）","下次维保时间（选填）","下次运营证检测时间（选填）","购买时间（选填）"};
        String join = StringUtils.join(TEMPLATE_HEAD, ",");
        System.out.println(join);

    }
}
