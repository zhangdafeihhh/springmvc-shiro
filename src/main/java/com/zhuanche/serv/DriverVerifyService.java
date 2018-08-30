package com.zhuanche.serv;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.DriverVerifyDto;
import com.zhuanche.entity.driver.DriverVerify;
import com.zhuanche.serv.common.DataPermissionHelper;
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
	private DataPermissionHelper dataPermissionHelper;
	
	@Autowired
	DriverVerifyExMapper driverVerifyExMapper;

	/** 查询司机加盟注册信息 **/
	public PageDTO queryDriverVerifyList(int page, int pageSize, Long cityId, String supplier, String mobile,
			Integer verifyStatus, String createDateBegin, String createDateEnd) {

		// 数据权限设置
		Set<Integer> permOfCity        = new HashSet<Integer>();//普通管理员可以管理的所有城市ID
		Set<Integer> permOfSupplier = new HashSet<Integer>();//普通管理员可以管理的所有供应商ID
		if(!WebSessionUtil.isSupperAdmin()) {
			permOfCity        = dataPermissionHelper.havePermOfCityIds("");
			permOfSupplier = dataPermissionHelper.havePermOfSupplierIds("");
		}
		// 分页查询司机加盟信息
		PageDTO pageDto = new PageDTO();
		int total = 0;
		List<DriverVerify> driverVerifyList = null;
		PageInfo<DriverVerify> pageInfo = null;
		try {
			pageInfo = PageHelper.startPage(page, pageSize, true).doSelectPageInfo(() -> driverVerifyExMapper
					.queryDriverVerifyList(cityId, supplier, mobile, verifyStatus, createDateBegin, createDateEnd));
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
