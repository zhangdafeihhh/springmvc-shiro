package com.zhuanche.dto.bigdata;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class DisinfectPenetranceDTO {
    /**
     * 大消渗透率
     */
    private BigDecimal penetrance = BigDecimal.ZERO;
}
