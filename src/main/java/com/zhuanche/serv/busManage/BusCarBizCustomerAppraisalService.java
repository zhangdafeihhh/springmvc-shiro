package com.zhuanche.serv.busManage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;

import mapper.rentcar.ex.BusCarBizCustomerAppraisalExMapper;

/**
 * @ClassName: BusCarBizCustomerAppraisalService
 * @Description: 查询司机评分
 * @author: yanyunpeng
 * @date: 2018年12月11日 下午6:08:56
 * 
 */
@Service
public class BusCarBizCustomerAppraisalService {

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusCarBizCustomerAppraisalExMapper busCarBizCustomerAppraisalExMapper;

	/**
	 * @Title: queryAppraisal
	 * @Description: 查询司机某月评分
	 * @param param
	 * @return 
	 * @return CarBizCustomerAppraisalStatistics
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public CarBizCustomerAppraisal queryAppraisal(String orderNo) {
		CarBizCustomerAppraisal appraisal = busCarBizCustomerAppraisalExMapper.queryAppraisal(orderNo);
		return appraisal;
	}
	
	/**
	 * @Title: getScore
	 * @Description: 获取司机某月评分
	 * @param driverId
	 * @param date
	 * @return 
	 * @return String
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public String getScore(String orderNo) {
		// 司机评分
		CarBizCustomerAppraisal appraisal = this.queryAppraisal(orderNo);
		String average = appraisal == null ? null : appraisal.getEvaluateScore();
		if (average == null && appraisal != null) {
			double num = 0d;
			int count = 0;
			try {
				Double instrumentAndServiceNum = 0d;
				if ((instrumentAndServiceNum = Double.valueOf(appraisal.getInstrumentAndService())) != 0) {
					num += instrumentAndServiceNum;
					count++;
				}
			} catch (NumberFormatException e) {
			}
			try {
				Double environmentAndEquippedNum = 0d;
				if ((environmentAndEquippedNum = Double.valueOf(appraisal.getEnvironmentAndEquipped())) != 0) {
					num += environmentAndEquippedNum;
					count++;
				}
			} catch (NumberFormatException e) {
			}
			try {
				Double efficiencyAndSafetyNum = 0d;
				if ((efficiencyAndSafetyNum = Double.valueOf(appraisal.getEfficiencyAndSafety())) != 0) {
					num += efficiencyAndSafetyNum;
					count++;
				}
			} catch (NumberFormatException e) {
			}
			if (count != 0) {
				average = String.valueOf(num / count);
			}
		}
		return average;
	}
}
