package com.zhuanche.serv.busManage;

import java.util.Date;

import mapper.mdbcarmanage.BusBizChangeLogMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.constants.BusConst;
import com.zhuanche.entity.mdbcarmanage.BusBizChangeLog;
import com.zhuanche.shiro.session.WebSessionUtil;

import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper;
import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;

/**
 * @ClassName: BusCarBizDriverInfoService
 * @Description:
 * @author: yanyunpeng
 * @date: 2018年12月7日 下午7:17:15
 * 
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BusBizChangeLogService implements BusConst {

	private static Logger logger = LoggerFactory.getLogger(BusBizChangeLogService.class);

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusBizChangeLogExMapper busBizChangeLogExMapper;

	@Autowired
	private BusBizChangeLogMapper busBizChangeLogMapper;

	/**
	 * @Title: insertLog
	 * @Description: 保存操作记录
	 * @param businessType
	 * @param businessKey
	 * @param updateDate
	 * @return 
	 * @return int
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER))
	public int insertLog(BusinessType businessType, String businessKey,String description, Date updateDate) {
		try {
			if (businessType == null || StringUtils.isBlank(businessKey)) {
				return 0;
			}
			BusBizChangeLog log = new BusBizChangeLog();
			if (updateDate == null) {
			    updateDate = new Date();
			}
			log.setBusinessType(businessType.businessType());
			log.setBusinessKey(businessKey);
			log.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
			log.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());
			log.setUpdateDate(updateDate);
			log.setDescription(description);
			return busBizChangeLogExMapper.insertLog(log);
		} catch (Exception e) {
			logger.error("[ BusBizChangeLogService-insertLog ] 保存操作记录异常,errorMsg={}", e.getMessage(), e);
		}
		return 0;
	}
	/**
	 * @Title: insertLog
	 * @Description: 保存操作记录
	 * @param businessType
	 * @param businessKey
	 * @param updateDate
	 * @return
	 * @return int
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER))
	public int insertLog(BusinessType businessType, String businessKey,String description,Integer updateBy,String updateName, Date updateDate) {
		try {
			if (businessType == null || StringUtils.isBlank(businessKey)) {
				return 0;
			}
			BusBizChangeLog log = new BusBizChangeLog();
			if (updateDate == null) {
				updateDate = new Date();
			}
			log.setBusinessType(businessType.businessType());
			log.setBusinessKey(businessKey);
			log.setUpdateBy(updateBy);
			log.setUpdateName(updateName);
			log.setUpdateDate(updateDate);
			log.setCreateDate(updateDate);
			log.setDescription(description);
			return busBizChangeLogMapper.insert(log);
		} catch (Exception e) {
			logger.error("[ BusBizChangeLogService-insertLog ] 保存操作记录异常,errorMsg={}", e.getMessage(), e);
		}
		return 0;
	}

}
