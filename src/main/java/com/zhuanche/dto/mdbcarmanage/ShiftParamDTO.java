package com.zhuanche.dto.mdbcarmanage;

import java.io.Serializable;

/**
  * @description: 整合属性DTO
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
  * @create: 2018-09-05 15:33
  *
*/

public class ShiftParamDTO implements Serializable{

    private static final long serialVersionUID = 2617089720360928486L;

    /** 属性PO类id*/
    private Integer id;

    private Integer groupId;

    private Integer supplierId;

    private Integer driverId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }
}
