package com.zhuanche.constants.financial;

/**
 * ClassName:FinancialConst <br/>
 * Date: 2019年4月23日 上午11:06:16 <br/>
 * 
 * @author baiyunlong
 * @version 1.0.0
 */
public final class FinancialConst {
	
	public static final class EnableStatusSelect {
		/**
		 *启用
		 */
		public static final Byte ENABLESTATUS = 1;
		/**
		 *停用
		 */
		public static final Byte DISCONTINUE = 0;
	}
	/**
	 * 销售对象 ClassName: salesTargetSelect <br/>
	 * Function: TODO ADD FUNCTION. <br/>
	 * Reason: TODO ADD REASON(可选). <br/>
	 * date: 2019年4月23日 下午2:20:43 <br/>
	 * 
	 * @author baiyunlong
	 * @version FinancialConst
	 */
	public static final class SalesTargetSelect {
		/**
		 * TOC
		 */
		public static final Byte SALES_TARGET_TOC = 1;
		/**
		 * TOB
		 */
		public static final Byte SALES_TARGET_TOB = 2;
	}

	/**
	 * 商品类型 ClassName: goodsTypeSelect <br/>
	 * Function: TODO ADD FUNCTION. <br/>
	 * Reason: TODO ADD REASON(可选). <br/>
	 * date: 2019年4月23日 下午2:23:13 <br/>
	 * 
	 * @author baiyunlong
	 * @version FinancialConst
	 */
	public static final class GoodsTypeSelect {
		/**
		 * 融资
		 */
		public static final Byte GOODS_TYPE_FINANCING = 1;
		/**
		 * 经租
		 */
		public static final Byte GOODS_TYPE_RENT = 2;
	}
	
	/**能源类型  纯电、混动、汽油、柴油
	 * ClassName: ENERGY_TYPE_SELECT <br/>  
	 * Function: TODO ADD FUNCTION. <br/>  
	 * Reason: TODO ADD REASON(可选). <br/>  
	 * date: 2019年4月23日 下午2:27:02 <br/>  
	 * @author baiyunlong  
	 * @version FinancialConst
	 */
	public static final class EnergyTypeSelect {
		/**
		 * 纯电
		 */
		public static final int ENERGY_TYPE_ELECTRICITY = 1;
		/**
		 * 混动
		 */
		public static final int ENERGY_TYPE_MIXING = 2;
		/**
		 * 汽油
		 */
		public static final int ENERGY_TYPE_GAAOLINE = 3;
		/**
		 * 柴油
		 */
		public static final int ENERGY_TYPE_DIESEL_OIL = 4;
		
	}
	
	/**商品状态
	 * ClassName: goodsState <br/>  
	 * Function: TODO ADD FUNCTION. <br/>  
	 * Reason: TODO ADD REASON(可选). <br/>  
	 * date: 2019年4月23日 下午2:39:39 <br/>  
	 * @author baiyunlong  
	 * @version FinancialConst
	 */
	public static final class GoodsState{
		/**
		 * 已上架
		 */
		public static final Byte STAY_ON_THE_SHELF = 0;
		/**
		 * 已下架
		 */
		public static final Byte ON_SHELVES  = 1;
		/**
		 * 已删除
		 */
		public static final Byte DELETE      = 2;
	}

	public static final class ClueStatus{
		/**
		 * 待分发
		 */
		public static final Byte UNTREATED = 0;
		/**
		 * 已分发
		 */
		public static final Byte PROCESSED = 1;
		
	}
	
}
