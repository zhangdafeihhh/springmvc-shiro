package com.zhuanche.entity.driver.appraisa;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: answer
 * @Date: 2018/11/13 10 46
 * @Descreption:
 */
@Setter
@Getter
public class UpdateAppraisalVo implements Serializable {
    private String modifyName;
    private Integer modifyBy;
    private Integer appraisalId;
    private Integer status;
    private String orderNo;

}
