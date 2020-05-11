package com.zhuanche.dto.bigdata;

import com.zhuanche.entity.bigdata.BiDriverMeasureDay;

import java.util.HashSet;
import java.util.Set;

public class BiDriverMeasureDayDto extends BiDriverMeasureDay {
    private Set<Integer> cityIds = new HashSet<Integer>();
    /**此用户可以管理的供应商ID**/
    private Set<Integer> supplierIds = new HashSet<Integer>();
    /**此用户可以管理的车队ID**/
    private Set<Integer> teamIds = new HashSet<Integer>();
    /**此用户可以管理的班组ID***/
    private Set<Integer> groupIds = new HashSet<Integer>();
}
