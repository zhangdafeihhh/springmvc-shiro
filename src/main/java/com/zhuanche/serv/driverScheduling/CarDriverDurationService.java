package com.zhuanche.serv.driverScheduling;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.driverDuty.CarDriverDurationDTO;
import com.zhuanche.dto.driverDuty.CarDriverMustDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMustDuty;
import com.zhuanche.entity.mdbcarmanage.CarDutyDuration;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.CarDriverMustDutyMapper;
import mapper.mdbcarmanage.CarDutyDurationMapper;
import mapper.mdbcarmanage.ex.CarDriverMustDutyExMapper;
import mapper.mdbcarmanage.ex.CarDutyDurationExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 排班时长
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
public class CarDriverDurationService {

	private static final Logger logger = LoggerFactory.getLogger(CarDriverDurationService.class);

	@Autowired
	private CitySupplierTeamCommonService citySupplierTeamCommonService;

	@Autowired
	private CarBizCityExMapper carBizCityExMapper;

	@Autowired
	private CarDutyDurationExMapper carDutyDurationExMapper;

	@Autowired
	private CarDutyDurationMapper carDutyDurationMapper;

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;

	/**
	 * @Desc: 查询排班时长列表
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/3
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public PageDTO getDriverDurationList(DutyParamRequest dutyParamRequest){
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
			PageInfo<CarDriverDurationDTO> pageInfo = PageHelper.startPage(dutyParamRequest.getPageNo(), dutyParamRequest.getPageSize(), true).doSelectPageInfo(()
					-> carDutyDurationExMapper.selectDutyDurationList(dutyParamRequest));
			PageDTO pageDTO = new PageDTO();
			pageDTO.setResult(pageInfo.getList());
			pageDTO.setTotal((int)pageInfo.getTotal());
			return pageDTO;
		}catch (Exception e){
			logger.error("查询排班时长列表异常:{}",e);
			return null;
		}finally {
			PageHelper.clearPage();
		}
	}

	/** 
	* @Desc: 保存/修改排班时长 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3 
	*/
	public int saveOrUpdateCarDriverDuration(CarDutyDuration carDutyDuration){
		if(Check.NuNObj(carDutyDuration)
				|| Check.NuNObj(carDutyDuration.getCity())
				|| Check.NuNObj(carDutyDuration.getTeamId())
				|| Check.NuNObj(carDutyDuration.getSupplier())
				){
			return 0;
		}
		if(carDutyDuration.getStartDate().length() != 5 || carDutyDuration.getEndDate().length() != 5){
			return 0;
		}
		String start = carDutyDuration.getStartDate().substring(0,2);
		String end = carDutyDuration.getEndDate().substring(0,2);
		String start1 = carDutyDuration.getStartDate().substring(3,5);
		String end1 = carDutyDuration.getEndDate().substring(3,5);
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
			if(!Check.NuNObj(carDutyDuration.getId())){
				/** 修改逻辑*/
				CarDutyDuration upRecord = new CarDutyDuration();
				upRecord.setId(carDutyDuration.getId());
				upRecord.setStartDate(carDutyDuration.getStartDate());
				upRecord.setEndDate(carDutyDuration.getEndDate());
				upRecord.setRemark(carDutyDuration.getRemark());
				upRecord.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
				return carDutyDurationMapper.updateByPrimaryKeySelective(upRecord);
			}else{
				Set<Integer> cityId = new HashSet<>();
				cityId.add(carDutyDuration.getCity());
				List<CarBizCity> carBizCities = carBizCityExMapper.queryByIds(cityId);
				if(Check.NuNCollection(carBizCities)){
					return 0;
				}
				carDutyDuration.setCityName(carBizCities.get(0).getCityName());
				CarBizSupplier supplier = new CarBizSupplier();
				supplier.setSupplierId(carDutyDuration.getSupplier());
				CarBizSupplier supplierDetail = carBizSupplierExMapper.queryForObject(supplier);
				if(Check.NuNObj(supplierDetail)){
					return 0;
				}
				carDutyDuration.setSupplierName(supplierDetail.getSupplierFullName());
				carDutyDuration.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());
				return carDutyDurationMapper.insertSelective(carDutyDuration);
			}
		}catch (Exception e){
			logger.error("保存/修改排班时长异常:{}",e);
			return 0;
		}

	}

	/**
	 * @Desc: 获取排班时长详情
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/3
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public CarDriverDurationDTO getCarDriverDurationDetail(Integer paramId){
		if(Check.NuNObj(paramId)){
			return null;
		}
		CarDriverDurationDTO carDutyDuration = carDutyDurationExMapper.selectOne(paramId);
		return carDutyDuration;
	}


}