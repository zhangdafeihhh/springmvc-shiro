package com.zhuanche.dto.rentcar;

import lombok.Data;

@Data
public class DriverIncomeScorePage {
    private int pageNo;
    private int pageSize;
    private int total;
    private boolean hasNext;
}
