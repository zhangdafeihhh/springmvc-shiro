package com.zhuanche.serv.driverScheduling;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.driverDuty.CarDriverMustDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMustDuty;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.CarDriverMustDutyMapper;
import mapper.mdbcarmanage.ex.CarDriverMustDutyExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.search.SearchTerm;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 强制排班
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
 * @create: 2018-09-03 12:54
 *
 */
@Service
public class CarDriverMustDutyService {

	private static final Logger logger = LoggerFactory.getLogger(CarDriverMustDutyService.class);

	@Autowired
	private CarDriverMustDutyExMapper carDriverMustDutyExMapper;

	@Autowired
	private CitySupplierTeamCommonService citySupplierTeamCommonService;

	@Autowired
	private CarBizCityExMapper carBizCityExMapper;

	@Autowired
	private CarDriverMustDutyMapper carDriverMustDutyMapper;

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;

	/** 
	* @Desc: 查询强制上班配置列表 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public PageDTO getDriverMustDutyList(DutyParamRequest dutyParamRequest){
		if(Check.NuNObj(dutyParamRequest)){
			return new PageDTO();
		}
		try{
			CommonRequest commonRequest = new CommonRequest();
			if(!Check.NuNObj(dutyParamRequest.getCityId())){
				commonRequest.setCityId(String.valueOf(dutyParamRequest.getCityId()));
			}
			if(!Check.NuNObj(dutyParamRequest.getSupplierId())){
				commonRequest.setSupplierId(String.valueOf(dutyParamRequest.getSupplierId()));
			}
			if(!Check.NuNObj(dutyParamRequest.getTeamId())){
				commonRequest.setTeamId(dutyParamRequest.getTeamId());
			}
			CommonRequest resultParmam = citySupplierTeamCommonService.paramDeal(commonRequest);
			if(Check.NuNObj(resultParmam)){
				return new PageDTO();
			}
			dutyParamRequest.setCityIds(citySupplierTeamCommonService.setStringShiftInteger(resultParmam.getCityIds()));
			dutyParamRequest.setSupplierIds(citySupplierTeamCommonService.setStringShiftInteger(resultParmam.getSupplierIds()));
			dutyParamRequest.setTeamIds(resultParmam.getTeamIds());
			PageInfo<CarDriverMustDutyDTO> pageInfo = PageHelper.startPage(dutyParamRequest.getPageNo(), dutyParamRequest.getPageSize(), true).doSelectPageInfo(()
					-> carDriverMustDutyExMapper.selectDriverMustDutyList(dutyParamRequest));
			PageDTO pageDTO = new PageDTO();
			pageDTO.setResult(pageInfo.getList());
			pageDTO.setTotal((int)pageInfo.getTotal());
			return pageDTO;
		}catch (Exception e){
			logger.error("查询强制上班列表异常:{}"+ JSON.toJSONString(e));
			return null;
		}finally {
			PageHelper.clearPage();
		}

	}

	/** 
	* @Desc: 新增或者修改强制排班信息 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/
	public int saveOrUpdateDriverMust(CarDriverMustDuty carDriverMustDuty){

		if(Check.NuNObj(carDriverMustDuty)
				|| Check.NuNObj(carDriverMustDuty.getCity())
				|| Check.NuNObj(carDriverMustDuty.getPeakTimes())
				|| Check.NuNObj(carDriverMustDuty.getSupplier())
				|| Check.NuNObj(carDriverMustDuty.getTeamId())
		){
			return 0;
		}
		if(carDriverMustDuty.getStartDate().length() != 5 || carDriverMustDuty.getEndDate().length() != 5){
			return 0;
		}
		String start = carDriverMustDuty.getStartDate().substring(0,2);
		String end = carDriverMustDuty.getEndDate().substring(0,2);
		String start1 = carDriverMustDuty.getStartDate().substring(3,5);
		String end1 = carDriverMustDuty.getEndDate().substring(3,5);
		if(!"".equals(start)&&start!=null&&!"".equals(end)&&end!=null){
			if(Integer.parseInt(start)>Integer.parseInt(end)){
				return 0;
			}else if(Integer.parseInt(start)==Integer.parseInt(end)){
				if(!"".equals(start1)&&start1!=null&&!"".equals(end1)&&end1!=null){
					if(Integer.parseInt(start1)>=Integer.parseInt(end1)){
						return 0;
					}
				}
			}
		}

		try{
			if(!Check.NuNObj(carDriverMustDuty.getId())){
				/** 修改逻辑*/
				CarDriverMustDuty upRecord = new CarDriverMustDuty();
				upRecord.setId(carDriverMustDuty.getId());
				upRecord.setStartDate(carDriverMustDuty.getStartDate());
				upRecord.setEndDate(carDriverMustDuty.getEndDate());
				upRecord.setRemark(carDriverMustDuty.getRemark());
				upRecord.setPeakTimes(carDriverMustDuty.getPeakTimes());
				upRecord.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
				upRecord.setStatus(carDriverMustDuty.getStatus());
				return carDriverMustDutyMapper.updateByPrimaryKeySelective(upRecord);
			}else{
				Set<Integer> cityId = new HashSet<>();
				cityId.add(carDriverMustDuty.getCity());
				List<CarBizCity> carBizCities = carBizCityExMapper.queryByIds(cityId);
				if(Check.NuNCollection(carBizCities)){
					return 0;
				}
				carDriverMustDuty.setCityName(carBizCities.get(0).getCityName());
				CarBizSupplier supplier = new CarBizSupplier();
				supplier.setSupplierId(carDriverMustDuty.getSupplier());
				CarBizSupplier supplierDetail = carBizSupplierExMapper.queryForObject(supplier);
				if(Check.NuNObj(supplierDetail)){
					return 0;
				}
				carDriverMustDuty.setSupplierName(supplierDetail.getSupplierFullName());
				carDriverMustDuty.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
				return carDriverMustDutyMapper.insertSelective(carDriverMustDuty);
			}
		}catch (Exception e){
			logger.error("保存/修改强制上班时间异常:{}"+ JSON.toJSONString(e));
			return 0;
		}
	}

	/** 
	* @Desc: 获取强制排班详情 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public CarDriverMustDutyDTO getCarDriverMustDetail(Integer paramId){
		if(Check.NuNObj(paramId)){
			return null;
		}
		DutyParamRequest param = new DutyParamRequest();
		param.setId(paramId);
		CarDriverMustDutyDTO detail = carDriverMustDutyExMapper.selectDriverMustDutyDetail(param);
		return detail;
	}


}