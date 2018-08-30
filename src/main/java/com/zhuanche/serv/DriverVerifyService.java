package com.zhuanche.serv;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.DriverVerifyDto;
import com.zhuanche.entity.driver.DriverVerify;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;

import mapper.driver.ex.DriverVerifyExMapper;

/**
 * 司机加盟审核服务层 ClassName: DriverVerifyService.java Date: 2018年8月29日
 * 
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 */
@Service
public class DriverVerifyService {

	private static final Logger logger = LoggerFactory.getLogger(DriverVerifyService.class);

	@Autowired
	DriverVerifyExMapper driverVerifyExMapper;

	/** 查询司机加盟注册信息 **/
	public PageDTO queryDriverVerifyList(int page, int pageSize, Long cityId, String supplier, String mobile,
			Integer verifyStatus, String createDateBegin, String createDateEnd) {

		// 数据权限设置
		Set<Integer> cityIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的所有城市ID
		Set<Integer> supplierIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的所有供应商ID
		if (!WebSessionUtil.isSupperAdmin()) {// 非超级管理员
			// 获取当前登录用户信息
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
			cityIdsForAuth = currentLoginUser.getCityIds();
			supplierIdsForAuth = currentLoginUser.getSupplierIds();
			if (cityIdsForAuth.size() == 0 || cityId != null && !cityIdsForAuth.contains(cityId)) {
				return null;
			}
			if (supplierIdsForAuth.size() == 0 || StringUtils.isNotBlank(supplier) && !supplierIdsForAuth.contains(supplier)) {
				return null;
			}
		}
		// 查询参数设置
		Set<Long> cityIds = new HashSet<Long>();
		Set<String> supplierIds = new HashSet<String>();
		// 城市权限
		if (null != cityId) {
			cityIds.add(cityId);
		} else {
			for (Integer cityid : cityIdsForAuth) {
				cityIds.add(cityid.longValue());
			}
		}
		// 供应商权限
		if (StringUtils.isNotBlank(supplier)) {
			supplierIds.add(supplier);
		} else {
			for (Integer supplierId : supplierIdsForAuth) {
				supplierIds.add(String.valueOf(supplierId));
			}
		}
		// 分页查询司机加盟信息
		PageDTO pageDto = new PageDTO();
		int total = 0;
		List<DriverVerify> driverVerifyList = null;
		PageInfo<DriverVerify> pageInfo = null;
		try {
			pageInfo = PageHelper.startPage(page, pageSize, true).doSelectPageInfo(() -> driverVerifyExMapper
					.queryDriverVerifyList(cityIds, supplierIds, mobile, verifyStatus, createDateBegin, createDateEnd));
			total = (int) pageInfo.getTotal();
			driverVerifyList = pageInfo.getList();
			if (null == driverVerifyList || driverVerifyList.size() <= 0) {
				return pageDto;
			}
			// 将分页查询结果转成dto
			List<DriverVerifyDto> dtos = BeanUtil.copyList(driverVerifyList, DriverVerifyDto.class);
			pageDto.setResult(dtos);
			pageDto.setTotal(total);
		} catch (Exception e) {
			logger.error("查询司机加盟注册信息异常", e);
		} finally {
			PageHelper.clearPage();
		}
		return pageDto;
	}
}
