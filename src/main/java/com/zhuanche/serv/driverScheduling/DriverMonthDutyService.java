package com.zhuanche.serv.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.dutyEnum.EnumDriverMonthDutyStatus;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driverDuty.CarDriverMonthDTO;
import com.zhuanche.dto.driverDuty.ColumnEntity;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMonthDuty;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.request.DriverMonthDutyRequest;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.util.Check;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.DriverUtils;
import com.zhuanche.util.MobileOverlayUtil;
import mapper.mdbcarmanage.CarDriverMonthDutyMapper;
import mapper.mdbcarmanage.ex.CarDriverMonthDutyExMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: ???????????????
 *
 * <PRE>
 * <BR>	????????????
 * <BR>-----------------------------------------------
 * <BR>	????????????			?????????			????????????
 * </PRE>
 *
 * @author lunan
 * @version 1.0
 * @since 1.0
 * @create: 2018-09-04 12:54
 *
 */
@Service
public class DriverMonthDutyService {

	private static final Logger logger = LoggerFactory.getLogger(DriverMonthDutyService.class);

	@Autowired
	private CarDriverTeamExMapper carDriverTeamExMapper;

	@Autowired
	private CarBizCityExMapper carBizCityExMapper;

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;

	@Autowired
	private CarRelateTeamExMapper carRelateTeamExMapper;

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	@Autowired
	private CarDriverMonthDutyExMapper carDriverMonthDutyExMapper;

	@Autowired
	private CarDriverMonthDutyMapper carDriverMonthDutyMapper;

	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;
	@Autowired
	private CarDriverTeamService carDriverTeamService;
	/**
	 * @Desc: ??????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public Map<String, Object> importDriverMonthDuty(
			DriverMonthDutyRequest params, HttpServletRequest request,MultipartFile file) {

		String resultError1 = "-1";//????????????
		String resultErrorMag1 = "????????????????????????!";

		Map<String,Object> result = new HashMap<String,Object>();
		List<DriverMonthDutyRequest> driverMonthDutyList = new ArrayList<DriverMonthDutyRequest>();
		List<DriverMonthDutyRequest> updateDriverMonthDutyList = new ArrayList<DriverMonthDutyRequest>();
//		String fileName = params.getFileName(); // ???????????? ???????????? ??????
//		String dirPath = request.getSession().getServletContext().getRealPath("/upload/"+params.getFileName());
//		String path  = Common.getPath(request);
//		String dirPath = path+params.getFileName();
//		File DRIVERINFO = new File(dirPath);
		CommonsMultipartFile commonsmultipartfile = null;
		try{
			commonsmultipartfile = (CommonsMultipartFile) file;
		}catch (Exception e){
			logger.error("?????????????????????", e);
			result.put("result", "0");
			result.put("msg","???????????????");
			return result;
		}
		String fileName = commonsmultipartfile.getOriginalFilename();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String time ="";
		if((!"".equals(params.getMonitorDate())&&params.getMonitorDate()!=null&&!"null".equals(params.getMonitorDate()))){
			time = params.getMonitorDate();
		}else{
			time = sdf.format(new Date());
		}
		int year = Integer.parseInt(time.substring(0, 4));
		int month = Integer.parseInt(time.substring(5, 7)); // ??????
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE); // ??????????????????
		int maxCol = maxDate + 5;
		String monthStr = time.substring(5, 7);

		try {
			// ??????????????????, ???????????????????????????????????????
			if (sdf.parse(time).before(sdf.parse(sdf.format(new Date())))) {
				result.put("result", resultError1);
				result.put("msg", "????????????????????????????????????");
				return result;
			}
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd");
			String nowDay = sdf1.format(new Date()); // ????????????

			InputStream is = commonsmultipartfile.getInputStream();
			Workbook workbook = null;
			String fileType = fileName.split("\\.")[1];
			if (fileType.equals("xls")) {
				workbook = new HSSFWorkbook(is);
			}
			else if (fileType.equals("xlsx")) {
				workbook = new XSSFWorkbook(is);
			}else {
				workbook = new XSSFWorkbook(is);
			}
			Sheet sheet = workbook.getSheetAt(0);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			// ????????????????????????
			Row row1 = sheet.getRow(1);

			for (int colIx = 0; colIx < maxCol; colIx++) {
				Cell cell = row1.getCell(colIx); // ???????????????
				CellValue cellValue = evaluator.evaluate(cell); // ???????????????
				if (cell == null || cellValue == null) {
					result.put("result", resultError1);
					result.put("msg", resultErrorMag1);
					return result;
				} else {
					if (colIx == 0) {
						if (!cellValue.getStringValue().contains("????????????")) {
							result.put("result", resultError1);
							result.put("msg", resultErrorMag1);
							return result;
						}
					} else if (colIx == 1) {
						if (!cellValue.getStringValue().contains("??????")) {
							result.put("result", resultError1);
							result.put("msg", resultErrorMag1);
							return result;
						}
					} else if (colIx == 2) {
						if (!cellValue.getStringValue().contains("????????????")) {
							result.put("result", resultError1);
							result.put("msg", resultErrorMag1);
							return result;
						}
					} else if (colIx == 3) {
						if (!cellValue.getStringValue().contains("????????????")) {
							result.put("result", resultError1);
							result.put("msg", resultErrorMag1);
							return result;
						}
					} else if (colIx == 4) {
						if (!cellValue.getStringValue().contains("?????????")) {
							result.put("result", resultError1);
							result.put("msg", resultErrorMag1);
							return result;
						}
					} else if (colIx > 4) {
						int day_num = colIx - 4;
						if (day_num < 10) {
							if (!cellValue.getStringValue().contains(monthStr +"-0" + day_num)) {
								result.put("result", resultError1);
								result.put("msg", resultErrorMag1);
								return result;
							}
						} else if (day_num <= maxDate) {
							if (!cellValue.getStringValue().contains(monthStr +"-" + day_num)) {
								result.put("result", resultError1);
								result.put("msg", resultErrorMag1);
								return result;
							}
						} else if (day_num > maxDate) {
							break;
						}
					}
				}
			}

			int minRowIx = 2;// ????????????????????????????????????????????????
			int maxRowIx = sheet.getLastRowNum(); // ???????????????????????????
			Set<String> driverIdSet = new HashSet<String>();
			for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
				Row row = sheet.getRow(rowIx); // ???????????????
				if(row==null){
					continue;
				}
				DriverMonthDutyRequest driverMonthDutyEntity = new DriverMonthDutyRequest();
				driverMonthDutyEntity.setMonitorDate(params.getMonitorDate().trim());
				CarDriverInfoDTO driverEntity = null;
				boolean isTrue = true;// ???????????????????????????
				boolean hasDuty = false; // ?????????????????? false ????????????true ?????????
				CarDriverMonthDTO oldRecord = null; // ?????????
				String data = null;
				// ????????????????????????????????????????????????+5??????
				for (int colIx = 0; colIx < maxCol; colIx++) {
					Cell cell = row.getCell(colIx); // ???????????????
					CellValue cellValue = null;
					if (null != cell) {
						cellValue = evaluator.evaluate(cell); // ???????????????
					}
					String cellStringValue = null;
					if (cellValue == null || StringUtils.isEmpty(cellValue.getStringValue())) {
						if (colIx > 4) {
							cellStringValue = "??????";
						}
					} else {
						cellStringValue = cellValue.getStringValue().trim();
					}
					DynamicRoutingDataSource.DataSourceMode mdbcarManageMode = DynamicRoutingDataSource.getMasterSlave("mdbcarmanage-DataSource");
					try{
						if (colIx == 0) {
							logger.info("???????????????"+cellStringValue);
							if (cellStringValue == null) {
								isTrue = false;
							} else {
								if (driverIdSet.contains(cellStringValue)) { // ?????????????????????????????????????????????????????????
									isTrue = false;
									continue;
								} else {
									driverIdSet.add(cellStringValue);
								}
								driverEntity = carBizDriverInfoExMapper.selectDriverDetail(cellValue.getStringValue());
								if (null == driverEntity) {
									isTrue = false;
									break;
								}
								driverMonthDutyEntity.setDriverId(Integer.parseInt(cellValue.getStringValue()));
								DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.SLAVE);
								List<CarDriverMonthDTO> oldRecordList = carDriverMonthDutyExMapper.queryDriverDutyList(driverMonthDutyEntity);
								if (null != oldRecordList && !oldRecordList.isEmpty()) {
									hasDuty = true;
									oldRecord = oldRecordList.get(0);
									driverMonthDutyEntity.setId(oldRecord.getId());
								}
							}
						} else if (colIx == 1) { // ??????
							logger.info("?????????"+cellStringValue);
							Map<String, Object> teamInfo = null;
							try {
								teamInfo = carDriverTeamExMapper.queryTeamIdByDriverId(Integer.parseInt(driverEntity.getDriverId()));
								if (cellStringValue != null
										&& !(teamInfo != null
										&& !StringUtils.isEmpty(teamInfo.get("teamName").toString())
										&& teamInfo.get("teamName").toString().equals(cellValue.getStringValue()))) { // ??????????????????????????????????????????
									isTrue = false;
								} else {
									if (teamInfo != null
											&& !StringUtils.isEmpty(teamInfo.get("teamId").toString())) {
										driverMonthDutyEntity.setTeamId(teamInfo.get("teamId").toString());
									}
								}
							} catch (Exception e) {
								isTrue = false;
								break;
							}

						} else if (colIx == 2) { // ????????????
							logger.info("???????????????"+cellStringValue);
							if (cellStringValue != null
									&& !((driverEntity.getName() != null
									&& !StringUtils.isEmpty(driverEntity.getName()))
									&& driverEntity.getName().equals(cellValue.getStringValue()))) { // ??????????????????????????????????????????
								isTrue = false;
							} else {
								driverMonthDutyEntity.setDriverName(driverEntity.getName());
							}
						} else if (colIx == 3) { // ????????????
							logger.info("???????????????"+cellStringValue);
							if (cellStringValue == null
									|| !("??????".equals(cellStringValue) || "??????".equals(cellStringValue))) {
								isTrue = false;
							} else if ("??????".equals(cellStringValue)) {
								if (null != driverEntity.getStatus() && 1 == driverEntity.getStatus().intValue()) {
									driverMonthDutyEntity.setStatus(1);
								} else {
									isTrue = false;
								}
							} else if ("??????".equals(cellStringValue)) {
								if (null != driverEntity.getStatus() && 0 == driverEntity.getStatus().intValue()) {
									isTrue = false;
								} else {
									isTrue = false;
								}
							}
						} else if (colIx == 4) { // ?????????
							logger.info("????????????"+cellStringValue);
							if (cellStringValue != null
									&& !((driverEntity.getName() != null
									&& !StringUtils.isEmpty(driverEntity.getLicensePlates()))
									&& driverEntity.getLicensePlates().equals(cellValue.getStringValue()))) { // ??????????????????????????????????????????
								isTrue = false;
							} else {
								driverMonthDutyEntity.setLicensePlates(driverEntity.getLicensePlates());
							}
						} else if (colIx > 4) { // ???????????????
							int day_num = colIx - 4;
							String key = null;
							if (day_num < 10) {
								key = monthStr +"-0" + day_num;
							} else if (day_num <= maxDate) {
								key = monthStr +"-" + day_num;
							} else if (day_num > maxDate) {
								break;
							}
							logger.info(key + "?????????"+cellStringValue);
							if (1 == day_num) {
								data = "{";
							} else {
								data += ",";
							}
							data += key +":";
							if (null != EnumDriverMonthDutyStatus.getValue(cellStringValue)) {
								data += EnumDriverMonthDutyStatus.getValue(cellStringValue);
							} else {
								isTrue = false;
								continue;
							}
							if (maxDate == day_num) {
								data += "}";
								driverMonthDutyEntity.setData(data);
							}
						}
					}finally {
						DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource",mdbcarManageMode);
					}
				}// ???????????????
				//cityId
				//driverPhone
				//supplierId
				//groupId
				if (isTrue && driverMonthDutyEntity != null && driverEntity != null) {
					driverMonthDutyEntity.setCityId("" + driverEntity.getCityId());
					driverMonthDutyEntity.setDriverPhone(driverEntity.getPhone());
					if (null != driverEntity.getSupplierId()) {
						driverMonthDutyEntity.setSupplierId(driverEntity.getSupplierId().toString());
					}
					driverMonthDutyEntity.setGroupId(String.valueOf(driverEntity.getGroupId()));
					if (hasDuty) { // ??????????????????
						// ??????????????????????????????????????????
						String newData = this.getNewData(oldRecord.getData(), driverMonthDutyEntity.getData(), nowDay);
						driverMonthDutyEntity.setData(newData);
						updateDriverMonthDutyList.add(driverMonthDutyEntity);
					} else { // ??????????????????
						String newData = this.getNewData(null, driverMonthDutyEntity.getData(), nowDay);
						driverMonthDutyEntity.setData(newData);
						driverMonthDutyList.add(driverMonthDutyEntity);
					}
				}
			}
		}catch (Exception e) {
			logger.error("??????????????????", e);
			result.put("result", "0");
			result.put("msg","???????????????");
			return result;
		}
		String download = "";
		if(("".equals(driverMonthDutyList)||driverMonthDutyList==null||driverMonthDutyList.size()==0)
				&& ("".equals(updateDriverMonthDutyList)||updateDriverMonthDutyList==null||updateDriverMonthDutyList.size()==0)){
			result.put("result", "0");
			result.put("msg","???????????????");
		}else{
			//??????????????????
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("driverList", driverMonthDutyList);
			int count = 0;
			// ????????????
			if (!("".equals(driverMonthDutyList)||driverMonthDutyList==null||driverMonthDutyList.size()==0)) {
				count = carDriverMonthDutyExMapper.saveDriverMonthDutyList(paramMap);
			}
			if (!("".equals(updateDriverMonthDutyList)||updateDriverMonthDutyList==null||updateDriverMonthDutyList.size()==0)) {
				String yearMonthStr = time.substring(0, 7);
				for(DriverMonthDutyRequest param : updateDriverMonthDutyList){
					count += carDriverMonthDutyExMapper.updateDriverMonthDutyOne(param);
					RedisCacheUtil.delete(Constants.REDISKEYPREFIX_ISINDUTY+"_"+yearMonthStr+"_"+param.getDriverId());
				}
//				count += carDriverMonthDutyExMapper.updateDriverMonthDutyList(paramMap);
			}

			if (count > 0) {
				result.put("result", "1");
				result.put("msg","???????????????????????????" + count + "????????????");
			} else {
				result.put("result", "0");
				result.put("msg","???????????????");
			}
		}
		/*try {
			// ?????????????????????
			if(listException.size() > 0) {
				Workbook wb = exportExcel(request.getServletContext().getRealPath(File.separator)+ "template" + File.separator + "driverMonthDuty_exception.xlsx", listException);
				download = exportExcelFromTempletToLoacl(request, wb,new String("ERROR".getBytes("utf-8"), "iso8859-1") );
			}
		}catch(Exception e){
			e.printStackTrace();
		}*/
		/*if(!"".equals(download)&&download!=null){
			result.put("download",download);
		}else{
			result.put("download","");
		}*/
		return result;
	}

	/**
	 * ??????????????????:
	 * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????????????????2????????????????????????????????????????????????
	 * @param oldData
	 * @param
	 * @param nowDay
	 * @return
	 */
	private String getNewData(String oldData, String inputData, String nowDay) {
		String newData = null;
		int index = inputData.indexOf(nowDay);
		if (index == -1) {
			newData = inputData;
		} else {
			if (null != oldData) {
				int oldIndex = oldData.indexOf(nowDay);
				newData = oldData.substring(0, oldIndex) + inputData.substring(index);
			} else {
				newData = "{" + inputData.substring(index);
			}
		}

		return newData;
	}

	/**
	 * @Desc: ??????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.MASTER )
	} )
	public int updateDriverMonthDutyData(CarDriverMonthDuty record){
		if(Check.NuNObj(record)){
			return 0;
		}
		int result = carDriverMonthDutyExMapper.updateDriverMonthDutyData(record);
		if(result > 0){
			String yearMonthStr = DateUtils.formatDate(record.getTime(), "yyyy-mm");
			RedisCacheUtil.delete(Constants.REDISKEYPREFIX_ISINDUTY+"_"+yearMonthStr+"_"+record.getDriverId());
		}
		return result;
	}

	/**
	 * @Desc: ?????????????????????By Id
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public CarDriverMonthDuty selectByPrimaryKey(Integer id){
		CarDriverMonthDuty month = carDriverMonthDutyMapper.selectByPrimaryKey(id);
		return month;
	}


	/**
	 * @Desc: ??????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public List<Map<String,Object>> queryDriverIdsByTeamIds(Set<Integer>  teamIds){
		return carRelateTeamExMapper.queryDriverIdsByTeamIds(teamIds);
	}

	/**
	 * @Desc: ?????????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public PageInfo<CarDriverMonthDTO> queryDriverDutyList(DriverMonthDutyRequest param){
		try{
			logger.info("??????????????????service?????????"+ JSON.toJSONString(param));
			//??????team??????
			Set<Integer> teamIds = param.getTeamIds();

			if(teamIds != null && !teamIds.isEmpty()){
				//??????????????????,???????????????????????????????????????????????????
				if(StringUtils.isNotEmpty(param.getTeamId())){
					Integer testTeamId = Integer.parseInt(param.getTeamId());
					if(!teamIds.contains(testTeamId)){//???????????????????????????????????????????????????
						PageInfo<CarDriverMonthDTO> pageInfo = new PageInfo(new ArrayList());
						return pageInfo;
					}
				}else {
					teamIds = new HashSet<>();
					/*teamIds.add(Integer.parseInt(param.getTeamId()));*/
					Set<Integer> authDriverIds = DriverUtils.getDriverIdsByUserTeamsV2(carDriverTeamService,teamIds);
					if(authDriverIds != null && !authDriverIds.isEmpty()){
						param.setDriverIds(authDriverIds);
					}
				}
			}


			if(StringUtils.isNotEmpty(param.getDriverName()) || StringUtils.isNotEmpty(param.getLicensePlates())){
				//?????????????????????????????????????????????ids
				CarBizDriverInfoDTO driverSearchParam = new CarBizDriverInfoDTO();
				driverSearchParam.setCityIds(param.getCityIds());
				driverSearchParam.setSupplierIds(param.getSupplierIds());
				if(StringUtils.isNotEmpty(param.getCityId())){
					driverSearchParam.setServiceCity(Integer.parseInt(param.getCityId()));
				}
				if(StringUtils.isNotEmpty(param.getSupplierId())){
					driverSearchParam.setSupplierId(Integer.parseInt(param.getSupplierId()));
				}
				driverSearchParam.setDriverIds(param.getDriverIds());

				driverSearchParam.setName(param.getDriverName());
				driverSearchParam.setLicensePlates(param.getLicensePlates());

				List<CarBizDriverInfoDTO> driverInfoDTOList = carBizDriverInfoService.queryDriverList(driverSearchParam);

				if(driverInfoDTOList == null ){
					//??????????????????????????????????????????
					PageInfo<CarDriverMonthDTO> pageInfo = new PageInfo(new ArrayList());
					return pageInfo;

				}else{
					Set<Integer>  driverIdSet = new HashSet<>();
					for(CarBizDriverInfoDTO item : driverInfoDTOList){
						driverIdSet.add(item.getDriverId());
					}
					//????????????id??????
					param.setDriverIds(driverIdSet);
				}
			}
			PageInfo<CarDriverMonthDTO> pageInfo = PageHelper.startPage(param.getPageNo(),param.getPageSize(),true).doSelectPageInfo(
					()->carDriverMonthDutyExMapper.queryDriverDutyList(param));
			logger.info("??????????????????service??????"+ (param==null?"null":JSON.toJSONString(param))
				+",????????????"+pageInfo.getTotal()+";????????????"+pageInfo.getPages()
			);
			List<CarDriverMonthDTO> list = pageInfo.getList();
			if(Check.NuNCollection(list)){
				//?????????
				return pageInfo;
			}
			Set<Integer> driverIdSet = new HashSet<>();
			for (CarDriverMonthDTO month : list) {
				driverIdSet.add(month.getDriverId());
			}
			Map<String,CarDriverInfoDTO> cache = new HashMap<>();
			if(!driverIdSet.isEmpty()){
				List<CarDriverInfoDTO> driverInfoDTOS = carBizDriverInfoExMapper.queryListDriverByDriverIds(driverIdSet);
				if(driverInfoDTOS != null){
					for(CarDriverInfoDTO item : driverInfoDTOS){
						cache.put("d_"+item.getDriverId(),item);
					}
				}
			}
			for (CarDriverMonthDTO month : list) {
				CarDriverInfoDTO info = cache.get("d_"+month.getDriverId());
				if(!Check.NuNObj(info)){
					month.setStatus(info.getStatus());
				}
				month.setDriverPhone(MobileOverlayUtil.doOverlayPhone(month.getDriverPhone()));
			}

			return pageInfo;
		}catch (Exception e){
			logger.error("??????????????????service??????:{}",e);
			return null;
		}finally {
			PageHelper.clearPage();
		}
	}

	private Workbook create(InputStream inp) throws IOException,InvalidFormatException {
		if (!inp.markSupported()) {
			inp = new PushbackInputStream(inp, 8);
		}
		if (POIFSFileSystem.hasPOIFSHeader(inp)) {
			return new HSSFWorkbook(inp);
		}
		if (POIXMLDocument.hasOOXMLHeader(inp)) {
			return new XSSFWorkbook(OPCPackage.open(inp));
		}
		throw new IllegalArgumentException("??????excel????????????poi????????????");
	}

	public List<CarDriverMonthDTO> changeDay(List<CarDriverMonthDTO> rows){
		if(!"".equals(rows)&&rows!=null){
			for(int i=0;i<rows.size();i++){
				try {
					String str = rows.get(i).getData();
					if (null == str || "".endsWith(str.trim())) {
						continue;
					}
					// ?????????????????????????????? ??????JSONObject?????????
					str = str.replace(":,", ":\"\",");
					str = str.replace(":}", ":\"\"}");
					logger.info("changeDay,driverId="+rows.get(i).getDriverId()+",data={}"+str);
					JSONObject obj = JSONObject.fromObject(str);
					Map<String,String> map = new HashMap<String,String>();
					@SuppressWarnings("rawtypes")
					Iterator keys=obj.keys();
					while(keys.hasNext()){
						String key=(String) keys.next();
						String value=obj.get(key).toString();
						map.put(key, value);
					}
					rows.get(i).setMap(map);
					if(!"".equals(rows.get(i).getSupplierId())&&rows.get(i).getSupplierId()!=null){
						CarBizSupplier supplier = new CarBizSupplier();
						supplier.setSupplierId(rows.get(i).getSupplierId());
						CarBizSupplier existsSupplier = carBizSupplierExMapper.queryForObject(supplier);
						if(!Check.NuNObj(existsSupplier)){
							rows.get(i).setSupplierName(existsSupplier.getSupplierFullName());
						}
					}
					if(!"".equals(rows.get(i).getCityId())&&rows.get(i).getCityId()!=null){
						Set<Integer> paramSet = new HashSet<>();
						paramSet.add(rows.get(i).getCityId());
						List<CarBizCity> city = carBizCityExMapper.queryByIds(paramSet);
						if(!Check.NuNCollection(city)){
							rows.get(i).setCityName(city.get(0).getCityName());
						}
					}
				} catch (NumberFormatException e) {
					logger.info("changeDay error:" +JSON.toJSONString(e));
				}
			}
		}
		return rows;
	}

	/**
	 * @Desc: ?????????????????????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public List<CarDriverInfoDTO> queryDriverListForMonthDuty(DriverMonthDutyRequest param){
		return carBizDriverInfoExMapper.queryDriverListForMonthDuty(param);
	}

	/**
	 * @Desc: ?????????????????????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public List<CarDriverInfoDTO> queryDriverListInfoForMonthDuty(DriverMonthDutyRequest param, Map<String, Object> driverTeamMap, Set<Integer> teamIds) {
		List<CarDriverInfoDTO> driverEntityList = this.carBizDriverInfoExMapper.queryDriverListForMonthDuty(param);
		if (null != driverEntityList && !driverEntityList.isEmpty()) {
			for (int i = 0; i < driverEntityList.size(); i++) {
				CarDriverInfoDTO driver = driverEntityList.get(i);
				if (!Check.NuNCollection(teamIds)) {
					if (null ==  driverTeamMap) { // ????????????????????????
						return null;
					} else {
						if (null == driverTeamMap.get(driver.getDriverId())) {  // ?????????????????????????????? ??????
							driverEntityList.remove(i);
							i --;
						}
					}
				}
				if (null !=  driverTeamMap && null != driverTeamMap.get(driver.getDriverId())) { // ??????????????????
					driver.setTeamName(driverTeamMap.get(driver.getDriverId()).toString());
				}
			}
		}
		return driverEntityList;
	}


	/**
	 * @Desc: ?????????????????????
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	public Map<String,Object> queryDriverDutyTable(DriverMonthDutyRequest params) {

		Map<String,Object> result = new LinkedHashMap<String,Object>();
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		map.put("driverId", "????????????");
		map.put("teamName", "??????");
		map.put("driverName", "????????????");
		map.put("status", "????????????");
		map.put("licensePlates", "?????????");
		ColumnEntity info = new ColumnEntity();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time ="";
		if((!"".equals(params.getMonitorDate())&&params.getMonitorDate()!=null&&!"null".equals(params.getMonitorDate()))){
			time = params.getMonitorDate();
		}else{
			time = sdf.format(new Date());
		}
		int year = Integer.parseInt(time.substring(0, 4));
		int month = Integer.parseInt(time.substring(5, 7));
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		int maxDate = calendar.get(Calendar.DATE);
		String monthStr = time.substring(5, 7);
		for(int i=1;i<=maxDate;i++){
			if(i<10){
				map.put("day"+i, monthStr+"-0"+i+"");
			}else{
				map.put("day"+i, monthStr+"-"+i+"");
			}
			info.setValue(year,month,i);
		}
		result.put("Rows", map);
		return result;
	}

	/**
	 *
	 * @param monitorDate  ????????????
	 * @return
	 */
	public List<com.alibaba.fastjson.JSONObject> generateTableHeader(String monitorDate){

		List<com.alibaba.fastjson.JSONObject> columnList = new ArrayList<>();

		com.alibaba.fastjson.JSONObject columen1 = new com.alibaba.fastjson.JSONObject();
		columen1.put("proName","driverId");
		columen1.put("showName","????????????");
		columnList.add(columen1);

		com.alibaba.fastjson.JSONObject columen2 = new com.alibaba.fastjson.JSONObject();
		columen2.put("proName","teamName");
		columen2.put("showName","??????");
		columnList.add(columen2);


		com.alibaba.fastjson.JSONObject columen3 = new com.alibaba.fastjson.JSONObject();
		columen3.put("proName","driverName");
		columen3.put("showName","????????????");
		columnList.add(columen3);


		com.alibaba.fastjson.JSONObject columen4 = new com.alibaba.fastjson.JSONObject();
		columen4.put("proName","status");
		columen4.put("showName","????????????");
		columnList.add(columen4);

		com.alibaba.fastjson.JSONObject columen5 = new com.alibaba.fastjson.JSONObject();
		columen5.put("proName","licensePlates");
		columen5.put("showName","?????????");
		columnList.add(columen5);


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time ="";
		if((StringUtils.isNotEmpty(monitorDate) && !"null".equals( monitorDate))){
			time = monitorDate;
		}else{
			time = sdf.format(new Date());
		}
		int year = Integer.parseInt(time.substring(0, 4));
		int month = Integer.parseInt(time.substring(5, 7));
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		int maxDate = calendar.get(Calendar.DATE);
		String monthStr = time.substring(5, 7);

		for(int i=1;i<=maxDate;i++){
			com.alibaba.fastjson.JSONObject columen = new com.alibaba.fastjson.JSONObject();

			if(i<10){

				columen.put("showName", monthStr+"-0"+i+"");
				columen.put("proName", monthStr+"-0"+i+"");
			}else{
				columen.put("showName", monthStr+"-"+i+"");
				columen.put("proName", monthStr+"-"+i+"");
			}

			columnList.add(columen);
		}

		return columnList;

	}







}