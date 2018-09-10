package com.zhuanche.serv.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.session.WebSessionUtil;

import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;

/**
 * 查询当前登录用户可见的城市、供应商、车队信息 
 * ClassName: CitySupplierTeamService.java 
 * Date:2018年9月3日
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 */
@Service
public class CitySupplierTeamService {

	private static final Logger logger = LoggerFactory.getLogger(CitySupplierTeamService.class);

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;

	@Autowired
	private CarDriverTeamExMapper carDriverTeamExMapper;

	/** 查询当前登录用户可见的供应商信息 **/
	public List<CarBizSupplier> querySupplierList() {

		List<CarBizSupplier> suppliers = new ArrayList<CarBizSupplier>();
		// 进行查询 (区分 超级管理员 、普通管理员 )
		if (WebSessionUtil.isSupperAdmin()) {
			suppliers = carBizSupplierExMapper.querySuppliers(null, null);
		} else {
			Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
			if (null != supplierIds && supplierIds.size() > 0) {
				suppliers = carBizSupplierExMapper.querySuppliers(null, supplierIds);
			}else{
				suppliers = carBizSupplierExMapper.querySuppliers(null, null);
			}
		}
		return suppliers;
	}

	/** 查询当前登录用户可见的车队信息 **/
	public List<CarDriverTeam> queryDriverTeamList() {
		
		List<CarDriverTeam> teams = new ArrayList<CarDriverTeam>();
		// 进行查询 (区分 超级管理员 、普通管理员 )
		if (WebSessionUtil.isSupperAdmin()) {
			teams =  carDriverTeamExMapper.queryDriverTeam(null, null, null);
		} else {
			Set<Integer> teamIds = WebSessionUtil.getCurrentLoginUser().getTeamIds();
			if (null != teamIds && teamIds.size() > 0) {
				teams =  carDriverTeamExMapper.queryDriverTeam(null, null, teamIds);
			}else{
				teams =  carDriverTeamExMapper.queryDriverTeam(null, null, null);
			}
		}
		return teams;
	}

	/** 查询供应商下可见的车队信息 **/
	public List<CarDriverTeam> queryDriverTeamListForSupplier(Integer supplierId) {
		if(supplierId==null || supplierId.intValue()<=0) {
            return new ArrayList<CarDriverTeam>();
        }
        //供应商ID 进行校验数据权限
        if(WebSessionUtil.isSupperAdmin()==false ) {
            Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
            if(null !=supplierIds && supplierIds.size()>0 && supplierIds.contains(supplierId)==false  ) {
                return new ArrayList<CarDriverTeam>();
            }
        }
        //进行查询 (区分 超级管理员 、普通管理员 )
        Set<String> supplierIds = new HashSet<String>(1);
        supplierIds.add(String.valueOf(supplierId));
        if( WebSessionUtil.isSupperAdmin() ) {
            return carDriverTeamExMapper.queryDriverTeam(null, supplierIds, null);
        }else {
            Set<Integer> teamIds = WebSessionUtil.getCurrentLoginUser().getTeamIds();
            if(null == teamIds || teamIds.size()==0 ) {
                return carDriverTeamExMapper.queryDriverTeam(null, supplierIds, null);
            }else{
            	return carDriverTeamExMapper.queryDriverTeam(null, supplierIds, teamIds);
            }
        }
	}
}
