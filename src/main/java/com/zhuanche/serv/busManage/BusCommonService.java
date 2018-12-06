package com.zhuanche.serv.busManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.session.WebSessionUtil;

import mapper.rentcar.ex.BusCarBizSupplierExMapper;

/**
 * @ClassName: BusCommonService
 * @Description: 巴士公用接口服务类
 * @author: yanyunpeng
 * @date: 2018年12月6日 上午10:25:09
 * 
 */
@Service
public class BusCommonService {

	private static final Logger logger = LoggerFactory.getLogger(BusCommonService.class);

	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

	public List<CarBizSupplier> querySuppliers(Integer cityId) {
		// 数据权限控制SSOLoginUser
		Set<Integer> authOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
		Set<Integer> authOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID

		Map<String, Object> param = new HashMap<>();
		param.put(null, null);
		
		List<CarBizSupplier> suppliers = busCarBizSupplierExMapper.querySuppliers(param);
		return suppliers;
	}

}
