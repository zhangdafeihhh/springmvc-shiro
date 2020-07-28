package com.zhuanche.entity.query;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/***
 * @Description: 数据库查询基类
 * @Author: zjw
 * @Date: 2020/5/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseQuery implements Serializable {


	private static final long serialVersionUID = 1L;

	/**
	 * 此用户可以管理的城市ID
	 **/
	private Set<Integer> cityIds;
	/**
	 * 此用户可以管理的运力商ID
	 **/
	private Set<Integer> supplierIds;
	/**
	 * 此用户可以管理的车队ID
	 **/
	private Set<Integer> teamIds;
	/**
	 * 此用户可以管理的班组ID
	 ***/
	private Set<Integer> groupIds;
}
