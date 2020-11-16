package com.zhuanche.dto.mdbcarmanage;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author fanht
 * @Description
 * @Date 2020/11/3 下午2:55
 * @Version 1.0
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class InterDriverLineRelDto {

    private Integer id;

    private Integer userId;

    private JSONArray jsonDriver;

    private JSONArray jsonLine;

}
