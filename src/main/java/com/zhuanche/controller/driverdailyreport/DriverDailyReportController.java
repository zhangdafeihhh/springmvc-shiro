package com.zhuanche.controller.driverdailyreport;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.DriverDailyReportDTO;
import com.zhuanche.entity.common.DriverDailyReportBean;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.MyRestTemplate;
import mapper.mdbcarmanage.ex.CarRelateGroupExMapper;
import mapper.mdbcarmanage.ex.DriverDailyReportExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *   司机日报、周报、月报查询接口
 *   @Auther  wanghongdong
 */
@Controller
@RequestMapping(value = "/driverDailyReport")
public class DriverDailyReportController extends DriverQueryController {

	private static Log log =  LogFactory.getLog(DriverDailyReportController.class);
	
	@Autowired
	private DriverDailyReportExMapper driverDailyReportExMapper;
	
	@Autowired
	private DataPermissionHelper dataPermissionHelper;
	
	@Autowired
	private CarRelateGroupExMapper carRelateGroupExMapper;
	
	
	@Autowired
	@Qualifier("busOrderCostTemplate")
	private MyRestTemplate busOrderCostTemplate;
	
	@RequestMapping("/list")
	public String list(){
		return "driverdailyreport/driverlist";
	}
	
	/**
	 * <p>Title: queryDriverWeekReportDataNew</p>  
	 * <p>Description: </p>  
	 * @param driverDailyReportBean 查询参数
	 * @param page  当前页
	 * @param pageSize  页面展示数量
	 * @param reportType  报表类型  0 日报 1周报 2 月报  默认为0，查询日报
	 * @return  
	 * return: AjaxResponse
	 */
	@ResponseBody
	@RequestMapping(value = "/queryDriverReportData")
	public AjaxResponse queryDriverWeekReportDataNew(DriverDailyReportBean driverDailyReportBean, Integer page, Integer pageSize, Integer reportType){
		log.info("司机周报列表数据:queryDriverDailyReportData");
		reportType = reportType == null ? 0 : reportType;
		int total = 0;
		//判断权限   如果司机id为空为查询列表页
		if(StringUtils.isEmpty(driverDailyReportBean.getDriverIds())){
			String driverList = "";
			//如果页面输入了小组id
			if(StringUtils.isNotEmpty(driverDailyReportBean.getGroupIds())){
				//通过小组id查询司机id, 如果用户
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(null, String.valueOf(driverDailyReportBean.getGroupIds()));
				//如果该小组下无司机，返回空
				if(StringUtils.isEmpty(driverList)){
					log.info("司机日报列表-有选择小组查询条件-该小组下没有司机groupId=="+driverDailyReportBean.getGroupIds());
					PageDTO pageDTO = new PageDTO(page, pageSize, total, null);
					return AjaxResponse.success(pageDTO);
				}
			}
			driverDailyReportBean.setDriverIds(driverList);
		}
		//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限 
		driverDailyReportBean = this.chuliDriverDailyReportEntity(driverDailyReportBean);
		List<DriverDailyReport> list = null;
		//开始查询
		Page<DriverDailyReport> p = PageHelper.startPage(page, pageSize);
		try {
			if ( reportType==0 ) {
				list = this.driverDailyReportExMapper.queryForListObject(driverDailyReportBean);
			}else if ( reportType==1 ) {
				list = this.driverDailyReportExMapper.queryWeekForListObject(driverDailyReportBean);
			} else if ( reportType==2 ) {
				list = this.driverDailyReportExMapper.queryForListObject(driverDailyReportBean);
			}
			total = (int) p.getTotal();
		} finally {
			PageHelper.clearPage();
		}
		//如果不为空，进行查询供应商名称
		List<DriverDailyReportDTO> dtoList = this.selectSuppierNameAndCityNameDays(list);
		PageDTO pageDTO = new PageDTO(page, pageSize, total, dtoList);
		return AjaxResponse.success(pageDTO);
	}


	/**
	 * <p>Title: queryDriverWeekReportDataNew</p>
	 * <p>Description: </p>
	 * @param driverDailyReportBean 查询参数
	 * @param reportType  报表类型  0 日报 1周报 2 月报  默认为0，查询日报
	 * @return
	 * return: AjaxResponse
	 */
	@RequestMapping(value = "/exportDriverReportData")
	public void exportDriverReportData(DriverDailyReportBean driverDailyReportBean, Integer reportType, HttpServletRequest request,HttpServletResponse response) {
		try {
			log.info("司机周报列表数据:queryDriverDailyReportData");
			reportType = reportType == null ? 0 : reportType;

			List<DriverDailyReportDTO> rows = new ArrayList<>();
			List<DriverDailyReport> list = new ArrayList<>();

			int total = 0;
			//判断权限   如果司机id为空为查询列表页
			String driverList = null;
			//如果页面输入了小组id
			if(StringUtils.isNotEmpty(driverDailyReportBean.getGroupIds())){
				//通过小组id查询司机id, 如果用户
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(null, String.valueOf(driverDailyReportBean.getGroupIds()));
				//如果该小组下无司机，返回空
				if(StringUtils.isEmpty(driverList)){
					log.info("司机日报列表-有选择小组查询条件-该小组下没有司机groupId=="+driverDailyReportBean.getGroupIds());
					list = new ArrayList<DriverDailyReport>();
				}
			}
			if(StringUtils.isNotEmpty(driverDailyReportBean.getGroupIds()) && (StringUtils.isEmpty(driverList))){
			}else{
				driverDailyReportBean.setDriverIds(driverList);
				//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
				driverDailyReportBean = this.chuliDriverDailyReportEntity(driverDailyReportBean);
				//开始查询
				Page<DriverDailyReport> p = PageHelper.startPage(1, 500);
				try {
					if ( reportType==0 ) {
						list = this.driverDailyReportExMapper.queryForListObject(driverDailyReportBean);
					}else if ( reportType==1 ) {
						list = this.driverDailyReportExMapper.queryWeekForListObject(driverDailyReportBean);
					} else if ( reportType==2 ) {
						list = this.driverDailyReportExMapper.queryForListObject(driverDailyReportBean);
					}
					total = (int) p.getTotal();
				} finally {
					PageHelper.clearPage();
				}
				rows = this.selectSuppierNameAndCityNameDays(list);
			}
			Workbook wb = this.exportExcel(rows,request.getRealPath("/")+ File.separator+"template"+File.separator+"driverDailyReport_info.xlsx");
			this.exportExcelFromTemplet(request, response, wb, new String("司机周/月报列表".getBytes("gb2312"), "iso8859-1"));
		} catch (Exception e) {
			log.error("导出失败哦！");
		}
	}

	/**
	 * <p>Title: selectSuppierNameAndCityNameDays</p>
	 * <p>Description: 转换</p>
	 * @param rows
	 * @return
	 * return: List<DriverDailyReportDTO>
	 */
	public List<DriverDailyReportDTO> selectSuppierNameAndCityNameDays(List<DriverDailyReport> rows){
		List<DriverDailyReportDTO> list = null;
		//不为空进行转换并查询城市名称和供应商名称
		if(rows!=null&&rows.size()>0){
			list = BeanUtil.copyList(rows, DriverDailyReportDTO.class);
			for(DriverDailyReportDTO driverDailyReport : list){
				//查询城市名称和供应商名称
				Map<String, Object> result = this.querySupplierName(driverDailyReport.getCityId(), driverDailyReport.getSupplierId());
				driverDailyReport.setCityName((String)result.get("cityName"));
				driverDailyReport.setSupplierName((String)result.get("supplierName"));
				//司机营业信息查询
				this.modifyDriverVolume(driverDailyReport, driverDailyReport.getStatDate());
			}
		}
		return list;
	}

	/**
	 * <p>Title: chuliDriverDailyReportEntity</p>
	 * <p>Description: 根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限 </p>
	 * @param driverDailyReportBean
	 * @return
	 * return: DriverDailyReportDTO
	 */
	public DriverDailyReportBean  chuliDriverDailyReportEntity(DriverDailyReportBean driverDailyReportBean){
		//整理排序字段
		if(!"".equals(driverDailyReportBean.getSortname())&&driverDailyReportBean.getSortname()!=null){
			String sortName = pingSortName(driverDailyReportBean.getSortname());
			driverDailyReportBean.setSortname(sortName);
		}
		//获取当前 用户
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
		//当前用户权限
		String cities = currentLoginUser.getCityIds().toString();
		String suppliers = currentLoginUser.getSupplierIds().toString();
		String teamIds = currentLoginUser.getTeamIds().toString();
		if("".equals(driverDailyReportBean.getCities())||driverDailyReportBean.getCities()==null){
			driverDailyReportBean.setCities(cities.substring(1, cities.length()-1));
		}
		if("".equals(driverDailyReportBean.getSuppliers())||driverDailyReportBean.getSuppliers()==null){
			driverDailyReportBean.setSuppliers(suppliers.substring(1, suppliers.length()-1));
		}
		if("".equals(driverDailyReportBean.getTeamIds())||driverDailyReportBean.getTeamIds()==null){
			driverDailyReportBean.setTeamIds(teamIds.substring(1, teamIds.length()-1));
		}
		return driverDailyReportBean;
	}

	/**
	 * 
	 * <p>Title: modifyDriverVolume</p>  
	 * <p>Description: 司机营业信息查询</p>  
	 * @param ddre
	 * @param statDateStart  
	 * return: void
	 */
	private void modifyDriverVolume(DriverDailyReportDTO ddre, String statDateStart) {
		try {
			String url = "/driverIncome/getDriverIncome?driverId="+ddre.getDriverId()+"&incomeDate=" + statDateStart;
			String result = busOrderCostTemplate.getForObject(url, String.class);
			
			Map<String, Object> resultMap = JSONObject.parseObject(result, HashMap.class);
			if (null == resultMap || !String.valueOf(resultMap.get("code")).equals("0")) {
				log.info("查询接口【/driverIncome/getDriverIncome】返回异常,code:"+String.valueOf(resultMap.get("code")));
				return;
			}
			
			String reData = String.valueOf(resultMap.get("data"));
			if (StringUtils.isBlank(reData)) {
				log.info("查询接口【/driverIncome/getDriverIncome】返回data为空.");
				return;
			}
			
			Map dataMap = JSONObject.parseObject(String.valueOf(resultMap.get("data")), Map.class);
			if (dataMap!=null){
				String driverIncome = String.valueOf(dataMap.get("driverIncome"));
				if (StringUtils.isBlank(driverIncome)) {
					log.info("查询接口【/driverIncome/getDriverIncome】返回driverIncome为空.");
				}

				JSONObject jsonObject = JSONObject.parseObject(driverIncome);

				// 当日完成订单量
				Integer orderCounts= Integer.valueOf(String.valueOf(jsonObject.get("orderCounts")));
				ddre.setOperationNum(orderCounts);
				// 当日营业额
				BigDecimal todayIncomeAmount = new BigDecimal(String.valueOf(jsonObject.get("todayIncomeAmount")));
				ddre.setActualPay(todayIncomeAmount.doubleValue());
				// 当日载客里程
				BigDecimal todayTravelMileage = new BigDecimal(String.valueOf(jsonObject.get("todayTravelMileage")));
				ddre.setServiceMileage(todayTravelMileage.doubleValue());
				// 当日司机代付价外费
				BigDecimal todayOtherFee = new BigDecimal(String.valueOf(jsonObject.get("todayOtherFee")));
				ddre.setDriverOutPay(todayOtherFee.doubleValue());
				// 当日司机代收
				BigDecimal todayDriverPay = new BigDecimal(String.valueOf(jsonObject.get("todayDriverPay")));
			}
		} catch (RestClientException e) {
			log.error("查询接口【/driverIncome/getDriverIncome】返回异常.",e);
		} catch (NumberFormatException e) {
			log.error("查询接口【/driverIncome/getDriverIncome】返回异常.",e);
		}
	}

	/**
	 * 整理excel 行
	 * @param list
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Workbook exportExcel(List<DriverDailyReportDTO> list, String path)
			throws Exception {
		FileInputStream io = new FileInputStream(path);
		Workbook wb = new XSSFWorkbook(io);
		if (list != null && list.size() > 0) {
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = null;
			int i = 1;
			for (DriverDailyReportDTO s : list) {
				Row row = sheet.createRow(i + 1);
				cell = row.createCell(0);
				cell.setCellValue(s.getLicensePlates() != null ? ""
						+ s.getLicensePlates() + "" : "");

				cell = row.createCell(1);
				cell.setCellValue(s.getDriverName() != null ? ""
						+ s.getDriverName() + "" : "");

				cell = row.createCell(2);
				cell.setCellValue(s.getSupplierName() != null ? ""
						+ s.getSupplierName() + "" : "");

				cell = row.createCell(3);
				cell.setCellValue(s.getTeamName()!= null ? ""
						+ s.getTeamName() + "" : "");

				cell = row.createCell(4);
				cell.setCellValue(s.getGroupName()!= null ? ""
						+ s.getGroupName() + "" : "");

				cell = row.createCell(5);
				cell.setCellValue(s.getUpOnlineTime());

				cell = row.createCell(6);
				cell.setCellValue(s.getOnlineTime());

				cell = row.createCell(7);
				cell.setCellValue(s.getForcedTime());

				cell = row.createCell(8);
				cell.setCellValue(s.getTravelTimeStart());

				cell = row.createCell(9);
				cell.setCellValue(s.getTravelMileageStart());

				cell = row.createCell(10);
				cell.setCellValue(s.getTravelTime());

				cell = row.createCell(11);
				cell.setCellValue(s.getTravelMileage());

				cell = row.createCell(12);
				cell.setCellValue(s.getServiceTime());

				cell = row.createCell(13);
				cell.setCellValue(s.getServiceMileage());

				cell = row.createCell(14);
				cell.setCellValue(s.getTravelTimeEnd());

				cell = row.createCell(15);
				cell.setCellValue(s.getTravelMileageEnd());

				cell = row.createCell(16);
				cell.setCellValue(s.getActualPay());

				cell = row.createCell(17);
				cell.setCellValue(s.getDriverOutPay());

				cell = row.createCell(18);
				cell.setCellValue(s.getAssignOrderNum());

				cell = row.createCell(19);
				cell.setCellValue(s.getContendOrderNum());

				cell = row.createCell(20);
				cell.setCellValue(s.getPlatformOrderNum());

				cell = row.createCell(21);
				cell.setCellValue(s.getGetPlaneNum());

				cell = row.createCell(22);
				cell.setCellValue(s.getOutPlaneNum());

				cell = row.createCell(23);
				cell.setCellValue(s.getOperationNum());

				cell = row.createCell(24);
				cell.setCellValue(s.getStatDate());

				i++;
			}
		}
		return wb;
	}

}