package com.zhuanche.entity.mdbcarmanage;

/**
 * 分表策略实现类
 * 在这个类中可以定义多个表的分表策略
 **/
public class ShardTableStrategy{
//	/**用户操作日志表(按月分表)**/
//	@ShardTableConfig(tablename="user_operation_log",moduleClass=UserOperationLog.class)
//	public String user_operation_log(UserOperationLog po){
//		return DateUtil.format(po.getCreateTime(), "yyyyMM" );
//	}
//	/**投资人活期子账户金额变化日志表(按月分表)**/
//	@ShardTableConfig(tablename="sub_account_log",moduleClass=SubAccountLog.class)
//	public String shardSubAccountLog(SubAccountLog log){
//		return DateUtil.format(log.getCreateTime(), "yyyyMMdd" );
//	}
//	/**分润表(按天分表)**/
//	@ShardTableConfig(tablename="daliy_user_project_profit",moduleClass=DaliyUserProjectProfit.class)
//	public String daliy_user_project_profit(DaliyUserProjectProfit po){
//		return DateUtil.format(log.getCreateTime(), "yyyyMMdd" );
//	}

	/**用户操作记录日志表（按月分表）**/
//	@ShardTableConfig(tablename="sfc_check_log",moduleClass=SfcCheckLog.class)
//	public String shardSfcCheckLog(SfcCheckLog sfcCheckLog){
//		return new SimpleDateFormat("yyyyMM").format(date);
//	}
}