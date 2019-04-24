package com.zhuanche.serv.financial;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constants.financial.FinancialConst.EnableStatusSelect;
import com.zhuanche.dto.financial.FinancialBasicsVehiclesDTO;
import com.zhuanche.entity.driver.FinancialBasicsVehicles;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

import mapper.driver.FinancialBasicsVehiclesMapper;

/**  
 * ClassName:FinancialBasicsVehiclesService <br/>  
 * Date:     2019年4月23日 下午6:53:20 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Service
public class FinancialBasicsVehiclesService {
	@Autowired
	private FinancialBasicsVehiclesMapper financialBasicsVehiclesMapper;
	
	
	public PageDTO queryFinancialBasicsVehiclesForList(Integer page, Integer pageSize, String vehiclesDetailedName,
			Integer energyType) {
		return null;
	}

	public int saveFinancialBasicsVehicles(FinancialBasicsVehicles financialBasicsVehicles) {
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		Date now=new Date();
		financialBasicsVehicles.setCreateBy(user.getName());
		financialBasicsVehicles.setUpdateBy(user.getName());
		financialBasicsVehicles.setCreateTime(now);
		financialBasicsVehicles.setUpdateTime(now);
		financialBasicsVehicles.setEnableStatus(EnableStatusSelect.ENABLESTATUS);
		int i=financialBasicsVehiclesMapper.insertSelective(financialBasicsVehicles);
		return i;
	}

	public int updateFinancialBasicsVehicles(FinancialBasicsVehicles financialBasicsVehicles) {
		  
		return 0;
	}

	public FinancialBasicsVehiclesDTO queryFinancialBasicsVehiclesById(Integer basicsVehiclesId) {
		  
		return null;
	}

}
  
