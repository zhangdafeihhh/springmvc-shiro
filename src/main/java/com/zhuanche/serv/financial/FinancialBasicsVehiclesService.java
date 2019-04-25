package com.zhuanche.serv.financial;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constants.financial.FinancialConst.EnableStatusSelect;
import com.zhuanche.dto.financial.FinancialBasicsVehiclesDTO;
import com.zhuanche.entity.driver.DriverVehicle;
import com.zhuanche.entity.driver.FinancialBasicsVehicles;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;

import mapper.driver.FinancialBasicsVehiclesMapper;
import mapper.driver.ex.FinancialBasicsVehiclesExMapper;

/**  
 * ClassName:FinancialBasicsVehiclesService <br/>  
 * Date:     2019年4月23日 下午6:53:20 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Service
public class FinancialBasicsVehiclesService {
    private static final Logger logger = LoggerFactory.getLogger(FinancialBasicsVehiclesService.class);
	@Autowired
	private FinancialBasicsVehiclesMapper financialBasicsVehiclesMapper;
	@Autowired
	private FinancialBasicsVehiclesExMapper financialBasicsVehiclesExMapper;
	@Autowired
	private DriverVehicleService driverVehicleService;
	public PageDTO queryFinancialBasicsVehiclesForList(Integer page, Integer pageSize, String vehiclesDetailedName,
			Integer energyType) {
		if(page==null || page.intValue()<=0) {
			page = new Integer(1);
		}
		if(pageSize==null || pageSize.intValue()<=0) {
			pageSize = new Integer(10);
		}
		//执行SQL查询
    	int total = 0;
    	List<FinancialBasicsVehiclesDTO> financialBasicsVehiclesDtos=null;
    	Page p = PageHelper.startPage( page, pageSize, true );
    	try{
    		financialBasicsVehiclesDtos=financialBasicsVehiclesExMapper.queryFinancialBasicsVehiclesForList(vehiclesDetailedName,energyType);
        	total  = (int)p.getTotal();
    	}catch (Exception e) {
    		logger.error("查询车型库信息失败",e);
		}finally {
        	PageHelper.clearPage();
    	}
    	//判断返回结果
    	if(financialBasicsVehiclesDtos==null || financialBasicsVehiclesDtos.size()==0) {
    		return new PageDTO(page, pageSize, total, new ArrayList());
    	}
    	
    	for (int i = 0; i < financialBasicsVehiclesDtos.size(); i++) {
    		FinancialBasicsVehiclesDTO financialBasicsVehiclesDto = financialBasicsVehiclesDtos.get(i);
    		DriverVehicle driverVehicle = driverVehicleService.getDriverVehicleByPrimaryKey(financialBasicsVehiclesDto.getModelId());
    		if (driverVehicle!=null) {
    			financialBasicsVehiclesDto.setBrandName(driverVehicle.getBrandName());
        		financialBasicsVehiclesDto.setVehicleName(driverVehicle.getVehicleName());
			}
		}
    	//返回
    	return new PageDTO(page,pageSize,total,financialBasicsVehiclesDtos);
    	
	}

	public FinancialBasicsVehicles saveFinancialBasicsVehicles(FinancialBasicsVehicles financialBasicsVehicles) {
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		Date now=new Date();
		financialBasicsVehicles.setCreateBy(user.getName());
		financialBasicsVehicles.setUpdateBy(user.getName());
		financialBasicsVehicles.setCreateTime(now);
		financialBasicsVehicles.setUpdateTime(now);
		financialBasicsVehicles.setEnableStatus(EnableStatusSelect.ENABLESTATUS);
		int i=financialBasicsVehiclesMapper.insertSelective(financialBasicsVehicles);
		return financialBasicsVehicles;
	}

	public FinancialBasicsVehicles updateFinancialBasicsVehicles(FinancialBasicsVehicles financialBasicsVehicles) {
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		financialBasicsVehicles.setUpdateBy(user.getName());
		int i=financialBasicsVehiclesMapper.updateByPrimaryKeySelective(financialBasicsVehicles);
		return financialBasicsVehicles;
	}

	public FinancialBasicsVehiclesDTO queryFinancialBasicsVehiclesById(Integer basicsVehiclesId) {
		FinancialBasicsVehicles financialBasicsVehicles=financialBasicsVehiclesMapper.selectByPrimaryKey(basicsVehiclesId);
		FinancialBasicsVehiclesDTO financialBasicsVehiclesDTO=BeanUtil.copyObject(financialBasicsVehicles, FinancialBasicsVehiclesDTO.class);
		DriverVehicle driverVehicle = driverVehicleService.getDriverVehicleByPrimaryKey(financialBasicsVehiclesDTO.getModelId());
		if (driverVehicle!=null) {
			financialBasicsVehiclesDTO.setBrandName(driverVehicle.getBrandName());
			financialBasicsVehiclesDTO.setVehicleName(driverVehicle.getVehicleName());
		}
		return financialBasicsVehiclesDTO;
	}

}
  
