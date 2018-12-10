package com.zhuanche.serv.busManage;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class BusBizChangeLogService implements BusConst {

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusBizChangeLogExMapper busBizChangeLogExMapper;

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
	public int insertLog(BusinessType businessType, String businessKey, Date updateDate) {
		BusBizChangeLog log = new BusBizChangeLog();
		log.setBusinessType(businessType.businessType());
		log.setBusinessKey(businessKey);
		log.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
		log.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());
		if (updateDate == null) {
			log.setUpdateDate(new Date());
		}
		return busBizChangeLogExMapper.insertLog(log);
	}

}
