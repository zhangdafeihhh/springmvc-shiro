package com.zhuanche.dto.driver;

import com.zhuanche.entity.mdbcarmanage.CarDriverDayDuty;

/**
  * @description: 查看司机排班返回封装类
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
  * @create: 2018-09-01 15:48
  *
*/

public class CarDriverDayDutyDTO extends CarDriverDayDuty {

    private String phone; // 司机手机号

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
