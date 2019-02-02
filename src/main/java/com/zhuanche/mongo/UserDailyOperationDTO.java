package com.zhuanche.mongo;

import lombok.Data;

@Data
public class UserDailyOperationDTO {
    private int count;
    private int userCount;
    private int avgCount;
    private String date;

}
