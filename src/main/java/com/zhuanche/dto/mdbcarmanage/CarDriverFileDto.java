package com.zhuanche.dto.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarDriverFile;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CarDriverFileDto extends CarDriverFile {
    private Set<Integer> cityIds = new HashSet<Integer>();
    /**此用户可以管理的供应商ID**/
    private Set<Integer> supplierIds = new HashSet<Integer>();
    /**此用户可以管理的车队ID**/
    private Set<Integer> teamIds = new HashSet<Integer>();
    /**此用户可以管理的班组ID***/
    private Set<Integer> groupIds = new HashSet<Integer>();
}
