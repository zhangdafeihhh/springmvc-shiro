package com.zhuanche.constants;

import java.util.HashSet;
import java.util.Set;

/**常量定义**/
public final class SaasConst {
	/**默认的初始密码**/
	public static final String INITIAL_PASSWORD             = "12345678";
	/**手机号码验证正则表达式**/
	public static final String MOBILE_REGEX                    = "^1(3|4|5|6|7|8|9)[0-9]{9}$";
	/**账号的正则表达式**/
	public static final String ACCOUNT_REGEX                = "^[a-zA-Z0-9_\\-]{3,30}$";
	/**电子邮箱的正则表达式**/
	public static final String EMAIL_REGEX                       = "^[A-Za-z0-9]+([-_\\.][A-Za-z0-9]+)*@([-A-Za-z0-9]+[\\.])+[A-Za-z0-9]+$";
	
	/**权限类型**/
	public static final class PermissionType{
		/**权限类型：菜单**/
		public static final Byte MENU             = 0;
		/**权限类型：按钮**/
		public static final Byte BUTTON         = 1;
		/**权限类型：数据区域**/
		public static final Byte DATA_AREA    = 2;
	}
	/**菜单打开模式**/
	public static final class MenuOpenMode{
		/**菜单打开模式：原窗体**/
		public static final Byte CURRENT_WINDOW    = 0;
		/**菜单打开模式：新窗体**/
		public static final Byte NEW_WINDOW            = 1;
	}
	
	/**返回的权限数据格式**/
	public static final class PermissionDataFormat{
		/**返回的权限数据格式：列表**/
		public static final String LIST    = "list";
		/**返回的权限数据格式：树形**/
		public static final String TREE  = "tree";
	}

	/**系统预置角色**/
	public static String           SYSTEM_ROLE             = "mp_manage_super_admin";
	/**系统预置权限**/
	public static Set<String> SYSTEM_PERMISSIONS  = new HashSet<String>( 64 );
	static{
		//----------------------------------------------------系统管理
		SYSTEM_PERMISSIONS.add("SYSTEM_MANAGEMENT");
		//----------------------------------------------------用户管理
		SYSTEM_PERMISSIONS.add("USER_MANAGEMENT");
		SYSTEM_PERMISSIONS.add("ADD_USER");                          //增加一个用户
		SYSTEM_PERMISSIONS.add("DISABLE_USER");                     //禁用一个用户
		SYSTEM_PERMISSIONS.add("ENABLE_USER");                     //启用一个用户
		SYSTEM_PERMISSIONS.add("CHANGE_USER");                    //修改一个用户
		SYSTEM_PERMISSIONS.add("GET_ALL_ROLEIDS_OF_USER"); //查询一个用户中的角色ID
		SYSTEM_PERMISSIONS.add("SAVE_ROLEIDS_OF_USER");     //保存一个用户中的角色ID
		SYSTEM_PERMISSIONS.add("QUERY_USER_LIST");              //查询用户列表
		SYSTEM_PERMISSIONS.add("RESET_USER_PASSWORD");    //重置用户密码
		//----------------------------------------------------角色管理
		SYSTEM_PERMISSIONS.add("ROLE_MANAGEMENT");
		SYSTEM_PERMISSIONS.add("ADD_SAAS_ROLE");                     //增加一个角色
		SYSTEM_PERMISSIONS.add("DISABLE_SAAS_ROLE");                //禁用一个角色
		SYSTEM_PERMISSIONS.add("ENABLE_SAAS_ROLE");                //启用一个角色
		SYSTEM_PERMISSIONS.add("CHANGE_SAAS_ROLE");               //修改一个角色
		SYSTEM_PERMISSIONS.add("GET_ALL_ROLE_PERMISSIONS");   //查询一个角色中的权限
		SYSTEM_PERMISSIONS.add("GET_PERMISSIONIDS_OF_ROLE"); //查询一个角色中的权限ID
		SYSTEM_PERMISSIONS.add("SAVE_ROLE_PERMISSIONIDS");    //保存一个角色中的权限ID
		SYSTEM_PERMISSIONS.add("QUERY_SAAS_ROLE_LIST");          //查询角色列表
		//----------------------------------------------------权限管理
		SYSTEM_PERMISSIONS.add("PERMISSION_MANAGENT");
		SYSTEM_PERMISSIONS.add("ADD_SAAS_PERMISSION");                 //增加一个权限
		SYSTEM_PERMISSIONS.add("DISABLE_SAAS_PERMISSION");            //禁用一个权限
		SYSTEM_PERMISSIONS.add("ENABLE_SAAS_PERMISSION");             //启用一个权限
		SYSTEM_PERMISSIONS.add("CHANGE_SAAS_PERMISSION");           //修改一个权限
		SYSTEM_PERMISSIONS.add("GET_ALL_SAAS_PERMISSIONS_INFO");  //查询所有的权限信息
	}
	

	
	
	
//	public static void main( String[] args ) {
//		boolean res = "5,66,465,0,46".matches("^([0-9]+,)*[0-9]+$");
//		System.out.println( res  );
//	}
	
}
