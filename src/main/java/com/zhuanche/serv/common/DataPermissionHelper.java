package com.zhuanche.serv.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

/**对于普通管理员，查询其拥有的数据权限**/
@Service
public class DataPermissionHelper{
	
	/**查询一个普通管理员可以管理的所有城市ID**/
	public Set<Integer> havePermOfCityIds( String ssoLoginName ){
		//TODO 需要查库完成
		return new HashSet<Integer>( Arrays.asList(new Integer[] {10000,20000,300000}) );
	}

	/**查询一个普通管理员可以管理的所有供应商ID**/
	public Set<Integer> havePermOfSupplierIds( String ssoLoginName ){
		//TODO 需要查库完成
		return new HashSet<Integer>(Arrays.asList(new Integer[] {10000,20000,300000}) );
	}
	
	/**查询一个普通管理员可以管理的所有车队ID**/
	public Set<Integer> havePermOfDriverTeamIds( String ssoLoginName ){
		//TODO 需要查库完成
		return new HashSet<Integer>(Arrays.asList(new Integer[] {10000,20000,300000}) );
	}

}