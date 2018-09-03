package com.zhuanche.serv.driverteam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.pagehelper.PageInfo;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.CarBizSupplierService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.web.LayUIPage;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;

import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
/**
 * @description: 车队设置
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author lunan
 * @version 1.0
 * @since 1.0
 * @create: 2018-08-29 16:54
 *
 */
@Service
public class CarDriverTeamService{

	private static final Logger logger = LoggerFactory.getLogger(CarDriverTeamService.class);

	@Autowired
	private DataPermissionHelper dataPermissionHelper;

	@Autowired
	private CarBizCityService carBizCityService;

	@Autowired
	private CarBizSupplierService carBizSupplierService;

	@Autowired
	private CarDriverTeamExMapper carDriverTeamExMapper;

	/**
	* @Desc: 查询车队列表 
	* @param:  
	* @return:  
	* @Author: lunan
	* @Date: 2018/8/29 
	*/ 
	@SuppressWarnings("rawtypes")
	public PageDTO queryDriverTeamPage(int page, int pageSize, String cityId, String supplierId, Integer teamId ) {
		//----------------------------------------------------------------------------------首先，如果是普通管理员，校验数据权限（cityId、supplierId、teamId）
		Set<Integer> permOfCity        = new HashSet<Integer>();//普通管理员可以管理的所有城市ID
		Set<Integer> permOfSupplier = new HashSet<Integer>();//普通管理员可以管理的所有供应商ID
		Set<Integer> permOfTeam     = new HashSet<Integer>();//普通管理员可以管理的所有车队ID
		if(WebSessionUtil.isSupperAdmin() == false) {//如果是普通管理员
			permOfCity        = dataPermissionHelper.havePermOfCityIds("");
			permOfSupplier = dataPermissionHelper.havePermOfSupplierIds("");
			permOfTeam     = dataPermissionHelper.havePermOfDriverTeamIds("");
			if( permOfCity.size()==0 || (StringUtils.isNotEmpty(cityId) && permOfCity.contains(Integer.valueOf(cityId))==false)  ) {
				return null;
			}
			if( permOfSupplier.size()==0 || (StringUtils.isNotEmpty(supplierId) && permOfSupplier.contains(Integer.valueOf(supplierId))==false)   ) {
				return null;
			}
			if( permOfTeam.size()==0 || (teamId!=null && permOfTeam.contains(Integer.valueOf(teamId))==false )   ) {
//				return LayUIPage.build("您没有查询此车队的权限！", 0, null);
				return null;
			}
		}

		//A--------------------------------------------------------------------------------设置SQL查询参数
		Set<String> cityIds        = new HashSet<String>();
		Set<String> supplierIds = new HashSet<String>();
		Set<Integer> teamIds    = new HashSet<Integer>();
		//A1城市ID
		if(StringUtils.isNotEmpty(cityId)) {//当有参数传入时，以传入的参数为准
			cityIds.add(cityId);
		}else{                                       //当无参数传入时，超级管理员可以查询任何城市; 普通管理员只能查询自己数据权限内的城市
			if( WebSessionUtil.isSupperAdmin() ) {
				cityIds.clear();    
			}else {
				for(Integer cityid : permOfCity) {
					cityIds.add( String.valueOf(cityid)  );
				}
			}
		}
		//A2供应商ID
		if(StringUtils.isNotEmpty(supplierId)) {//当有参数传入时，以传入的参数为准
			supplierIds.add(supplierId);
		}else{                                              //当无参数传入时，超级管理员可以查询任何供应商; 普通管理员只能查询自己数据权限内的供应商
			if( WebSessionUtil.isSupperAdmin() ) {
				supplierIds.clear();    
			}else {
				for(Integer sid : permOfSupplier) {
					supplierIds.add( String.valueOf(sid)  );
				}
			}
		}
		//A3车队ID
		if(teamId!=null) {         //当有参数传入时，以传入的参数为准
			teamIds.add(teamId);
		}else{                         //当无参数传入时，超级管理员可以查询任何车队; 普通管理员只能查询自己数据权限内的车队
			if( WebSessionUtil.isSupperAdmin() ) {
				teamIds.clear();    
			}else {
				teamIds.addAll( permOfTeam );
			}
		}
		
		//B----------------------------------------------------------------------------------进行分页查询
		PageDTO pageDTO = new PageDTO();
		int total = 0;
		List<CarDriverTeam> driverTeams = null;
		PageInfo<CarDriverTeam> pageInfo =null;
		try{
			pageInfo =
					PageHelper.startPage(page, pageSize, true).doSelectPageInfo(() -> carDriverTeamExMapper.queryDriverTeam(cityIds, supplierIds, teamIds));
//			driverTeams = carDriverTeamExMapper.queryDriverTeam(cityIds, supplierIds, teamIds);
			driverTeams = pageInfo.getList();
			total = (int)pageInfo.getTotal();
		}finally {
			PageHelper.clearPage();
		}
		if(driverTeams==null || driverTeams.size()==0) {
			return new PageDTO();
		}
		
		//C----------------------------------------------------------------------------------将分页结果转换为DTO，并填全城市名称、供应商名称
		List<CarDriverTeamDTO> dtos = BeanUtil.copyList(driverTeams, CarDriverTeamDTO.class);
		//查询此结果中的 城市信息和供应商信息
		Set<Integer> resultOfcityIds        = new HashSet<Integer>();
		Set<Integer> resultOfsupplierIds = new HashSet<Integer>();
		for(CarDriverTeamDTO dto : dtos) {
			if( StringUtils.isNotEmpty( dto.getCity() )) {
				resultOfcityIds.add( Integer.valueOf(dto.getCity()) ) ;
			}
			if( StringUtils.isNotEmpty( dto.getSupplier() )) {
				resultOfsupplierIds.add( Integer.valueOf(dto.getSupplier()) ) ;
			}
		}
		Map<Integer, CarBizCity>        cityMapping        = carBizCityService.queryCity( resultOfcityIds );
		Map<Integer, CarBizSupplier> supplierMapping = carBizSupplierService.querySupplier(null, resultOfsupplierIds);
		//填充城市名称、供应商名称
		for(CarDriverTeamDTO dto : dtos ) {
			if( StringUtils.isNotEmpty( dto.getCity() )) {
				CarBizCity city = cityMapping.get( Integer.valueOf(dto.getCity()));
				if(city!=null) {
					dto.setCityName(  city.getCityName() );
				}
			}
			if( StringUtils.isNotEmpty( dto.getSupplier() )) {
				CarBizSupplier supplier = supplierMapping.get( Integer.valueOf(dto.getSupplier())  );
				if( supplier!=null ) {
					dto.setSupplierName( supplier.getSupplierFullName() );
				}
			}
		}
		pageDTO.setResult(dtos);
		pageDTO.setTotal(total);
		return pageDTO;
	}
}