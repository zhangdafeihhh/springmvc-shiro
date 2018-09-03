package com.zhuanche.serv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zhuanche.entity.rentcar.CarBizCity;
import mapper.rentcar.CarBizCityMapper;
import mapper.rentcar.CarBizSupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.entity.rentcar.CarBizSupplier;

import mapper.rentcar.ex.CarBizSupplierExMapper;
/**供应商信息 的 基本服务层**/
@Service
public class CarBizSupplierService{
	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;

	@Autowired
	private CarBizSupplierMapper carBizSupplierMapper;

	/**查询供应商信息**/
	public Map<Integer, CarBizSupplier> querySupplier( Integer cityId,  Set<Integer> supplierids ){
		List<CarBizSupplier> list = carBizSupplierExMapper.querySuppliers(cityId, supplierids);
		if(list==null||list.size()==0) {
			return new HashMap<Integer, CarBizSupplier>(4);
		}
		Map<Integer, CarBizSupplier> result = new HashMap<Integer, CarBizSupplier>();
		for(CarBizSupplier c : list) {
			result.put(c.getSupplierId(),  c);
		}
		return result;
	}

    public CarBizSupplier queryForObject(CarBizSupplier carBizSupplier){
	    return carBizSupplierExMapper.queryForObject(carBizSupplier);
    }

	/**
	 * 根据供应商ID查询供应商信息
	 * @param supplierId
	 * @return
	 */
	public CarBizSupplier selectByPrimaryKey(Integer supplierId){
		return carBizSupplierMapper.selectByPrimaryKey(supplierId);
	}
}