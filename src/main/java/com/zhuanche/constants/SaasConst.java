package com.zhuanche.constants;

/**常量定义**/
public final class SaasConst {
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
	
}
