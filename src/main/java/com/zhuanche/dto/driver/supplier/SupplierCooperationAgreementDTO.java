package com.zhuanche.dto.driver.supplier;

import com.zhuanche.entity.driver.SupplierCooperationAgreementUrl;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SupplierCooperationAgreementDTO {
    private Long id;

    /**
     * 供应商id
     */
    private Integer supplierId;

    /**
     * 协议编号
     */
    private String agreementNumber;

    /**
     * 协议开始时间
     */
    private Date agreementStartTime;

    /**
     * 协议到期时间
     */
    private Date agreementEndTime;

    /**
     * 保证金金额
     */
    private Double marginAmount;

    /**
     * 保证金打款账户
     */
    private String marginAccount;

    /**
     * 创建人id
     */
    private Integer createBy;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 最后更新时间
     */
    private Date updateDate;

    private List<SupplierCooperationAgreementUrl> agreementUrlList;

}