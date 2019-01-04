package com.zhuanche.constants.busManage;

import com.zhuanche.util.dateUtil.DateUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 巴士涉及的部分常量
 * @author: niuzilian
 * @create: 2018-11-27 11:48
 **/
public class BusConstant {
    /**
     * 导入时车辆信息
     */
    public static String ERROR_CAR_KEY="bus:errmsg:car:";
    /**
     * 导入时司机信息
     */
    public static String ERROR_DRIVER_KEY="bus:errmsg:driver:";
    /**
     * 导入时车辆和司机信息 失效时间 15 分钟
     */
    public static int ERROR_IMPORT_KEY_EXPIRE=900;

    /**
     * 导出订单时每页查询的条数
     */
    public interface CarConstant{
        String FILE_NAME="巴士信息";
       // String EXPORT_HEAD="车牌号,城市,供应商,车型类别,具体车型,是否有效,创建时间";
        String[] TEMPLATE_HEAD={"城市","供应商","车牌号","车型类别名称","车辆颜色","燃料类别","运输证字号","车辆厂牌","具体车型（选填）","下次车检时间（选填）","下次维保时间（选填）","下次运营证检测时间（选填）","购买时间（选填）"};
    }
    public interface DriverMaidConstant{
        String MAID_FILE_NAME="分佣明细";
        String MAID_EXPORT_HEAD="订单号,城市名称,手机号,结算时间,订单总金额,预付金额,代收金额,高速费,停车费,住宿费,餐饮费,抽佣比例（%）,司机实际收入";
        String DRAW_FILE_NAME="提现记录";
        String DRAW_EXPORT_HEAD="账户名称,手机号,城市名称,结算时间,到账金额,银行卡号,银行名称";
        String ACCOUNT_FILE_NAME="账户余额";
        String ACCOUNT_EXPORT_HEAD="账户名称,手机号,城市名称,可提现金额,冻结金额,累计提现";
    }
    public interface SupplierMaidConstant{
        String BILL_FILE_NAME="供应商账单列表";
        String BILL_EXPORT_HEAD="供应商编号,供应商名称,城市,业务类型,账单编号,账单开始日期,账单结束日期,到期付款日期,账单金额,结算方式,分佣方式,分佣类型,账单状态";
        String TRANSACTION_FLOW_FILE_NAME="交易流水";
        String TRANSACTION_FALOW_FALE_NAME="来源编号,结算金额,发生时间,结算类型";
        //账单状态
         enum EnumStatus{
          INITIAL(0,"初始"),WAIT_PROOFREAD_BILL(1,"待对账"),WAIT_FRO_INVOICE(2,"待开票"),WAIT_PAYMENT(3,"待支付"),COMPLETE(4,"已完成")  ;
            // 0 初始 1 待对账 2 待开票 3 待付款 4 已完成
            private int code;
            private String desc;

            EnumStatus(int code, String desc) {
                this.code = code;
                this.desc = desc;
            }

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }
            public static String getDescByCode(Integer code){
                for (EnumStatus status:EnumStatus.values()) {
                    if(status.code==code){
                        return status.desc;
                    }
                }
                return StringUtils.EMPTY;
            }
        }
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

    //写一些共有的静态方法
    public static String buidFileName (HttpServletRequest request, String name) throws UnsupportedEncodingException {
        String fileName = name + com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), DateUtil.intTimestampPattern) + ".csv";
        //获得浏览器信息并转换为大写
        String agent = request.getHeader("User-Agent").toUpperCase();
        //IE浏览器和Edge浏览器
        if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {  //其他浏览器
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
        }
        return fileName;
    }

}
