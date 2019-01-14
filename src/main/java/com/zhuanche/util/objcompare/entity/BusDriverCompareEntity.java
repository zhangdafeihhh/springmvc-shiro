package com.zhuanche.util.objcompare.entity;

import com.zhuanche.util.objcompare.FieldNote;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;

/**
 * @program: mp-manage
 * @description: 对比操作记录
 * @author: niuzilian
 * @create: 2019-01-14 18:04
 **/
public class BusDriverCompareEntity {
    @FieldNote("司机姓名")
    private String name;
    @FieldNote("电话号码")
    private String phone;
    @FieldNote("车牌号")
    private String licensePlates;
    @FieldNote("状态")
    private String status;
    @FieldNote("身份证号")
    private String idCardNo;
    @FieldNote("imei")
    private String  imei;
    @FieldNote("形象")
    private String isImage;
    @FieldNote("城市名称")
    private String cityName;
    @FieldNote("供应商名称")
    private String supplierName;
    @FieldNote("服务类型")
    private String groupName;
    @FieldNote("加盟类型")
    private String cooperationTypeName;
    @FieldNote("创建人")
    private String createName;
    @FieldNote("创建时间")
    private Date createDate;
    @FieldNote("修改人")
    private String updateName;
    @FieldNote("修改时间")
    private Date updateDate;
    @FieldNote("")
    private String teamName;

}
