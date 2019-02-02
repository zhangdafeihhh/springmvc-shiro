package com.zhuanche.mongo;

import lombok.Data;

@Data
public class UserOperationDTO {
    private String userName;
    private int id;
    private int count;
    private int dailyCount;
}
