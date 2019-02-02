package com.zhuanche.controller.driverjoin;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.constant.Constants;
import com.zhuanche.util.dateUtil.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.securityLog.SensitiveDataOperationLog;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.DriverJoinRecordDto;
import com.zhuanche.dto.driver.DriverVerifyDto;
import com.zhuanche.entity.driver.DriverJoinRecord;
import com.zhuanche.serv.driverjoin.DriverVerifyService;
import com.zhuanche.util.BeanUtil;

import mapper.driver.ex.DriverJoinRecordExMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.zhuanche.common.enums.MenuEnum.*;

/**
 * 司机加盟注册 
 * ClassName: DriverVerifyController.java 
 * Date: 2018年8月29日
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 */

@Controller
@RequestMapping("/driverVerify")
public class DriverVerifyController {

	private static final Logger logger = LoggerFactory.getLogger(DriverVerifyController.class);

	@Autowired
	DriverVerifyService driverVerifyService;

	@Autowired
	DriverJoinRecordExMapper driverJoinRecordExMapper;
	
	/**
	 * 查询加盟司机审核列表数据
	 * 
	 * @param page
	 * @param pageSize
	 * @param cityId
	 * @param supplier
	 * @param mobile
	 * @param verifyStatus
	 * @param createDateBegin
	 * @param createDateEnd
	 * @return
	 */
	@RequestMapping("/queryDriverVerifyData")
	@RequiresPermissions(value = { "DriverInvite_look" } )
	@ResponseBody
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@SensitiveDataOperationLog(primaryDataType="加盟司机数据",secondaryDataType="加盟司机个人基本信息",desc="加盟司机信息列表查询")
	@RequestFunction(menu = DRIVER_JOIN_PROMOTE_LIST)
	public AjaxResponse queryDriverVerifyData(Integer page, Integer pageSize, Long cityId, String supplier,
			String mobile, Integer verifyStatus, String createDateBegin, String createDateEnd) {

		if (null == page || page.intValue() <= 0) {
			page = 1;
		}
		if (null == pageSize || pageSize.intValue() <= 0) {
			pageSize = 20;
		}
		// 增加操作日志 TODO
		PageDTO pageDto = new PageDTO();
		// 分页查询司机加盟注册信息
		pageDto = driverVerifyService.queryDriverVerifyList(page, pageSize, cityId, supplier, mobile, verifyStatus,
				createDateBegin, createDateEnd);
		return AjaxResponse.success(pageDto);
	}

	@RequestMapping("/exportDriverVerifyData")
	@RequiresPermissions(value = { "DriverInvite_export" } )
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@SensitiveDataOperationLog(primaryDataType="加盟司机数据",secondaryDataType="加盟司机个人基本信息",desc="加盟司机信息列表查询")
	@RequestFunction(menu = DRIVER_JOIN_PROMOTE_EXPORT)
	public void exportDriverVerifyData(Long cityId, String supplierId,
									   String mobile, Integer verifyStatus, String createDateBegin, String createDateEnd,
									   HttpServletRequest request, HttpServletResponse response){
		int page = 1;
		int pageSize = CsvUtils.downPerSize;
		PageDTO pageDTO = driverVerifyService.queryDriverVerifyList(page, pageSize, cityId, supplierId, mobile, verifyStatus,
				createDateBegin, createDateEnd);
		pageDTO.setPageSize(CsvUtils.downPerSize);
		int pages = pageDTO.getPages();
		CsvUtils utilEntity = new CsvUtils();
		try {
			List<String> headerList = new ArrayList<>();
			headerList.add("注册城市,注册ID,姓名,手机号,供应商,车牌号,注册时间,加盟状态");
			String fileName = "司机加盟推广信息" + com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern) + ".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) {  //IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {  //其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}
			utilEntity.exportCsvV2(response, transferData(pageDTO),headerList,fileName,true, page == pages);
			page+=1;
			while (page <= pages) {
				pageDTO = driverVerifyService.queryDriverVerifyList(page, pageSize, cityId, supplierId, mobile, verifyStatus,
						createDateBegin, createDateEnd);
				utilEntity.exportCsvV2(response, transferData(pageDTO),headerList,fileName,false, page == pages);
				page++;
			}
		}catch (Exception e){
			logger.error("导出司机信息失败", e);
			return;
		}
	}


	/** 查询司机加盟注册信息通过司机ID **/
	@RequestMapping(value = "/queryDriverVerifyById")
	@ResponseBody
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@SensitiveDataOperationLog(primaryDataType="加盟司机数据",secondaryDataType="加盟司机个人信息详情",desc="加盟司机信息详情查看")
	@RequestFunction(menu = DRIVER_JOIN_PROMOTE_DETAIL)
	public AjaxResponse queryDriverVerifyById(@Verify(param = "driverId", rule = "required") Long driverId) {

		DriverVerifyDto driverDto = driverVerifyService.queryDriverVerifyById(driverId);
		return AjaxResponse.success(driverDto);
	}

	/** 查询司机证件照片通过司机ID和证件照片类型 **/
	@RequestMapping(value = "/queryImageByDriverIdAndType")
	@ResponseBody
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@RequestFunction(menu = DRIVER_JOIN_PROMOTE_IMAGE)
	public AjaxResponse queryImageByDriverIdAndType(@Verify(param = "driverId", rule = "required") Long driverId,
			@Verify(param = "type", rule = "required") Integer type) {

		String image = driverVerifyService.queryImageByDriverIdAndType(driverId, type);
		Map<String, Object> result = Maps.newHashMap();
		result.put("image", image);
		return AjaxResponse.success(result);
	}
	
	/** 查询司机加盟记录列表数据 **/
	@ResponseBody
	@RequestMapping(value = "/queryDriverJoinRecordData")
	@MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@RequestFunction(menu = DRIVER_JOIN_PROMOTE_RECORD)
	public AjaxResponse queryDriverJoinRecordData(Integer page, Integer pageSize, @Verify(param = "driverId", rule = "required") Long driverId){
		logger.info("查询司机加盟记录列表数据通过司机ID,driverId="+driverId);
		if (null == page || page.intValue() <= 0) {
			page = 1;
		}
		if (null == pageSize || pageSize.intValue() <= 0) {
			pageSize = 20;
		}
		// 分页查询司机加盟记录信息
		PageDTO pageDto = new PageDTO();
		int total = 0;
		List<DriverJoinRecord> driverJoinRecordList = null;
		PageInfo<DriverJoinRecord> pageInfo = null;
		try {
			pageInfo = PageHelper.startPage(page, pageSize, true).doSelectPageInfo(() -> driverJoinRecordExMapper
					.queryJoinRecordByDriverId(driverId));
			total = (int) pageInfo.getTotal();
			driverJoinRecordList = pageInfo.getList();
			if (null == driverJoinRecordList || driverJoinRecordList.size() <= 0) {
				return AjaxResponse.success(pageDto);
			}
			// 将分页查询结果转成dto
			List<DriverJoinRecordDto> dtos = BeanUtil.copyList(driverJoinRecordList, DriverJoinRecordDto.class);
			pageDto.setResult(dtos);
			pageDto.setTotal(total);
		} catch (Exception e) {
			logger.error("查询司机加盟记录列表数据异常,driverId="+driverId, e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		} finally {
			PageHelper.clearPage();
		}
		return AjaxResponse.success(pageDto);
	}

	private List<String> transferData(PageDTO pageDTO){
		List<String> data = new ArrayList<>();
		List<DriverVerifyDto> result = pageDTO.getResult();
		if (result == null || result.isEmpty()){
			return data;
		}
		StringBuilder builder = new StringBuilder();
		for (DriverVerifyDto driverVerify : result){
			if (driverVerify == null){
				continue;
			}
			builder.append("\t").append(driverVerify.getCityName() == null ? "" : driverVerify.getCityName()).append(Constants.SEPERATER);
			builder.append("\t").append(driverVerify.getId()).append(Constants.SEPERATER);
			builder.append("\t").append(driverVerify.getName() == null ? "" : driverVerify.getName()).append(Constants.SEPERATER);
			builder.append("\t").append(driverVerify.getMobile() == null ? "" : driverVerify.getMobile()).append(Constants.SEPERATER);
			builder.append("\t").append(driverVerify.getSupplierName() == null ? "" : driverVerify.getSupplierName()).append(Constants.SEPERATER);
			builder.append("\t").append(driverVerify.getPlateNum() == null ? "" : driverVerify.getPlateNum()).append(Constants.SEPERATER);
			builder.append("\t").append(DateUtil.timestampFormat(driverVerify.getCreateAt())).append(Constants.SEPERATER);
			Integer verifyStatus = driverVerify.getVerifyStatus();
			//1注册、2资料完善、3资料审核通过、4资料审核未通过、5验车通过、6验车未通过
			String status;
			switch (verifyStatus){
				case 1:
					status = "注册";
					break;
				case 2:
					status = "资料完善";
					break;
				case 3:
					status = "资料审核通过";
					break;
				case 4:
					status = "资料审核未通过";
					break;
				case 5:
					status = "验车通过";
					break;
				case 6:
					status = "验车未通过";
					break;
				default:
					status = "未知状态";
					break;
			}
			builder.append(status);
			data.add(builder.toString());
			builder.delete(0, builder.length());
		}
		return data;
	}

}
