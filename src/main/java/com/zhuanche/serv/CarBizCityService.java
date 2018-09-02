package com.zhuanche.serv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mapper.rentcar.CarBizCityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.entity.rentcar.CarBizCity;

import mapper.rentcar.ex.CarBizCityExMapper;
/**城市信息 的 基本服务层**/
@Service
public class CarBizCityService{
	@Autowired
    private CarBizCityExMapper carBizCityExMapper;

    @Autowired
    private CarBizCityMapper carBizCityMapper;

	/**根据CITYID查询出城市信息**/
	public Map<Integer, CarBizCity> queryCity( Set<Integer> cityIds ){
		if(cityIds==null || cityIds.size()==0) {
			return new HashMap<Integer, CarBizCity>(4);
		}
		List<CarBizCity> list = carBizCityExMapper.queryByIds(cityIds);
		if(list==null||list.size()==0) {
			return new HashMap<Integer, CarBizCity>(4);
		}
		Map<Integer, CarBizCity> result = new HashMap<Integer, CarBizCity>(  cityIds.size()*2 );
		for(CarBizCity c : list) {
			result.put(c.getCityId(), c);
		}
		return result;
	}

	/**
	 * 根据城市ID，查询城市名称
	 * @param cityId
	 * @return
	 */
	public CarBizCity selectByPrimaryKey(Integer cityId){
		return carBizCityMapper.selectByPrimaryKey(cityId);
	}
}