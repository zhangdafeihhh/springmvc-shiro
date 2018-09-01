package com.zhuanche.request;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
  * @description: 处理接口请求参数封装类
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

public class CommonRequest implements Serializable{

    private static final long serialVersionUID = -3621783801383952963L;

    private Set<String> cityIds;

    private Set<String> supplierIds;

    private Set<Integer> teamIds;

    private Set<Integer> permOfCity;//普通管理员可以管理的所有城市ID

    private Set<Integer> permOfSupplier;//普通管理员可以管理的所有供应商ID

    private Set<Integer> permOfTeam;//普通管理员可以管理的所有车队ID

    private String cityId;

    private String supplierId;

    private Integer teamId;

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

    public Set<Integer> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(Set<Integer> teamIds) {
        this.teamIds = teamIds;
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

    public Set<Integer> getPermOfCity() {
        return permOfCity;
    }

    public void setPermOfCity(Set<Integer> permOfCity) {
        this.permOfCity = permOfCity;
    }

    public Set<Integer> getPermOfSupplier() {
        return permOfSupplier;
    }

    public void setPermOfSupplier(Set<Integer> permOfSupplier) {
        this.permOfSupplier = permOfSupplier;
    }

    public Set<Integer> getPermOfTeam() {
        return permOfTeam;
    }

    public void setPermOfTeam(Set<Integer> permOfTeam) {
        this.permOfTeam = permOfTeam;
    }
}
