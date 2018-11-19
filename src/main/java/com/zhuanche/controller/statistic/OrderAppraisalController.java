package com.zhuanche.controller.statistic;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalBean;
import com.zhuanche.dto.rentcar.DriverDutyStatisticDTO;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.serv.CarBizCustomerAppraisalExService;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.excel.CsvUtils;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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
	private DriverOutageService driverOutageService;

	@Autowired
	private CarBizCustomerAppraisalExService carBizCustomerAppraisalExService;

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
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
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
										  @Verify(param = "pageSize",rule = "max(50)")Integer pageSize) {

		if (StringUtils.isEmpty(driverPhone) && StringUtils.isEmpty(teamId)){
			//请选择一个车队号或输入司机手机
			return AjaxResponse.fail(RestErrorCode.TEAMID_OR_DRIVERID_ISNULL);
		}
		CarBizCustomerAppraisalParams params = new CarBizCustomerAppraisalParams(cityId,supplierId,teamId,groupIds,driverName,driverPhone,orderNo,
				createDateBegin,createDateEnd,evaluateScore,sortName,sortOrder,page,pageSize);


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
		List<CarBizCustomerAppraisal> list = null;
		try {
			log.info("查询订单评分---参数："+params.toString());
			PageInfo<CarBizCustomerAppraisal> pageInfo = carBizCustomerAppraisalExService.findPageByparam(params);
			list = pageInfo.getList();
			total = (int)pageInfo.getTotal();
		} catch (Exception e){
			 log.error("查询订单评分异常，参数为"+(params==null?"null": JSON.toJSONString(params)),e);
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
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
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
		int page =1;
		int pageSize = CsvUtils.downPerSize;



		List<String> headerList = new ArrayList<>();
		headerList.add("司机姓名,司机手机,车牌号,订单号,评分,评价,备注,时间");

		String fileName = "";
		List<String> csvDataList = new ArrayList<>();
		CarBizCustomerAppraisalParams params = null;
		try {
			fileName = "订单评分"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {  //其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}

			if (StringUtils.isEmpty(driverPhone) && StringUtils.isEmpty(teamId)){
				//请选择一个车队号或输入司机手机
				return AjaxResponse.fail(RestErrorCode.TEAMID_OR_DRIVERID_ISNULL);
			}
			CsvUtils entity = new CsvUtils();
			params = new CarBizCustomerAppraisalParams(cityId,supplierId,teamId,groupIds,driverName,driverPhone,orderNo,
					createDateBegin,createDateEnd,evaluateScore,sortName,sortOrder,page,pageSize);


			String driverList = "";
			if(StringUtils.isNotEmpty(params.getGroupIds()) || StringUtils.isNotEmpty(params.getTeamId())){
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(params.getTeamId(), params.getGroupIds());
				if(driverList==null || "".equals(driverList)){
					log.info("订单评价列表-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
					log.info("订单评价列表-有选择车队查询条件-该车队下没有司机teamId=="+params.getTeamId());


					csvDataList.add("没有查到符合条件的数据");
					CsvUtils.exportCsvV2(response,csvDataList,headerList,fileName,true,true,entity);
				}
			}
			params.setDriverIds(driverList);
			//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
			params = this.chuliParams(params);


			PageInfo<CarBizCustomerAppraisal> pageInfo = carBizCustomerAppraisalExService.findPageByparam(params);

			int totalPage = pageInfo.getPages();
			log.info("导出订单评分，参数为"+JSON.toJSONString(params)+"第1页，共"+totalPage+"页");
			if(totalPage == 0){
				csvDataList.add("没有查到符合条件的数据");

				CsvUtils.exportCsvV2(response,csvDataList,headerList,fileName,true,true,entity);
			}else{
				boolean isFirst = true;
				boolean isLast = false;
				List<CarBizCustomerAppraisal> rows = pageInfo.getList();
				//数据转换
				dataTrans(rows,csvDataList);
				if(totalPage == 1){
					isLast = true;
				}
				CsvUtils.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast,entity);
				isFirst = false;

				for(int pageNumber=2; pageNumber <= totalPage; pageNumber++){
					csvDataList = new ArrayList<>();
					params.setPage(pageNumber);
					log.info("导出订单评分，第"+pageNumber+"页，共"+totalPage+"页，参数为"+JSON.toJSONString(params));
					pageInfo = carBizCustomerAppraisalExService.findPageByparam(params);
					if(pageNumber == totalPage){
						isLast = true;
					}
					rows = pageInfo.getList();
					dataTrans(rows,csvDataList);
					CsvUtils.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast,entity);
				}
				log.info("导出司机评分成功，参数为"+(params==null?"null": JSON.toJSONString(params)));
			}

		} catch (Exception e){
			log.error("导出司机评分异常，参数为"+(params==null?"null": JSON.toJSONString(params)),e);
		}

		return AjaxResponse.success(null);
	}
	private void dataTrans(List<CarBizCustomerAppraisal> result, List<String>  csvDataList ){
		if(null == result){
			return;
		}
		for(CarBizCustomerAppraisal s:result){
			StringBuffer stringBuffer = new StringBuffer();


			stringBuffer.append(s.getDriverName());
			stringBuffer.append(",");

			stringBuffer.append(s.getDriverPhone()==null?"":"\t"+s.getDriverPhone());
			stringBuffer.append(",");

			stringBuffer.append(s.getLicensePlates()==null?"":s.getLicensePlates());
			stringBuffer.append(",");

			stringBuffer.append(s.getOrderNo());
			stringBuffer.append(",");

			stringBuffer.append(s.getEvaluateScore()==null?"":s.getEvaluateScore());
			stringBuffer.append(",");

			stringBuffer.append(StringUtils.isEmpty(s.getEvaluate())?"":s.getEvaluate().replaceAll(",","，"));
			stringBuffer.append(",");

			stringBuffer.append(s.getMemo()==null?"":s.getMemo().replaceAll(",","，"));//评价
			stringBuffer.append(",");

			stringBuffer.append(DateUtils.formatDateTime_CN(s.getCreateDate()));

			csvDataList.add(stringBuffer.toString());
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

	@ResponseBody
	@RequestMapping("/orderAppraisalListFromDriverOutageData")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
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


			List<CarBizCustomerAppraisalBean> list = null;
			try {
				PageInfo<CarBizCustomerAppraisal> pageInfo = carBizCustomerAppraisalExService.findPageByparam(customerAppraisalEntity);
				List<CarBizCustomerAppraisal> appraisalList = pageInfo.getList();
				list = BeanUtil.copyList(appraisalList,CarBizCustomerAppraisalBean.class);
				total = (int) pageInfo.getTotal();
			}catch (Exception e){
				log.error("异常，参数为："+JSON.toJSONString(customerAppraisalEntity),e);
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
