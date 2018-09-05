package com.zhuanche.serv.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.dutyEnum.EnumDriverDutyTimeFlag;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverDayDuty;
import com.zhuanche.entity.mdbcarmanage.DriverDutyTimeInfo;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Check;
import com.zhuanche.util.Common;
import mapper.mdbcarmanage.ex.CarDriverDayDutyExMapper;
import mapper.mdbcarmanage.ex.DriverDutyTimeInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 查看排班
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
 * @create: 2018-09-01 12:54
 *
 */
@Service
public class CarDriverDutyService {

	private static final Logger logger = LoggerFactory.getLogger(CarDriverDutyService.class);

	@Autowired
	private DataPermissionHelper dataPermissionHelper;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	@Autowired
	private CarDriverDayDutyExMapper carDriverDayDutyExMapper;

	@Autowired
	private DriverDutyTimeInfoExMapper driverDutyTimeInfoExMapper;

	@Autowired
	private CitySupplierTeamCommonService citySupplierTeamCommonService;

	@Autowired
	private AsyncDutyService asyncDutyService;

	private static final ExecutorService es = Executors.newCachedThreadPool();

	/** 
	* @Desc: 查询司机排班 (包含发布司机排班数据列表 status 1)
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/1 
	*/ 
	public PageDTO queryDriverDayDutyList(DutyParamRequest dutyParamRequest){


		if(Check.NuNObj(dutyParamRequest)){
			return null;
		}
		//发布司机排班的查询功能 上层返回提示语
		if(dutyParamRequest.getUnpublishedFlag() == 1){
			dutyParamRequest.setStatus(1);
		}
		try{
			/** 数据权限处理开始 */
			CommonRequest commonRequest = new CommonRequest();
			commonRequest.setCityId(String.valueOf(dutyParamRequest.getCityId()));
			commonRequest.setSupplierId(String.valueOf(dutyParamRequest.getSupplierId()));
			commonRequest.setTeamId(dutyParamRequest.getTeamId());
			CommonRequest resultParmam = citySupplierTeamCommonService.paramDeal(commonRequest);
			dutyParamRequest.setCityIds(citySupplierTeamCommonService.setStringShiftInteger(resultParmam.getCityIds()));
			dutyParamRequest.setSupplierIds(citySupplierTeamCommonService.setStringShiftInteger(resultParmam.getSupplierIds()));
			dutyParamRequest.setSupplierIds(resultParmam.getTeamIds());
			/** 数据权限处理结束 */

			DutyParamRequest request = new DutyParamRequest();
			//手机号转换driverId进行查询
			if(!Check.NuNStr(dutyParamRequest.getPhone())){
				request.setPhone(dutyParamRequest.getPhone());
				CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.queryOneDriver(request);
				request.setPhone(null);
				if(Check.NuNObj(driverInfo)){
					return null;
				}
				dutyParamRequest.setDriverId(Integer.valueOf(driverInfo.getDriverId()));
			}

			PageInfo<CarDriverDayDutyDTO> pageInfo = PageHelper.startPage(dutyParamRequest.getPageNo(), dutyParamRequest.getPageSize(), true).doSelectPageInfo(()
					-> carDriverDayDutyExMapper.selectForList(dutyParamRequest));
			List<CarDriverDayDutyDTO> list = pageInfo.getList();

			for (CarDriverDayDutyDTO carDriverDayDuty : list) {
				request.setDriverId(carDriverDayDuty.getDriverId());
				CarDriverInfoDTO info = carBizDriverInfoExMapper.queryOneDriver(request);
				carDriverDayDuty.setPhone(info.getPhone());
			}
			PageDTO pageDTO = new PageDTO();
			pageDTO.setTotal((int)pageInfo.getTotal());
			pageDTO.setResult(list);
			return pageDTO;
		}catch (Exception e){
			logger.error("查询排班司机列表异常:{}",e);
			return null;
		}
	}

	/** 
	* @Desc: 发布排班 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/3
	*/ 
	public int issueDriverDuty(DutyParamRequest dutyParamRequest){
		if(Check.NuNObj(dutyParamRequest)){
			return 0;
		}
		String startTime = this.getStartTimeForParams(dutyParamRequest);
		if (null != startTime) {
			dutyParamRequest.setStartTime(startTime);
		}
		try{
			if (Check.NuNObj(dutyParamRequest.getCityId())) {
				return ServiceReturnCodeEnum.NONE_CITY_EXISTS.getCode();
			}
			if (Check.NuNObj(dutyParamRequest.getSupplierId())) {
				return ServiceReturnCodeEnum.NONE_SUPPLIER_EXISTS.getCode();
			}
			if (Check.NuNObj(dutyParamRequest.getTeamId())) {
				return ServiceReturnCodeEnum.NONE_TEAM_EXISTS.getCode();
			}
			DutyParamRequest request = new DutyParamRequest();
			//手机号转换driverId进行查询
			if(!Check.NuNStr(dutyParamRequest.getPhone())){
				request.setPhone(dutyParamRequest.getPhone());
				CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.queryOneDriver(request);
				request.setPhone(null);
				if(Check.NuNObj(driverInfo)){
					return ServiceReturnCodeEnum.NONE_RECODE_EXISTS.getCode();
				}
				dutyParamRequest.setDriverId(Integer.valueOf(driverInfo.getDriverId()));
			}
			dutyParamRequest.setStatus(1);
			Integer total = carDriverDayDutyExMapper.getUnIssueCount(dutyParamRequest);
			if(total <= 0){
				return ServiceReturnCodeEnum.NONE_RECODE_EXISTS.getCode();
			}
			es.submit(new AffirmDriverDayDuty(dutyParamRequest));
			return ServiceReturnCodeEnum.DEAL_SUCCESS_MSG.getCode();
		}catch (Exception e){
			logger.error("发布数据异常:{}",e);
			return 0;
		}

	}

	class AffirmDriverDayDuty implements Runnable{

		private DutyParamRequest dutyParamRequest;

		public AffirmDriverDayDuty(DutyParamRequest dutyParamRequest){
			this.dutyParamRequest = dutyParamRequest;
		}
		@Override
		public void run() {
			try {
				// 发布数据
				asyncDutyService.affirmDriverDayDuty(dutyParamRequest);
			} catch (Exception e) {
				logger.error("AffirmDriverDayDuty",e);
			}
		}
	}

	private String getStartTimeForParams(DutyParamRequest params) {
		String startTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sysDateStr = sdf.format(new Date());
		try {
			Date sysDate = sdf.parse(sysDateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sysDate);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			Date defaultStartTime = calendar.getTime();
			startTime = sdf.format(defaultStartTime);

			if (null != params && !StringUtils.isEmpty(params.getStartTime())) {
				Date searchStartTime = sdf.parse(params.getStartTime());
				if (searchStartTime.after(defaultStartTime)) {
					startTime = null;
				}
			}
		} catch (ParseException e) {
			logger.error("时间参数错误", e);
		}

		return startTime;
	}




}