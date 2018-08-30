package com.zhuanche.controller.driverteam;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.CarBizCarInfoTempDTO;
import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import com.zhuanche.serv.deiver.CarBizCarInfoTempService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/temporary")
public class TemporaryController{

	@Autowired
	private CarBizCarInfoTempService carBizCarInfoTempService;


    /**
     *
     * @param page 页数
     * @param pageSize 条数
     * @param licensePlates 车牌号
     * @param carModelIds 车型
     * @param cities 城市
     * @param supplierIds 供应商
     * @param createDateBegin 开始时间
     * @param createDateEnd 结束时间
     * @return
     */
	@ResponseBody
	@RequestMapping(value = "/queryCarData", method =  RequestMethod.GET )
	public AjaxResponse queryCarData(@RequestParam(value = "page",defaultValue="1") Integer page,
                                     @RequestParam(value = "pageNum",defaultValue="10") Integer pageSize,
                                     @RequestParam(value = "licensePlates",required = false) String licensePlates,
                                     @RequestParam(value = "carModelIds",required = false) String carModelIds,
                                     @RequestParam(value = "cities",required = false) String cities,
                                     @RequestParam(value = "supplierIds",required = false) String supplierIds,
                                     @RequestParam(value = "createDateBegin",required = false) String createDateBegin,
                                     @RequestParam(value = "createDateEnd",required = false) String createDateEnd) {
	    int total = 0;
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String,Object> params = Maps.newHashMap();
        String sessionCities = StringUtils.join(currentLoginUser.getCityIds().toArray(), ",");
        String sessionSupplierIds = StringUtils.join(currentLoginUser.getSupplierIds().toArray(), ",");
        String sessionCarModelIds = StringUtils.join(currentLoginUser.getTeamIds().toArray(), ",");
        params.put("licensePlates",licensePlates);
        params.put("createDateBegin",createDateBegin);
        params.put("createDateEnd",createDateEnd);
        if(StringUtils.isNotBlank(carModelIds)){
            params.put("carModelIds",carModelIds);
        }else{
            params.put("carModelIds",sessionCities);
        }
        if(StringUtils.isNotBlank(cities)){
            params.put("cities",cities);
        }else{
            params.put("cities",sessionSupplierIds);
        }
        if(StringUtils.isNotBlank(supplierIds)){
            params.put("supplierIds",supplierIds);
        }else{
            params.put("supplierIds",sessionCarModelIds);
        }
        params.put("cities",StringUtils.join(currentLoginUser.getCityIds().toArray(), ","));
        params.put("supplierIds",StringUtils.join(currentLoginUser.getSupplierIds().toArray(), ","));
        params.put("carModelIds",StringUtils.join(currentLoginUser.getTeamIds().toArray(), ","));
        Page p = PageHelper.startPage(page, pageSize,true);
        List<CarBizCarInfoTemp> carBizCarInfoTempList = null;
        try{
            carBizCarInfoTempList = carBizCarInfoTempService.queryForPageObject(params);
        }finally {
            PageHelper.clearPage();
        }
        if(carBizCarInfoTempList == null || carBizCarInfoTempList.size() == 0){
            PageDTO pageDto = new PageDTO(page,pageSize,0,new ArrayList());
            return AjaxResponse.success(pageDto);
        }
        List<CarBizCarInfoTempDTO> carBizCarInfoTempDTOList = BeanUtil.copyList(carBizCarInfoTempList,CarBizCarInfoTempDTO.class);
        PageDTO pageDto = new PageDTO(page,pageSize,total,carBizCarInfoTempDTOList);
        return AjaxResponse.success(pageDto);
	}

	/*@AuthPassport
	@RequestMapping(value = "/queryCarInfo", method = { RequestMethod.GET })
	public String queryCarInfo(ModelMap model, CarInfoEntity params) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"查看车辆详情列表");
		} catch (Exception e) {
		}
		logger.info("queryCar:查看车辆详情列表");
		params = getCarInfo(params); 
		model.put("carInfoEntity", params);
		return "car/carInfo";
	}
	
	public CarInfoEntity getCarInfo(CarInfoEntity carInfo){
		CarInfoEntity params = new CarInfoEntity();
		params = this.carTempService.queryForObject(carInfo);
		if(params!=null){
			Map<String, Object> result = new HashMap<String, Object>();
			result = super.querySupplierName(params.getCityId(), params.getSupplierId());
			params.setCityName((String)result.get("cityName"));
			params.setSupplierName((String)result.get("supplierName"));
			CarBizModelEntity model = carBizModelService.selectByPrimaryKey(params.getCarModelId());
			if(model!=null){
				params.setModeName(model.getModelName());
			}
		}
		return params;
	}

	@AuthPassport
	@RequestMapping(value = "/changeCarInfo", method = { RequestMethod.GET })
	public String changeCarInfo(ModelMap model, CarInfoEntity params) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"新增车辆页");
		} catch (Exception e) {
		}
		if (params != null && params.getCarId() != null) {
			params = getCarInfo(params); 
			model.put("carInfoEntity", params);
		}
		return "temporary/car/addCar";
	}

	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/checkLicensePlates", method = { RequestMethod.POST })
	public Object checkLicensePlates(CarInfoEntity params) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"根据车牌号查询是否已存在");
		} catch (Exception e) {
		}
		logger.info("根据车牌号查询是否已存在:checkLicensePlates");
		return carTempService.checkLicensePlates(params.getLicensePlates());
	}

	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/saveCarInfo", method = { RequestMethod.POST })
	public Object saveCarInfo(CarInfoEntity params, HttpServletRequest request) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"车辆保存/修改");
		} catch (Exception e) {
		}
		logger.info("车辆保存/修改:saveCarInfo");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			CustomUserDetails user = ToolUtils.getSecurityUser();
			Integer userId = user.getUserId();
			params.setUpdateBy(userId);
			if (params.getCarId() == null) {
				logger.info("*********操作类型：新建");
				params.setCreateBy(userId);
			}
			result = this.carTempService.save(params);
		} catch (Exception e) {
			logger.error("save CarInfo error. ", e);
		}
		return result;
	}

	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/deleteCarInfo", method = { RequestMethod.POST })
	public Object deleteCarInfo(CarInfoEntity params, HttpServletRequest request) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"车辆删除");
		} catch (Exception e) {
		}
		logger.info("车辆删除:deleteCarInfo");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = this.carTempService.delete(params);
		} catch (Exception e) {
			logger.error("delete CarInfo error. ", e);
		}
		return result;
	}
	
	*//**
	 * 车辆信息导入
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/importCarInfo")
	public Object importCarInfo(CarInfoEntity params, HttpServletRequest request) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"车辆信息导入");
		} catch (Exception e) {
		}
		logger.info("车辆信息导入保存:importCarInfo,参数" + params.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result = this.carTempService.importCarInfo(params, request);
		return result;
	}

	*//**
	 * 巴士车辆信息导入
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/importCarInfo4Bus")
	public Object importCarInfo4Bus(CarInfoEntity params, HttpServletRequest request) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"车辆（巴士）信息导入");
		} catch (Exception e) {
		}
		logger.info("车辆信息（巴士）导入保存:importCarInfo,参数" + params.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result = this.carTempService.importCarInfo4Bus(params, request);
		return result;
	}

	*//**
	 * 车辆导入模板下载接口
	 *
	 * @param response
	 * @throws IOException
	 *//*
	@AuthPassport
	@RequestMapping(value="/exportBusExcel")
	public void exportExcel(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream;charset=ISO8859-1");
		response.setHeader("Content-Disposition", "attachment;filename=" + new String(BusConstant.BusExcel.SHEET_NAME.getBytes("GB2312"), "ISO8859-1") + ".xls");
		response.addHeader("Pargam", "no-cache");
		response.addHeader("Cache-Control", "no-cache");
		new ExportExcelUtil<>().exportExcel(BusConstant.BusExcel.SHEET_NAME, BusConstant.BusExcel.BUS_EXCLE_TITLE, response.getOutputStream());
	}


	*//**
	 * 车辆信息审核
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/shenheCarInfo")
	public Object shenheCarInfo(CarInfoEntity params) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"车辆信息审核");
		} catch (Exception e) {
		}
		logger.info("车辆信息审核:importCarInfo,参数" + params.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result = this.carTempService.shenheCarInfo(params);
		return result;
	}
	
	*//**
	  * 
	 *车辆信息导出
	 *//*
	@AuthPassport
	@RequestMapping("/exportCarInfo")
	public void exportCarInfo(CarInfoEntity params, HttpServletRequest request,HttpServletResponse response){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"车辆信息导出");
		} catch (Exception e) {
		}
		logger.info("exportCarInfo:车辆信息导出");
		try {
			@SuppressWarnings("deprecation")
			Workbook wb = this.carTempService.exportExcel(params,request.getRealPath("/")+File.separator+"template"+File.separator+"car_info.xlsx");
			super.exportExcelFromTemplet(request, response, wb, new String("车辆信息列表".getBytes("utf-8"), "iso8859-1"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	//司机====================================================================================
	@AuthPassport
	@RequestMapping(value="/driverList", method = { RequestMethod.GET })
	public String driverList(){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"司机信息列表");
		} catch (Exception e) {
		}
		logger.info("司机信息列表");
		return "temporary/driver/driverList";
	}
	
	@AuthPassport
	@RequestMapping(value="/driverListNew", method = { RequestMethod.GET })
	public String driverListNew(){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"司机信息列表");
		} catch (Exception e) {
		}
		return "temporary/driver/driverExamineList";
	}
	
	@AuthPassport
	@ResponseBody
	@RequestMapping(value="/driverListData", method = { RequestMethod.POST })
	public Object driverListData(DriverVoEntity driverEntity){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"司机信息列表->查询");
		} catch (Exception e) {
		}
		logger.info("司机信息列表:driverEntity,参数"+driverEntity.toString());
		
		//权限  当前用户的权限
		String cities = ToolUtils.getSecurityUser().getCities();
		String suppliers = ToolUtils.getSecurityUser().getSuppliers();
		String teamIds = ToolUtils.getSecurityUser().getTeamId();
		String groupIds = driverEntity.getGroupIds();
		if(driverEntity.getServiceCityId()!=null && !"".equals(driverEntity.getServiceCityId())){
			cities = String.valueOf(driverEntity.getServiceCityId());
		}
		if(driverEntity.getSupplierId()!=null && !"".equals(driverEntity.getSupplierId())){
			suppliers = String.valueOf(driverEntity.getSupplierId());
		}
		if(!"".equals(driverEntity.getTeamIds())&&driverEntity.getTeamIds()!=null){
			teamIds = driverEntity.getTeamIds();
		}
		List<DriverVoEntity> rows = new ArrayList<DriverVoEntity>(); 
		int total=0;
		driverEntity.setCities(cities);
		driverEntity.setSuppliers(suppliers);
		driverEntity.setTeamIds(teamIds);
		driverEntity.setGroupIds(groupIds);
		
		total = this.driverTempService.queryForInt(driverEntity);
		if(total==0){
			return this.gridJsonFormate(rows, total);
		}
		rows = this.driverTempService.queryForPageObject(driverEntity);
		rows = getGroupName(rows);
		return this.gridJsonFormate(rows, total);
	}
	
	public List<DriverVoEntity> getGroupName(List<DriverVoEntity> rows){
		if(rows!=null&&rows.size()>0){
			for(DriverVoEntity cdpi : rows){
				Map<String, Object> result = new HashMap<String, Object>();
				result = super.querySupplierName(cdpi.getCityId(), cdpi.getSupplierId());
				cdpi.setServiceCity((String)result.get("cityName"));
				cdpi.setSupplierName((String)result.get("supplierName"));
				//车型，加盟类型
				if(cdpi.getGroupid()!=null&&!"".equals(cdpi.getGroupid())){
					GroupEntity group = new GroupEntity();
					group.setGroupId(cdpi.getGroupid());
					GroupEntity groupd = groupService.queryForObject(group);
					if(groupd!=null){
						cdpi.setCarGroupName(groupd.getGroupName());
					}
				}
				if(cdpi.getCooperationType()!=0){
					CooperationTypeEntity group = new CooperationTypeEntity();
					group.setId(cdpi.getCooperationType());
					CooperationTypeEntity groupd = cooperationTypeService.queryForObject(group);
					if(groupd!=null){
						cdpi.setCooperationName(groupd.getCooperationName());
					}
				}
			}
		}
		return rows;
	}
	
	*//**
	 * 司机个人信息
	 *//*
	@AuthPassport
	@RequestMapping(value = "/driverInfo")
	public String driverInfo(ModelMap model, DriverVoEntity driver) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"司机个人信息");
		} catch (Exception e) {
		}
		logger.info("/web/driver/driverInfo:司机个人信息");
		DriverVoEntity rows = selectDriverVoEntity(driver);
		model.addAttribute("driver", rows);
		return "driverManage/driverInfoNew";
	}
	
	public DriverVoEntity selectDriverVoEntity(DriverVoEntity driver){
		DriverVoEntity rows = this.driverTempService.queryForObject(driver);
		if (rows.getDrivingLicenseType() != null) {
			if (rows.getDrivingLicenseType().equals("1")) {
				rows.setDrivingTypeString("A1");
			} else if (rows.getDrivingLicenseType().equals("2")) {
				rows.setDrivingTypeString("A2");
			} else if (rows.getDrivingLicenseType().equals("3")) {
				rows.setDrivingTypeString("B1");
			} else if (rows.getDrivingLicenseType().equals("4")) {
				rows.setDrivingTypeString("B2");
			} else if (rows.getDrivingLicenseType().equals("5")) {
				rows.setDrivingTypeString("C1");
			} else if (rows.getDrivingLicenseType().equals("6")) {
				rows.setDrivingTypeString("C2");
			} else {
				rows.setDrivingTypeString(rows.getDrivingLicenseType());
			}
		}
		if(rows!=null){
			Map<String, Object> result = new HashMap<String, Object>();
			result = super.querySupplierName(rows.getCityId(), rows.getSupplierId());
			rows.setCityName((String)result.get("cityName"));
			rows.setServiceCity((String)result.get("cityName"));
			rows.setSupplierName((String)result.get("supplierName"));
			// 车型，加盟类型
			if(rows.getGroupid()!=null&&!"".equals(rows.getGroupid())){
				GroupEntity group = new GroupEntity();
				group.setGroupId(rows.getGroupid());
				GroupEntity groupd = groupService.queryForObject(group);
				if(groupd!=null){
					rows.setCarGroupName(groupd.getGroupName());
				}
			}
			if(rows.getCooperationType()!=0){
				CooperationTypeEntity group = new CooperationTypeEntity();
				group.setId(rows.getCooperationType());
				CooperationTypeEntity groupd = cooperationTypeService.queryForObject(group);
				if(groupd!=null){
					rows.setCooperationName(groupd.getCooperationName());
				}
			}
		}
		return rows;
	}
	
	*//**
	 * 跳转到修改司机个人信息
	 *//*
	@AuthPassport
	@RequestMapping(value = "/driverInfoChange")
	public String driverInfoChange(ModelMap model, DriverVoEntity driver ) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"修改司机个人信息页");
		} catch (Exception e) {
		}
		logger.info("/web/driver/driverInfoChange:跳转到修改司机个人信息");
		if (driver != null && driver.getDriverId() != null) {
			DriverVoEntity rows = selectDriverVoEntity(driver);
			model.addAttribute("driver", rows);
			return "temporary/driver/driverInfoChange";
		}
		return "temporary/driver/driverInfoAdd";
	}
	
	@AuthPassport
	@RequestMapping(value="/driverInfoChangeData")
	public @ResponseBody Object driverInfoChangeData(DriverVoEntity driver){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"修改司机个人信息");
		} catch (Exception e) {
		}
		logger.info("修改司机个人信息:driverInfoChangeData");
		Map<String,Object> result = new HashMap<String,Object>();
		result = this.driverTempService.save(driver);
		return result;
	}
	
	*//**
	 * 司机信息导入
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/importDriverInfo")
	public Object importDriverInfo(DriverVoEntity driverEntity,HttpServletRequest request){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"供应商导入司机信息");
		} catch (Exception e) {
		}
		logger.info("供应商司机信息导入保存:importDriverInfo,参数");
		Map<String,Object> result = new HashMap<String,Object>();
		result = this.driverTempService.importDriverInfo(driverEntity, request);
		return result;
	}
	
	*//**
	 * 司机信息导入
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/importDriverInfo4Bus")
	public Object importDriverInfo4Bus(DriverVoEntity driverEntity,HttpServletRequest request){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"供应商导入司机(巴士)信息");
		} catch (Exception e) {
		}
		logger.info("供应商司机(巴士)信息导入保存:importDriverInfo4Bus,参数");
		Map<String,Object> result = new HashMap<String,Object>();
		result = this.driverTempService.importDriverInfo4Bus(driverEntity, request);
		return result;
	}
	
	*//**
	 * 司机手机号是否存在
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/checkPhone")
	public Boolean checkPhone(DriverVoEntity driverEntity){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"查询司机手机号是否存在");
		} catch (Exception e) {
		}
		logger.info("司机手机号是否存在:checkPhone"+driverEntity.toString());
		Boolean had = this.driverTempService.checkPhone(driverEntity);
		return had;
	}
	
	 *//**
	  * 
	 *导出司机信息操作
	 * @param request
	 * @param response
	 * @return
	 *//*
	@AuthPassport
	@SuppressWarnings("deprecation")
	@RequestMapping("/daochu")
	public void daochu(DriverVoEntity driverEntity,HttpServletRequest request,HttpServletResponse response){
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"导出司机信息");
		} catch (Exception e) {
		}
		logger.info("司机导出操作");
		try {
			//权限  当前用户的权限
			String cities = ToolUtils.getSecurityUser().getCities();
			String suppliers = ToolUtils.getSecurityUser().getSuppliers();
			String teamIds = ToolUtils.getSecurityUser().getTeamId();
			String groupIds = driverEntity.getGroupIds();
			if(driverEntity.getServiceCityId()!=null && !"".equals(driverEntity.getServiceCityId())){
				cities = String.valueOf(driverEntity.getServiceCityId());
			}
			if(driverEntity.getSupplierId()!=null && !"".equals(driverEntity.getSupplierId())){
				suppliers = String.valueOf(driverEntity.getSupplierId());
			}
			if(!"".equals(driverEntity.getTeamIds())&&driverEntity.getTeamIds()!=null){
				teamIds = driverEntity.getTeamIds();
			}
			driverEntity.setCities(cities);
			driverEntity.setSuppliers(suppliers);
			driverEntity.setTeamIds(teamIds);
			driverEntity.setGroupIds(groupIds);
			List<DriverVoEntity> list = driverTempService.queryForListObject(driverEntity);
			list = getGroupName(list);
			Workbook wb = driverTempService.exportExcel(list,request.getRealPath("/")+File.separator+"template"+File.separator+"driver_info.xlsx");
			super.exportExcelFromTemplet(request, response, wb, new String("司机信息列表".getBytes("utf-8"), "iso8859-1"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/deleteDriverInfo", method = { RequestMethod.POST })
	public Object deleteDriverInfo(DriverVoEntity params) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"司机删除");
		} catch (Exception e) {
		}
		logger.info("司机删除:deleteCarInfo");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = this.driverTempService.delete(params);
		} catch (Exception e) {
			logger.error("delete DriverInfo error. ", e);
		}
		return result;
	}
	
	*//**
	 * 司机信息审核
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/shenheDriverInfo")
	public Object shenheDriverInfo(DriverVoEntity params, HttpServletRequest request) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"司机信息审核");
		} catch (Exception e) {
		}
		logger.info("司机信息审核:shenheDriverInfo,参数" + params.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result = this.driverTempService.shenheDriverInfo(params, request);
		return result;
	}
	
	//
	*//*
	 * 查询未绑定车牌号
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value="/licensePlatesList")
	public Object licensePlatesList(CarInfoEntity carInfoEntity) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"查询未绑定车牌号");
		} catch (Exception e) {
		}
		logger.info("licensePlatesList:查询未绑定车牌号");
		// 查询未绑定车牌号
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("cityId",carInfoEntity.getCityId());//默认查北京的未绑定的车牌
		map.put("supplierId",carInfoEntity.getSupplierId());
		map.put("driverId",carInfoEntity.getDriverid());
		List<CarInfoEntity> carList = carTempService.selectcarnum(map);
		return carList;
	}
	
	@AuthPassport
	@RequestMapping(value = "/importExport", method = { RequestMethod.GET })
	public String importExport(int value) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_QUERY,"车辆、司机批量审核");
		} catch (Exception e) {
		}
		logger.info("importExport:车辆、司机批量审核");
		if(value==1){
			return "temporary/car/importExportCar";
		}else{
			return "temporary/driver/importExportDriver";
		}
	}
	
	*//**
	 * 车辆信息批量审核
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/importExportCar")
	public Object importExportCar(CarInfoEntity params,HttpServletRequest request) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"车辆信息批量审核");
		} catch (Exception e) {
		}
		logger.info("车辆信息批量审核:importExportCar,参数" + params.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result = this.carTempService.importExportCar(params,request);
		return result;
	}
	
	*//**
	 * 司机信息批量审核
	 *//*
	@AuthPassport
	@ResponseBody
	@RequestMapping(value = "/importExportDriver")
	public Object importExportDriver(DriverVoEntity params,HttpServletRequest request) {
		try {
			logService.insertLog(com.zhuanche.security.tool.Constants.LOG_TYPE_EXIT,"司机信息批量审核");
		} catch (Exception e) {
		}
		logger.info("司机信息批量审核:importExportDriver,参数" + params.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result = this.driverTempService.importExportDriver(params,request);
		return result;
	}*/
}