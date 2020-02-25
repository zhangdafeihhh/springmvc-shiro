package com.zhuanche.serv.financial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zhuanche.entity.driver.DriverVehicle;

import mapper.driver.ex.DriverVehicleExMapper;

/**  
 * ClassName:DriverVehicleService <br/>  
 * Date:     2019年4月25日 下午3:09:06 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Service
public class DriverVehicleService {
	
	@Autowired
	private DriverVehicleExMapper driverVehicleExMapper;
	
	private static Map<Long, DriverVehicle> driverVehiclelMap = new HashMap<>();
	private static List<DriverVehicle> driverVehiclelList = new ArrayList<>();
	//brandId,车型编码集合
	private static Map<Long , List<DriverVehicle>> driverVehiclelListMap = new HashMap<>();

	@PostConstruct
	private void init() {
		driverVehiclelList         = driverVehicleExMapper.queryDriverVehicleList(null);
		driverVehiclelMap       = driverVehiclelList.stream().collect(Collectors.toMap(DriverVehicle::getId, o -> o ));
		driverVehiclelListMap  = driverVehiclelList.stream().collect(Collectors.groupingBy(DriverVehicle::getBrandId)); 
	}
	@Scheduled(cron="0 */1 * * * ?")   //每分钟执行一次  
	private void refreshCarBrandData(){
		this.init();
	}
	/**
	 * 根据品牌id获取车型
	 * @param brandId
	 * @return
	 */
	public List<DriverVehicle> getDriverVehicleListByBrandId(Long  brandId){
			List<DriverVehicle> list = new ArrayList<DriverVehicle>();
			if(null == brandId) {
				list.addAll(driverVehiclelList);
			}else{
				list = driverVehiclelListMap.get(brandId);
			}
			return list;
	}
	/**
	 * 缓存中获取单个车辆型号信息
	 */
	public DriverVehicle getDriverVehicleByPrimaryKey(Long modelId) {
			DriverVehicle driverVehiclel = null;
			driverVehiclel = driverVehiclelMap.get(modelId);
			return driverVehiclel;
	}
	
	
	public List<DriverVehicle> queryDriverVehicleList(Integer brandId) {
		List<DriverVehicle> list=driverVehicleExMapper.queryDriverVehicleList(brandId);
		return list;
	}

	public DriverVehicle queryByModelId(Integer modelId) {
		return driverVehicleExMapper.queryByModelId(modelId);
	}
}
  
