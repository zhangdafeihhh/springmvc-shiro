package com.zhuanche.constants.busManage;

/**
 * @program: mp-manage
 * @description: 巴士涉及的部分常量
 * @author: niuzilian
 * @create: 2018-11-27 11:48
 **/
public class BusConstant {
    public interface CarConstant{
        String EXPORT_HEAD="车牌号,城市,供应商,车型类别,具体车型,是否有效,创建时间";
        String[] TEMPLATE_HEAD={"车牌号","车型类别名称","车辆颜色","燃料类别","运输证字号","车辆厂牌","具体车型（选填）","下次车检时间（选填）","下次维保时间（选填）","下次运营证检测时间（选填）","购买时间（选填）"};
    }




    /**
     * 巴士车辆保存某些字段的默认值(经过产品确认)
     *  属性值跟car_biz_car_info表保持一致，方便查找问题
     */
    public  interface BusSaveDefaultProperty{
        /**
         * 车辆类型（以机动车行驶证为主）
         */
        String vehicle_type = "car54856656";
        /**
         * 车辆注册日期
         */
        String vehicle_registration_date="2016-06-01";
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
        String transportNumberDateStart ="2016-06-01";
        /**
         * 网络预约出租汽车运输证有效期止
         */
        String transportNumberDateEnd = "2026-06-01";
        /**
         * 网约车初次登记日期
         */
        String firstDate = "2016-06-01";
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
        String auditing_date = "2016-06-01";
        /**
         * 网约车发票打印设备序列号
         */
        String equipmentNumber = "ASF54ASD8564688";
        /**
         * 卫星定位装置品牌
         */
        String gpsBrand = "BRAND";
        /**
         * 卫星定位装置型号
         */
        String gpsType = "VASMD";
        /**
         * 卫星定位装置IMEI号
         */
        String gps_imei ="IMEIV2015";
        /**
         * 卫星定位装置安装日期
         */
        String gpsDate = "2016-06-01";
        //==============================
        /*发动机编号*/
        String engine_no = "L620HF00011";
        /*发动机排量*/
        String vehicleEngineDisplacement="800";
        /**发动机功率*/
        String vehicle_engine_power = "100";
        /*车架号*/
        String frame_no ="LA9LA2E3XFBBFC272";
        /*车辆轴距*/
        String vehicle_engine_wheelbase= "2200";
        /*下次车检时间*/
        String next_inspect_date = "2020-06-01";
        /*下次维保时间*/
        String next_maintenance_date ="2020-06-01";
        /*运营证检测时间*/
        String next_operation_date = "2020-06-01";
        /*下次治安证检测时间*/
        String next_security_date = "2020-06-01";
        /*下次等级检测时间*/
        String next_class_date = "2020-06-01";
        /**二次维保时间*/
        String two_level_maintenance_date ="2020-06-01";
        /**租赁到期时间*/
        String rental_expire_date = "2020-06-01";
        /*VIN码*/
        String vehicle_VIN_code = "LA9LA2E3XFBBFC272";
    }
}
