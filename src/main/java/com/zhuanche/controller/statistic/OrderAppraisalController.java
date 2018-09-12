package com.zhuanche.controller.statistic;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalBean;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtils;
import mapper.rentcar.ex.CarBizCustomerAppraisalExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 投诉评分--订单评分
 *
 */
@Controller()
@RequestMapping("/orderAppraisal")
public class OrderAppraisalController extends DriverQueryController{

	private static Logger log =  LoggerFactory.getLogger(OrderAppraisalController.class);

	@Autowired
	private CarBizCustomerAppraisalExMapper carBizCustomerAppraisalExMapper;

	@Autowired
	private DriverOutageService driverOutageService;

	/**
	 * 订单评分查询
	 * @param cityId 城市
	 * @param supplierId 供应商
	 * @param teamId 车队
	 * @param groupIds 小组
	 * @param driverName 司机姓名
	 * @param driverPhone 司机手机号
	 * @param orderNo 订单号
	 * @param createDateBegin 开始时间
	 * @param createDateEnd 结束时间
	 * @param evaluateScore 司机评分
	 * @param sortName 排序字段
	 * @param sortOrder 排序
	 * @param page 当前页
	 * @param pageSize 当前展示页数
	 * @return AjaxResponse
	 */
	@ResponseBody
	@RequestMapping("/orderAppraisalListData")
	public AjaxResponse appraisalDataList(String cityId,
										  String supplierId,
										  String teamId,
										  String groupIds,
										  String driverName,
										  @Verify(param="driverPhone",rule="mobile")
													  String driverPhone,
										  String orderNo,
										  @Verify(param="createDateBegin",rule="required")String createDateBegin,
										  @Verify(param="createDateEnd",rule="required")String createDateEnd,
										  String evaluateScore,String sortName, String sortOrder,
										  Integer page,
										  Integer pageSize) {

		if (StringUtils.isEmpty(driverPhone) && StringUtils.isEmpty(teamId)){
			//请选择一个车队号或输入司机手机
			return AjaxResponse.fail(RestErrorCode.TEAMID_OR_DRIVERID_ISNULL);
		}
		CarBizCustomerAppraisalParams params = new CarBizCustomerAppraisalParams(cityId,supplierId,teamId,groupIds,driverName,driverPhone,orderNo,
				createDateBegin,createDateEnd,evaluateScore,sortName,sortOrder,page,pageSize);
		log.info("/web/orderAppraisal/orderAppraisalListData:司机评分数据列表数据---参数："+params.toString());

		int total = 0;
		String driverList = "";
		if(StringUtils.isNotEmpty(params.getGroupIds()) || StringUtils.isNotEmpty(params.getTeamId())){
			driverList = super.queryAuthorityDriverIdsByTeamAndGroup(params.getTeamId(), params.getGroupIds());
			if(driverList==null || "".equals(driverList)){
				log.info("订单评价列表-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
				log.info("订单评价列表-有选择车队查询条件-该车队下没有司机teamId=="+params.getTeamId());
				PageDTO pageDTO = new PageDTO(params.getPage(), params.getPageSize(), total, null);
				return AjaxResponse.success(pageDTO);
			}
		}
		params.setDriverIds(driverList);
		//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
		params = this.chuliParams(params);
		//开始查询
		Page<CarBizCustomerAppraisal> p = PageHelper.startPage(params.getPage(), params.getPageSize());
		List<CarBizCustomerAppraisalBean> list = null;
		try {
			List<CarBizCustomerAppraisal> appraisalList = this.carBizCustomerAppraisalExMapper.queryForListObject(params);
			list = BeanUtil.copyList(appraisalList,CarBizCustomerAppraisalBean.class);
			total = (int) p.getTotal();
		} finally {
			PageHelper.clearPage();
		}
		PageDTO pageDTO = new PageDTO(params.getPage(), params.getPageSize(), total, list);
		return AjaxResponse.success(pageDTO);
	}

	/**
	 * 订单评分导出
	 * @param cityId 城市
	 * @param supplierId 供应商
	 * @param teamId 车队
	 * @param groupIds 小组
	 * @param driverName 司机姓名
	 * @param driverPhone 司机手机号
	 * @param orderNo 订单号
	 * @param createDateBegin 开始时间
	 * @param createDateEnd 结束时间
	 * @param evaluateScore 司机评分
	 * @param sortName 排序字段
	 * @param sortOrder 排序
	 * @param request request
	 * @param response response
	 * @return
	 */
	@RequestMapping("/exportOrderAppraisal")
	@ResponseBody
	public AjaxResponse exportOrderAppraisal(String cityId,
									 String supplierId,
									 String teamId,
									 String groupIds,
									 String driverName,
									 @Verify(param="driverPhone",rule="mobile")
														 String driverPhone,
									 String orderNo,
									 @Verify(param="createDateBegin",rule="required")String createDateBegin,
									 @Verify(param="createDateEnd",rule="required")String createDateEnd,
									 String evaluateScore,String sortName, String sortOrder,HttpServletRequest request,HttpServletResponse response){

		if (StringUtils.isEmpty(driverPhone) && StringUtils.isEmpty(teamId)){
			//请选择一个车队或输入司机手机号
			return AjaxResponse.fail(RestErrorCode.TEAMID_OR_DRIVERID_ISNULL);
		}
		CarBizCustomerAppraisalParams params = new CarBizCustomerAppraisalParams(cityId,supplierId,teamId,groupIds,driverName,driverPhone,orderNo,
				createDateBegin,createDateEnd,evaluateScore,sortName,sortOrder,null,null);

		log.info("订单评分导出--/orderAppraisal/exportOrderAppraisal---参数："+params.toString());
		try {
			List<CarBizCustomerAppraisal> rows = new ArrayList<>();
			String driverList = "";
			if(StringUtils.isNotEmpty(params.getGroupIds()) || StringUtils.isNotEmpty(params.getTeamId())){
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(params.getTeamId(), params.getGroupIds());
				if(driverList==null || "".equals(driverList)){
					log.info("订单评分导出-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
					log.info("订单评分导出-有选择车队查询条件-该车队下没有司机teamId=="+params.getTeamId());
				}
			}
			if(!((StringUtils.isNotEmpty(params.getGroupIds()) || StringUtils.isNotEmpty(params.getTeamId())) && StringUtils.isEmpty(driverList))){
				params.setDriverIds(driverList);
				//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
				params = this.chuliParams(params);
				rows = carBizCustomerAppraisalExMapper.queryForListObject(params);
			}
			@SuppressWarnings("deprecation")
			Workbook wb = this.exportExcel(rows,request.getRealPath("/")+ File.separator+"template"+File.separator+"order_appraisal.xlsx");
			super.exportExcelFromTemplet(request, response, wb, new String("订单评分".getBytes("utf-8"), "iso8859-1"));
			log.error("订单评分导出--导出成功");
			return AjaxResponse.success("文件导出成功！");
		} catch (Exception e) {
			log.error("订单评分导出--导出失败");
			return AjaxResponse.fail(RestErrorCode.FILE_EXPORT_FAIL);
		}
	}

	/**
	 * 处理参数
	 * @param params
	 * @return
	 */
	private CarBizCustomerAppraisalParams chuliParams(CarBizCustomerAppraisalParams params) {
		//整理排序字段
		if(StringUtils.isNotEmpty(params.getSortName())){
			String sortName = super.pingSortName(params.getSortName());
			params.setSortName(sortName);
		}
		if (StringUtils.isEmpty(params.getCityId()) && StringUtils.isEmpty(params.getSupplierId())){
			log.info("订单评分导出--未输入城市和供应商，初始化用户本身的城市和供应商。");
			String cities = WebSessionUtil.getCurrentLoginUser().getCityIds().toString();
			String suppliers = WebSessionUtil.getCurrentLoginUser().getSupplierIds().toString();
			params.setCities(cities.substring(1,cities.length()-1));
			params.setSuppliers(suppliers.substring(1,suppliers.length()-1));
		}

		return params;
	}

	public Workbook exportExcel(List<CarBizCustomerAppraisal> list, String path) throws Exception{
		FileInputStream io = new FileInputStream(path);
		Workbook wb = new XSSFWorkbook(io);

		if(list != null && list.size()>0){
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = null;
			int i=0;
			for(CarBizCustomerAppraisal s:list){
				Row row = sheet.createRow(i + 1);

				cell = row.createCell(0);
				cell.setCellValue(s.getDriverName());

				cell = row.createCell(1);
				cell.setCellValue(s.getDriverPhone());

				cell = row.createCell(2);
				cell.setCellValue(s.getLicensePlates());

				cell = row.createCell(3);
				cell.setCellValue(s.getOrderNo());

				cell = row.createCell(4);
				cell.setCellValue(s.getEvaluateScore());

				cell = row.createCell(5);
				cell.setCellValue(s.getEvaluate());

				cell = row.createCell(6);
				cell.setCellValue(s.getMemo());

				cell = row.createCell(7);
				cell.setCellValue(DateUtils.formatDateTime_CN(s.getCreateDate()));

				i++;
			}
		}
		return wb;
	}


	@ResponseBody
	@RequestMapping("/orderAppraisalListFromDriverOutageData")
	public Object orderAppraisalListFromDriverOutageData(@Verify(param = "outageId", rule = "required") Integer outageId,
														 Integer page,
														 Integer pageSize) {
		log.info("/orderAppraisal/orderAppraisalListFromDriverOutageData:司机评分数据列表数据----来自司机停运");

		if(null == page || page == 0)
			page = 1;
		if(null == pageSize || pageSize == 0)
			pageSize = 30;


		int total = 0;
		DriverOutage params = new DriverOutage();
		params.setOutageId(outageId);
		DriverOutage paramss = driverOutageService.queryForObject(params);

		if(paramss!=null&&paramss.getOrderNos()!=null&&!"".equals(paramss.getOrderNos())){
			CarBizCustomerAppraisalParams customerAppraisalEntity = new CarBizCustomerAppraisalParams();
			String orderNos = paramss.getOrderNos().replaceAll(",", "\",\"");
			orderNos = "\""+orderNos +"\"";
			customerAppraisalEntity.setOrderNos(orderNos);


			Page<CarBizCustomerAppraisal> p = PageHelper.startPage(page, pageSize);
			List<CarBizCustomerAppraisalBean> list = null;
			try {
				List<CarBizCustomerAppraisal> appraisalList = this.carBizCustomerAppraisalExMapper.queryForListObject(customerAppraisalEntity);
				list = BeanUtil.copyList(appraisalList,CarBizCustomerAppraisalBean.class);
				total = (int) p.getTotal();
			} finally {
				PageHelper.clearPage();
			}

			PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
			return AjaxResponse.success(pageDTO);
		}else{
			return AjaxResponse.success(new PageDTO(page, pageSize, 0, null));
		}

	}

}
