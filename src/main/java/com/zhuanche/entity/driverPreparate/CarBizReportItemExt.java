package com.zhuanche.entity.driverPreparate;

import java.io.Serializable;

public class CarBizReportItemExt implements Serializable{

    private static final long serialVersionUID = -6156706828620156500L;
    /**
     * 扩展表id
     */
    private Integer id;
    /**
     * 报备id
     */
    private Integer reportId;
    /**
     * 报备项id
     */
    private Integer itemId;
    /**
     * 分类id
     */
    private Integer typeId;

    private String typeName;

    private String itemName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}