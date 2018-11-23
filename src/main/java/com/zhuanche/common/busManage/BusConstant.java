package com.zhuanche.common.busManage;

/**
 * @program: mp-manage
 * @description: 巴士车辆保存记录默认值
 * @author: niuzilian
 * @create: 2018-11-23 20:48
 **/
public class BusConstant {
    /**
     * 巴士车辆保存某些字段的默认值(经过产品确认)
     *  属性值跟car_biz_car_info表保持一致，方便查找问题
     */
    public interface BusSaveDefaultProperty{
        /**
         下次车检时间（可以为空）
         下次维保时间(可以为空字段)
         下次运营检测时间（可以为空）
         下次治安证检测时间（可以为空）
         下次等级验证时间（可以为空）
         二级维护时间（可以为空）
         租赁到期时间（可以为空）
         购买时间（可以为空）
         */
        /**
         * 车辆类型（以机动车行驶证为主）
         */
        String vehicle_type = "car54856656";
        /**
         * 车辆注册日期
         */
        String vehicle_registration_date="2012-5-10";
        /**
         * 网络预约出租汽车运输证发证机构
         */
        String certificationAuthority = "道路运输管理局";
        /**
         * 经营区域
         */
        String operatingRegion = "全国";
        /**
         * 网络预约出租汽车运输证有效期起
         */
        String transportNumberDateStart ="2016-6-1";
        /**
         * 网络预约出租汽车运输证有效期止
         */
        String transportNumberDateEnd = "2026-6-1";
        /**
         * 网约车初次登记日期
         */
        String firstDate = "2016-6-1";
        /**
         * 车辆检修状态（合格）
         */
        int overhaulStatus = 1;
        /**
         * 年度审验状态（合格）
         */
        int auditingStatus = 1;
        /**
         * 车辆年度审验日期
         */
        String auditing_date = "2016-6-1";
        /**
         * 网约车发票打印设备序列号
         */
        String equipmentNumber = "asf54asd8564688";
        /**
         * 卫星定位装置品牌
         */
        String gpsBrand = "brand";
        /**
         * 卫星定位装置型号
         */
        String gpsType = "vasmd";
        /**
         * 卫星定位装置IMEI号
         */
        String gps_imei ="imeiv2015";
        /**
         * 卫星定位装置安装日期
         */
        String gpsDate = "2016-6-1";
    }
}
