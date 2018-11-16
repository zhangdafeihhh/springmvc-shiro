package com.zhuanche.serv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.zhuanche.entity.rentcar.CarBizSupplier;

import mapper.rentcar.CarBizSupplierMapper;
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
        Set<Integer> cityIds = Sets.newHashSet();
        cityIds.add(cityId);
		List<CarBizSupplier> list = carBizSupplierExMapper.querySuppliers(cityIds, supplierids);
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

    public List<CarBizSupplier> findByIdSet(Set<Integer> supplierIdSet) {
		return carBizSupplierMapper.findByIdSet(supplierIdSet);
    }
}