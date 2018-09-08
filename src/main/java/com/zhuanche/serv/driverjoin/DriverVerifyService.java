package com.zhuanche.serv.driverjoin;

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
import com.zhuanche.dto.driver.DriverVerifyDto;
import com.zhuanche.entity.driver.DriverVerify;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.IdCard;

import mapper.driver.ex.DriverCertExMapper;
import mapper.driver.ex.DriverVerifyExMapper;
import mapper.rentcar.CarBizCarGroupMapper;
import mapper.rentcar.CarBizModelMapper;

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

	@Autowired
	DriverCertExMapper driverCertExMapper;

	@Autowired
	CarBizCarGroupMapper carBizCarGroupMapper;
	
	@Autowired
	CarBizModelMapper carBizModelMapper;
	
	/** 查询司机加盟注册信息 **/
	public PageDTO queryDriverVerifyList(int page, int pageSize, Long cityId, String supplier, String mobile,
			Integer verifyStatus, String createDateBegin, String createDateEnd) {

		// 数据权限设置
		Set<Integer> cityIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的所有城市ID
		Set<Integer> supplierIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的所有供应商ID
		if (!WebSessionUtil.isSupperAdmin()) {// 非超级管理员
			// 获取当前登录用户信息
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
			if(null != currentLoginUser){
				cityIdsForAuth = currentLoginUser.getCityIds();
				supplierIdsForAuth = currentLoginUser.getSupplierIds();
			}
			if (cityIdsForAuth.size() > 0 && cityId != null && !cityIdsForAuth.contains(cityId)) {
				return null;
			}
			if (supplierIdsForAuth.size() > 0
					&& StringUtils.isNotBlank(supplier) && !supplierIdsForAuth.contains(supplier)) {
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
			if(cityIdsForAuth != null && cityIdsForAuth.size() >0){
				for (Integer cityid : cityIdsForAuth) {
					cityIds.add(cityid.longValue());
				}
			}
		}
		// 供应商权限
		if (StringUtils.isNotBlank(supplier)) {
			supplierIds.add(supplier);
		} else {
			if(supplierIdsForAuth != null && supplierIdsForAuth.size() >0 ){
				for (Integer supplierId : supplierIdsForAuth) {
					supplierIds.add(String.valueOf(supplierId));
				}
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

	/** 查询司机加盟注册信息通过司机id **/
	public DriverVerifyDto queryDriverVerifyById(Long driverId) {
		// 查询司机加盟注册信息通过id
		logger.info("查询司机加盟注册信息通过司机ID,driverId=" + driverId);
		DriverVerifyDto dto = null;
		try {
			DriverVerify driverVerify = driverVerifyExMapper.selectByPrimaryKey(driverId);
			if (null == driverVerify) {
				return null;
			}
			// 车牌号码转大写
			String plateNum = driverVerify.getPlateNum();
			if (StringUtils.isNotBlank(plateNum)) {
				driverVerify.setPlateNum(plateNum.toUpperCase());
			}
			// 获取司机的出生日期 性别
			if (driverVerify.getIdCard() != null && !"".equals(driverVerify.getIdCard())) {
				if (driverVerify.getIdCard() != null && !"".equals(driverVerify.getIdCard())) {
					String gender = IdCard.getGenderByIdCard(driverVerify.getIdCard());
					if (Integer.valueOf(gender) == 1) {
						driverVerify.setIdcardSex("男");
					} else {
						driverVerify.setIdcardSex("女");
					}
					String d = IdCard.getBirthByIdCard(driverVerify.getIdCard());
					if (d != null && d.length() == 8) {
						driverVerify.setIdcardBirthday(
								d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6, 8));
					} else {
						driverVerify.setIdcardBirthday(d);
					}
				}
			}
			dto = BeanUtil.copyObject(driverVerify, DriverVerifyDto.class);
			// 查询服务类型名称通过serviceType car_biz_car_group 
			String serviceType = driverVerify.getServiceType();
			if(StringUtils.isNotBlank(serviceType)){
				CarBizCarGroup group = carBizCarGroupMapper.selectByPrimaryKey(Integer.valueOf(serviceType));
				if(null != group){
					dto.setServiceTypeName(group.getGroupName());
				}
			}
			// 查询车型名称通过车型modelId car_biz_model 
			Integer modelId = driverVerify.getModelId();
			if(null != modelId){
				CarBizModel modle = carBizModelMapper.selectByPrimaryKey(modelId);
				if(null != modle){
					dto.setModelIdName(modle.getModelName());
				}
			}
		} catch (Exception e) {
			logger.error("查询司机加盟注册信息通过司机ID异常,driverId=" + driverId, e);
		}
		return dto;
	}

	/** 查询司机证件照片通过司机ID和证件照片类型 **/
	public String queryImageByDriverIdAndType(Long driverId, Integer type) {
		String image = "";
		try {
			logger.info("查询司机证件照片通过司机ID和证件照片类型,driverId=" + driverId);
			image = driverCertExMapper.queryImageByDriverIdAndType(driverId, type);
		} catch (Exception e) {
			logger.error("查询司机证件照片通过司机ID和证件照片类型异常,driverId=" + driverId, e);
		}
		return image;
	}

}
