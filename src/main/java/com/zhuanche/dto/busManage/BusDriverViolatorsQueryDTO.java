package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * @ClassName: BusDriverViolatorsQueryDTO
 * @Description: 违规司机处理查询DTO
 * @author: tianye
 * @date: 2019年03月25日 下午17:10:15
 * 
 */
public class BusDriverViolatorsQueryDTO extends BaseDTO {

    private String busDriverName;//巴士司机姓名

    private String busDriverPhone;//巴士司机手机号

    private Integer cityId;//巴士司机所属城市id

    private Integer supplierId;//巴士司机所属供应商id

    private Short punishStatus;//处罚状态（0：正常 1：停运 2：冻结）

    public String getBusDriverName() {return busDriverName; }

    public void setBusDriverName(String busDriverName) {this.busDriverName = busDriverName;}

    public String getBusDriverPhone() {return busDriverPhone;}

    public void setBusDriverPhone(String busDriverPhone) {this.busDriverPhone = busDriverPhone;}

    public Integer getCityId() {return cityId;}

    public void setCityId(Integer cityId) {this.cityId = cityId;}

    public Integer getSupplierId() {return supplierId;}

    public void setSupplierId(Integer supplierId) {this.supplierId = supplierId;}

    public Short getPunishStatus() {return punishStatus;}

    public void setPunishStatus(Short punishStatus) {this.punishStatus = punishStatus;}
}
