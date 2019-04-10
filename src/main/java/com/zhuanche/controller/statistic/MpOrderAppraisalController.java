package com.zhuanche.controller.statistic;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalBean;
import com.zhuanche.entity.driver.MpCarBizCustomerAppraisal;
import com.zhuanche.entity.driver.MpCustomerAppraisalParams;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.serv.CarBizCustomerAppraisalExService;
import com.zhuanche.serv.deiver.MpDriverCustomerAppraisalService;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zhuanche.common.enums.MenuEnum.*;

/**
 * 投诉评分--订单评分
 *
 */
@Controller
@RequestMapping("/mpOrderAppraisal")
public class MpOrderAppraisalController extends DriverQueryController{

	private static Logger log =  LoggerFactory.getLogger(MpOrderAppraisalController.class);

	@Autowired
	private MpDriverCustomerAppraisalService mpDriverCustomerAppraisalService;


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
	 * @param appraisalStatus 评论是否有效 0:有效 1:无效
	 * @return AjaxResponse
	 */
	@ResponseBody
	@RequestMapping("/orderAppraisalListData")
	@RequiresPermissions(value = { "OrderScore_look" } )
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	@RequestFunction(menu = ORDER_RANK_LIST)
	public AjaxResponse appraisalDataList(@Verify(param="cityId",rule="required")String cityId,
										  @Verify(param="supplierId",rule="required")String supplierId,
										  String teamId,
										  String groupIds,
										  String driverName,
										  @Verify(param="driverPhone",rule="mobile") String driverPhone,
										  String orderNo, Integer appraisalStatus,
										  String createDateBegin,
										  String createDateEnd,
										  String orderFinishTimeBegin,
										  String orderFinishTimeEnd,
										  String evaluateScore,String sortName, String sortOrder,
										  Integer page,
										  @Verify(param = "pageSize",rule = "max(50)")Integer pageSize) {
		MpCustomerAppraisalParams params = new MpCustomerAppraisalParams(cityId,supplierId,teamId,groupIds,driverName,driverPhone,orderNo,
				createDateBegin,createDateEnd,evaluateScore,sortName,sortOrder,page,pageSize);
		params.setAppraisalStatus(appraisalStatus);

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
		//修改查询限制条件
		if((StringUtils.isEmpty(createDateBegin) && StringUtils.isEmpty(createDateEnd)) &&
				StringUtils.isEmpty(orderFinishTimeBegin) &&
				StringUtils.isEmpty(orderFinishTimeEnd)){
			log.info("评价时间】范围或【完成日期】范围至少限定一个，支持跨度31天");
			return AjaxResponse.fail(RestErrorCode.PARAMS_NOT,RestErrorCode.renderMsg(RestErrorCode.PARAMS_NOT));
		}

		params.setDriverIds(driverList);
		//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
		params = this.chuliParams(params);
		//开始查询
		List<MpCarBizCustomerAppraisal> list = null;
		try {
			log.info("查询订单评分---参数："+params.toString());

			//追加订单完成时间
			params.setOrderFinishTimeBegin(orderFinishTimeBegin);
			params.setOrderFinishTimeEnd(orderFinishTimeEnd);
			PageInfo<MpCarBizCustomerAppraisal> pageInfo = mpDriverCustomerAppraisalService.findPageByparam(params);
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
	@RequiresPermissions(value = { "OrderScore_export" } )
	@ResponseBody
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	@RequestFunction(menu = ORDER_RANK_EXPORT)
	public AjaxResponse exportOrderAppraisal(
			@Verify(param="cityId",rule="required")String cityId,
			@Verify(param="supplierId",rule="required") String supplierId,
			String teamId,
			String groupIds,
			String driverName,
			@Verify(param="driverPhone",rule="mobile")  String driverPhone,
			String orderNo, Integer appraisalStatus,
			String createDateBegin,
			String createDateEnd,
			String orderFinishTimeBegin,
			String orderFinishTimeEnd,
			String evaluateScore,String sortName, String sortOrder,HttpServletRequest request,HttpServletResponse response){

		//修改查询限制条件
		if((StringUtils.isEmpty(createDateBegin) && StringUtils.isEmpty(createDateEnd)) &&
				StringUtils.isEmpty(orderFinishTimeBegin) &&
				StringUtils.isEmpty(orderFinishTimeEnd)){
			log.info("评价时间】范围或【完成日期】范围至少限定一个，支持跨度31天");
			return AjaxResponse.fail(RestErrorCode.PARAMS_NOT,RestErrorCode.renderMsg(RestErrorCode.PARAMS_NOT));
		}

		int page =1;
		int pageSize = CsvUtils.downPerSize;

		List<String> headerList = new ArrayList<>();
		headerList.add("司机姓名,司机手机,车牌号,订单号,评分,评价,备注,时间, 状态, 订单完成时间");

		String fileName = "";
		List<String> csvDataList = new ArrayList<>();
		MpCustomerAppraisalParams params = null;
		try {
			fileName = "订单评分"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {  //其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}

			CsvUtils entity = new CsvUtils();
			params = new MpCustomerAppraisalParams(cityId,supplierId,teamId,groupIds,driverName,driverPhone,orderNo,
					createDateBegin,createDateEnd,evaluateScore,sortName,sortOrder,page,pageSize);
			params.setAppraisalStatus(appraisalStatus);

			String driverList = "";
			if(StringUtils.isNotEmpty(params.getGroupIds()) || StringUtils.isNotEmpty(params.getTeamId())){
				driverList = super.queryAuthorityDriverIdsByTeamAndGroup(params.getTeamId(), params.getGroupIds());
				if(driverList==null || "".equals(driverList)){
					log.info("订单评价列表-有选择小组查询条件-该小组下没有司机groupId=="+params.getGroupIds());
					log.info("订单评价列表-有选择车队查询条件-该车队下没有司机teamId=="+params.getTeamId());


					csvDataList.add("没有查到符合条件的数据");
					entity.exportCsvV2(response,csvDataList,headerList,fileName,true,true);
				}
			}
			params.setDriverIds(driverList);
			//根据 参数重新整理 入参条件 ,如果页面没有传入参数，则使用该用户绑定的权限
			params = this.chuliParams(params);

			params.setOrderFinishTimeBegin(orderFinishTimeBegin);
			params.setOrderFinishTimeEnd(orderFinishTimeEnd);
			PageInfo<MpCarBizCustomerAppraisal> pageInfo = mpDriverCustomerAppraisalService.findPageByparam(params);

			int totalPage = pageInfo.getPages();
			log.info("导出订单评分，参数为"+JSON.toJSONString(params)+"第1页，共"+totalPage+"页");
			if(totalPage == 0){
				csvDataList.add("没有查到符合条件的数据");

				entity.exportCsvV2(response,csvDataList,headerList,fileName,true,true);
			}else{
				boolean isFirst = true;
				boolean isLast = false;
				List<MpCarBizCustomerAppraisal> rows = pageInfo.getList();
				//数据转换
				dataTrans(rows,csvDataList);
				if(totalPage == 1){
					isLast = true;
				}
				entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
				isFirst = false;

				for(int pageNumber=2; pageNumber <= totalPage; pageNumber++){
					csvDataList = new ArrayList<>();
					params.setPage(pageNumber);
					log.info("导出订单评分，第"+pageNumber+"页，共"+totalPage+"页，参数为"+JSON.toJSONString(params));
					pageInfo = mpDriverCustomerAppraisalService.findPageByparam(params);
					if(pageNumber == totalPage){
						isLast = true;
					}
					rows = pageInfo.getList();
					dataTrans(rows,csvDataList);
					entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
				}
				log.info("导出司机评分成功，参数为"+(params==null?"null": JSON.toJSONString(params)));
			}

		} catch (Exception e){
			log.error("导出司机评分异常，参数为"+(params==null?"null": JSON.toJSONString(params)),e);
		}

		return AjaxResponse.success(null);
	}

	private void dataTrans(List<MpCarBizCustomerAppraisal> result, List<String>  csvDataList ){
		if(null == result){
			return;
		}
		for(MpCarBizCustomerAppraisal s:result){
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

			stringBuffer.append(s.getMemo()==null?"":s.getMemo().replaceAll(",","，").replaceAll(System.getProperty("line.separator"),"，"));//评价
			stringBuffer.append(",");

			stringBuffer.append(DateUtils.formatDateTime_CN(s.getCreateDate()));
			stringBuffer.append(",");

			stringBuffer.append((s.getAppraisalStatus()!=null&&s.getAppraisalStatus()==0)?"有效":"无效");

			csvDataList.add(stringBuffer.toString());
		}

	}

	/**
	 * 处理参数
	 * @param params
	 * @return
	 */
	private MpCustomerAppraisalParams chuliParams(MpCustomerAppraisalParams params) {
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

}
