package com.zhuanche.request;

import java.util.Set;

/**
  * @description: 接口请求参数封装类
  *
  * <PRE>
  * <BR>	修改记录
  * <BR>-----------------------------------------------
  * <BR>	修改日期			修改人			修改内容
  * </PRE>
  *
  * @author lunan
  * @version 1.0
  * @since 1.0
  * @create: 2018-08-30 10:10
  *
*/

public class DriverTeamRequest extends PageRequest{

    private static final long serialVersionUID = -6293630291498667107L;

    private Integer id;

    private Integer pId;

    private String cityId;

    private String supplierId;

    private Integer teamId;

    private String teamName;

    private Set<String> driverIds;

    private Set<String> cityIds;

    private Set<String> supplierIds;

    //车牌号(可多个)
    private String plates;

    private String license;

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getPlates() {
        return plates;
    }

    public void setPlates(String plates) {
        this.plates = plates;
    }

    public Set<String> getCityIds() {
        return cityIds;
    }

    public void setCityIds(Set<String> cityIds) {
        this.cityIds = cityIds;
    }

    public Set<String> getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(Set<String> supplierIds) {
        this.supplierIds = supplierIds;
    }

    public Set<String> getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(Set<String> driverIds) {
        this.driverIds = driverIds;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}
