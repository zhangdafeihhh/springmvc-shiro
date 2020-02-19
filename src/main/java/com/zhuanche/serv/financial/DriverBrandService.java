package com.zhuanche.serv.financial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.zhuanche.entity.driver.DriverBrand;
import mapper.driver.ex.DriverBrandExMapper;

/**  
 * ClassName:DriverBrandService <br/>  
 * Date:     2019年4月25日 下午3:05:14 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Service
public class DriverBrandService {
	@Autowired
	private DriverBrandExMapper driverBrandExMapper;
	
	private static Map<Long, DriverBrand> driverBrandMap = new HashMap<>();
	private static List<DriverBrand> driverBrandList = new ArrayList<>();

	@PostConstruct
	private void init() {
		driverBrandList = driverBrandExMapper.queryDriverBrandList();
		for (DriverBrand driverBrand : driverBrandList) {
			driverBrandMap.put(driverBrand.getId(), driverBrand);
		}
	}
	@Scheduled(cron="0 */1 * * * ?")   //每分钟执行一次  
	private void refreshCarBrandData(){
		this.init();
	}
	/**
	 * 缓存中获取品牌集合
	 * @return
	 */
	public List<DriverBrand> getAllDriverBrandList() {
			List<DriverBrand> driverBrands = new ArrayList<DriverBrand>();
			driverBrands.addAll(driverBrandList);
			return driverBrands;
	}
	/**
	 * 缓存中获取单个品牌信息
	 */
	public DriverBrand getDriverBrandByPrimaryKey(Long id) {
			DriverBrand driverBrand = null;
			driverBrand = driverBrandMap.get(id);
			return driverBrand;
	}
	
	
	public List<DriverBrand> queryDriverBrandList() {
		List<DriverBrand> list=driverBrandExMapper.queryDriverBrandList();
		return list;
	}

	public DriverBrand queryDriverBrandByName(String brandName) {
			return driverBrandExMapper.queryDriverBrandByName(brandName);
	}
}
  
