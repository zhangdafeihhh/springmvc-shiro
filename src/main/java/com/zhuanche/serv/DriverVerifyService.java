package com.zhuanche.serv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.entity.driver.DriverVerify;

import mapper.driver.ex.DriverVerifyExMapper;

/** 
 * 司机加盟审核服务层
 * ClassName: DriverVerifyService.java 
 * Date: 2018年8月29日 
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 */
@Service
public class DriverVerifyService {

	@Autowired
	DriverVerifyExMapper driverVerifyExMapper;
	
	/**查询司机加盟注册信息**/
	public List<DriverVerify> queryDriverVerifyList(Long cityId,String supplier,String mobile,Integer verifyStatus,String createDateBegin,String createDateEnd){
		return driverVerifyExMapper.queryDriverVerifyList(cityId,supplier,mobile,verifyStatus,createDateBegin,createDateEnd);
	}
}
