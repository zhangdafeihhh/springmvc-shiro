package com.zhuanche.serv.busManage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalStatistics;

import mapper.rentcar.ex.BusCarBizCustomerAppraisalStatisticsExMapper;

/**
 * @ClassName: BusCarBizCustomerAppraisalStatisticsService
 * @Description: 查询司机评分
 * @author: yanyunpeng
 * @date: 2018年12月11日 下午6:08:56
 * 
 */
@Service
public class BusCarBizCustomerAppraisalStatisticsService {

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusCarBizCustomerAppraisalStatisticsExMapper busCarBizCustomerAppraisalStatisticsExMapper;

	/**
	 * @Title: queryAppraisal
	 * @Description: 查询司机某月评分
	 * @param param
	 * @return 
	 * @return CarBizCustomerAppraisalStatistics
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public CarBizCustomerAppraisalStatistics queryAppraisal(Map<Object, Object> param) {
		CarBizCustomerAppraisalStatistics appraisal = busCarBizCustomerAppraisalStatisticsExMapper.queryAppraisal(param);
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
	public String getScore(Integer driverId, LocalDate date) {
		// 司机评分
		Map<Object, Object> param = new HashMap<>();
		param.put("driverId", driverId);
		param.put("createDate", DateTimeFormatter.ofPattern("y-M").format(date));// 处理成字符串
		CarBizCustomerAppraisalStatistics appraisal = this.queryAppraisal(param);
		String average = appraisal == null ? null : appraisal.getEvaluateScore();
		if (average == null) {
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
