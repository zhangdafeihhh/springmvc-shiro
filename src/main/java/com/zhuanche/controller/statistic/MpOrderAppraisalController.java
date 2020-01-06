package com.zhuanche.controller.statistic;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.util.RedisKeyUtils;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.DriverQueryController;
import com.zhuanche.entity.driver.DriverAppraisalAppeal;
import com.zhuanche.entity.driver.MpCarBizCustomerAppraisal;
import com.zhuanche.entity.driver.MpCustomerAppraisalParams;
import com.zhuanche.serv.deiver.DriverAppraisalAppealService;
import com.zhuanche.serv.deiver.MpDriverCustomerAppraisalService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.MobileOverlayUtil;
import com.zhuanche.util.dateUtil.DateUtil;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

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
	@Autowired
	private DriverAppraisalAppealService appraisalAppealService;


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
										  Integer isAllowedAppeal,
										  Integer appealStatus,
										  String evaluateScore,String sortName, String sortOrder,
										  Integer page,
										  Integer callbackStatus,
										  @Verify(param = "pageSize",rule = "max(50)")Integer pageSize) {
		long startTime=System.currentTimeMillis();
		String queryKey = RedisKeyUtils.MP_ORDER_APPRAISAL;
		try {
			//只能查询一个月的数据
			if ((StringUtils.isEmpty(createDateBegin) && StringUtils.isEmpty(createDateEnd)) && StringUtils.isEmpty(orderFinishTimeBegin) && StringUtils.isEmpty(orderFinishTimeEnd)) {
                log.info("【评价时间】范围或【完成日期】范围至少限定一个，支持跨度31天");
                return AjaxResponse.fail(RestErrorCode.PARAMS_NOT, RestErrorCode.renderMsg(RestErrorCode.PARAMS_NOT));
            }
			//封装主表查询条件
			MpCustomerAppraisalParams params = new MpCustomerAppraisalParams(cityId, supplierId, driverName, driverPhone, orderNo,
					createDateBegin, createDateEnd,orderFinishTimeBegin,orderFinishTimeEnd,
					evaluateScore, appraisalStatus,isAllowedAppeal, sortName, sortOrder);
			log.info("订单评价列表,查询参数："+ JSON.toJSONString(params));

			//车队id如果 为空，将用户的车队id赋值
			if(StringUtils.isEmpty(teamId)){
				Set<Integer> teamIds2 = WebSessionUtil.getCurrentLoginUser().getTeamIds();
				teamId = (teamIds2 != null && teamIds2.size()>0) ? StringUtils.join(teamIds2.toArray(), ",") : "";
			}
			//如果选择了车队小组，先查询该车队小组下对应的DriverId
			if (StringUtils.isNotEmpty(groupIds) || StringUtils.isNotEmpty(teamId)) {
                String driverIds = super.queryAuthorityDriverIdsByTeamAndGroup(teamId, groupIds);
                if (StringUtils.isBlank(driverIds)) {
                    log.info("订单评价列表-有选择小组查询条件-该小组下没有司机groupId={},teamId={}", groupIds, teamId);
                    return AjaxResponse.success(new PageDTO(page, pageSize, 0, new ArrayList()));
                }
                params.setDriverIds(driverIds);
            }
			if (appealStatus != null && appealStatus == 0) {
                params.setIsAlreadyAppeal(0);
            }
			//整理权限
			params = this.chuliParams(params);

			//判断是否有附表条件 appealStatus =0 可以转换为主表中的IsAlreadyAppeal = 0 查询主表信息
			List<MpCarBizCustomerAppraisal> resultList = null;

			Integer currentId = WebSessionUtil.getCurrentLoginUser().getId();

			 queryKey = queryKey + currentId + JSON.toJSONString(params);

			//PageDTO redisPageDTO =  RedisCacheUtil.get(queryKey,PageDTO.class);

			//此处是缓存查询条件，结果是实时的不能缓存查询结果

			if(RedisCacheUtil.exist(queryKey)){
				log.info("点击过于频繁");
				return AjaxResponse.success("点击过于频繁");
			}

			//缓存查询条件10s  如果有返回结果 则删除key
			RedisCacheUtil.set(queryKey,queryKey,10);

			long total = 0;
			List<Integer> queryParam=null;
			if ((appealStatus == null || appealStatus == 0) && (callbackStatus == null) ) {
                //没有附表查询条件直接查询主表信息返回
                params.setPage(page);
                params.setPageSize(pageSize);
                PageInfo<MpCarBizCustomerAppraisal> pageByparam = mpDriverCustomerAppraisalService.findPageByparam(params);
                resultList = pageByparam.getList();
                queryParam=resultList.stream().map(MpCarBizCustomerAppraisal::getAppraisalId).collect(Collectors.toList());
                total = pageByparam.getTotal();
                if(total==0){
                	//删除查询条件
					RedisCacheUtil.delete(queryKey);
					return AjaxResponse.success(new PageDTO(page,pageSize,0,new ArrayList()));
				}
            } else {
                List<Integer> mainTabIds = mpDriverCustomerAppraisalService.queryIds(params);
                Set<Integer> slaveTabIds = appraisalAppealService.getAppraissalIdsByAppealStatus(appealStatus , callbackStatus);
                //求并集
				mainTabIds.removeIf(o -> !slaveTabIds.contains(o));
				if(mainTabIds.size()==0){
					RedisCacheUtil.delete(queryKey);
					return AjaxResponse.success(new PageDTO(page,pageSize,0,new ArrayList()));
				}
				int start = (page - 1) * pageSize;
                int end = start + pageSize;
                queryParam = mainTabIds.subList((start <= 0) ? 0 : start, end >= mainTabIds.size() ? mainTabIds.size() : end);
                total = mainTabIds.size();
                resultList = mpDriverCustomerAppraisalService.queryByIds(queryParam);
            }
			//封装字段
			List<DriverAppraisalAppeal> appeals = appraisalAppealService.queryBaseInfoByAppraisalIds(queryParam);
			Map<Integer, DriverAppraisalAppeal> appealsMap = appeals.stream().collect(Collectors.toMap(o -> o.getAppraisalId(), O -> O));
			resultList.forEach(o -> {
                DriverAppraisalAppeal appeal = appealsMap.get(o.getAppraisalId());
                if (appeal != null) {
                    o.setAppealId(appeal.getId());
					if (Objects.nonNull(appeal.getCallbackStatus())) {
						o.setCallbackStatus(appeal.getCallbackStatus());
					}else {
						o.setCallbackStatus(0);
					}
					//撤销状态不显示 申述时间
                    if(appeal.getAppealStatus()!=4){
						o.setAppealTime(appeal.getCreateTime());
					}
                    o.setAppealStatus(appeal.getAppealStatus());
                } else {
                    //申诉表中没有表明未申诉
                    o.setAppealStatus(0);
                }
                o.setDriverPhone(MobileOverlayUtil.doOverlayPhone(o.getDriverPhone()));
            });
			log.info("订单评价列表 查询成功 耗时："+(System.currentTimeMillis()-startTime));
			RedisCacheUtil.delete(queryKey);
			return AjaxResponse.success(new PageDTO(page,pageSize,total, resultList));
		} catch (Exception e) {
			log.error("订单评价列表异常 耗时："+(System.currentTimeMillis()-startTime));
			RedisCacheUtil.delete(queryKey);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}


	@RequestMapping("/exportOrderAppraisal")
	@RequiresPermissions(value = { "OrderScore_export" } )
	@ResponseBody
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
	})
	@RequestFunction(menu = ORDER_RANK_EXPORT)
	public void exportOrderAppraisal(
			@Verify(param="cityId",rule="required")String cityId,
			@Verify(param="supplierId",rule="required") String supplierId,
			String teamId,
			String groupIds,
			String driverName,
			@Verify(param="driverPhone",rule="mobile")  String driverPhone,
			String orderNo, Integer appraisalStatus,
			Integer isAllowedAppeal,
			Integer appealStatus,
			String createDateBegin,
			String createDateEnd,
			String orderFinishTimeBegin,
			String orderFinishTimeEnd,
			Integer callbackStatus,
			String evaluateScore,String sortName, String sortOrder,HttpServletRequest request,HttpServletResponse response){
		     CsvUtils entity = new CsvUtils();
		    String fileName = this.getFileName(request, "订单评分");
		    long startTime=System.currentTimeMillis();
		try {
			//只能查询一个月的数据
			if ((StringUtils.isEmpty(createDateBegin) && StringUtils.isEmpty(createDateEnd)) && StringUtils.isEmpty(orderFinishTimeBegin) && StringUtils.isEmpty(orderFinishTimeEnd)) {
				ArrayList<String> errHead=new ArrayList<>();
				errHead.add("【评价时间】范围或【完成日期】范围至少限定一个，支持跨度31天");
				entity.exportCsvV2(response,null,errHead,fileName,true,true);
				return;
			}

			//封装主表查询条件
			MpCustomerAppraisalParams params = new MpCustomerAppraisalParams(cityId, supplierId, driverName, driverPhone, orderNo,
					createDateBegin, createDateEnd,orderFinishTimeBegin,orderFinishTimeEnd,
					evaluateScore, appraisalStatus,isAllowedAppeal, sortName, sortOrder);
			log.info("订单评价列表,导出参数："+ JSON.toJSONString(params));

			//车队id如果 为空，将用户的车队id赋值
			if(StringUtils.isEmpty(teamId)){
				Set<Integer> teamIds2 = WebSessionUtil.getCurrentLoginUser().getTeamIds();
				teamId = (teamIds2 != null && teamIds2.size()>0) ? StringUtils.join(teamIds2.toArray(), ",") : "";
			}
			//如果选择了车队小组，先查询该车队小组下对应的DriverId
			if (StringUtils.isNotEmpty(groupIds) || StringUtils.isNotEmpty(teamId)) {
				String driverIds = super.queryAuthorityDriverIdsByTeamAndGroup(teamId, groupIds);
				if (StringUtils.isBlank(driverIds)) {
					ArrayList<String> errHead=new ArrayList<>();
					errHead.add("暂无数据");
					entity.exportCsvV2(response,null,errHead,fileName,true,true);
					return;
				}
				params.setDriverIds(driverIds);
			}
			//查询未申诉数据，转换为主表中的字段
			if (appealStatus != null && appealStatus == 0) {
				params.setIsAlreadyAppeal(0);
			}
			//整理权限
			params = this.chuliParams(params);
			List<Integer> mainTabIds = mpDriverCustomerAppraisalService.queryIds(params);

			//查询附表
			if ((appealStatus != null && appealStatus != 0) || (callbackStatus != null)) {
				Set<Integer> slaveTabIds = appraisalAppealService.getAppraissalIdsByAppealStatus(appealStatus , callbackStatus);
				//求并集
				mainTabIds.removeIf(o -> !slaveTabIds.contains(o));
			}

			int total = mainTabIds.size();
			if(total==0){
				ArrayList<String> errHead=new ArrayList<>();
				errHead.add("暂无数据");
				entity.exportCsvV2(response,null,errHead,fileName,true,true);
				return;
			}
			int pageSize=30;
			int pages=total%pageSize==0?total/pageSize:total/pageSize+1;

			ArrayList<String> headList=new ArrayList();
			if ("44".equals(cityId)) {
				headList.add("司机姓名,司机手机,车牌号,订单号,评分,标签,评价内容,评分状态,申诉状态,回访状态");
			}else {
				headList.add("司机姓名,司机手机,车牌号,订单号,评分,评价,备注,评价时间,订单完成时间,评分状态,是否允许申诉,申诉状态,申诉时间");
			}

			boolean isFirst=true;
			boolean isLast=false;
			for(int i=1;i<=pages;i++){
				int start = (i-1)*pageSize;
				int end = start+pageSize>mainTabIds.size()?mainTabIds.size():start+pageSize;
				List<Integer> queryParam = mainTabIds.subList(start, end);
				List<MpCarBizCustomerAppraisal> appraisals = mpDriverCustomerAppraisalService.queryByIds(queryParam);
				List<DriverAppraisalAppeal> appeals = appraisalAppealService.queryBaseInfoByAppraisalIds(queryParam);
				Map<Integer, DriverAppraisalAppeal> appealsMap = appeals.stream().collect(Collectors.toMap(o -> o.getAppraisalId(), O -> O));
				List<String> data = dataTrans(appraisals, appealsMap , Integer.valueOf(cityId));
				entity.exportCsvV2(response,data,headList,fileName,isFirst,isLast);
				isFirst=false;
				if(i==pages-1){
					isLast=true;
				}
			}
			log.info("订单评分导出：total="+total+"耗时："+(System.currentTimeMillis()-startTime));
		} catch (Exception e) {
			log.error("订单评分导出异常" , e);
			ArrayList<String> errHead=new ArrayList<>();
			errHead.add("请联系管理员");
			try {
				entity.exportCsvV2(response,null,errHead,fileName,true,true);
			} catch (IOException e1) {
				log.error("订单评分导出异常" , e);
			}
		}


	}

	private String getFileName(HttpServletRequest request,String fileName){
		try {
			fileName = fileName+ DateUtil.dateFormat(new Date(), DateUtil.intTimestampPattern)+".csv";
			//获得浏览器信息并转换为大写
			String agent = request.getHeader("User-Agent").toUpperCase();
			//IE浏览器和Edge浏览器
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
				//其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
		} catch (UnsupportedEncodingException e) {
			log.error("格式化导出文件名异常"+e);
		}
		return fileName;
	}


	private List<String> dataTrans(List<MpCarBizCustomerAppraisal> result,Map<Integer, DriverAppraisalAppeal> appraisalMap , Integer cityId){
		List<String> list =new ArrayList<>();
		for(MpCarBizCustomerAppraisal s:result) {
			StringBuffer sb = new StringBuffer();
			sb.append(s.getDriverName());
			sb.append(",");

			sb.append(s.getDriverPhone() == null ? "" : "\t" + MobileOverlayUtil.doOverlayPhone(s.getDriverPhone()));
			sb.append(",");

			sb.append(s.getLicensePlates() == null ? "" : s.getLicensePlates());
			sb.append(",");

			sb.append(s.getOrderNo());
			sb.append(",");

			sb.append(s.getEvaluateScore() == null ? "" : s.getEvaluateScore());
			sb.append(",");

			sb.append(StringUtils.isEmpty(s.getEvaluate()) ? "" : s.getEvaluate().replaceAll(",", "，"));
			sb.append(",");
			//评价
			sb.append(s.getMemo()==null?"":s.getMemo().replaceAll(",","，").replaceAll(System.getProperty("line.separator"),"，"));
			sb.append(",");

			//北京的差评申诉单独处理
			if (cityId == 44) {
				sb.append((s.getAppraisalStatus()!=null&&s.getAppraisalStatus()==0)?"有效":"无效");
				sb.append(",");
				if (s.getIsAlreadyAppeal() == 0) {
					sb.append("未申诉").append(",");
				}else {
					sb.append(AppealStatusEnum.getMsg(s.getAppealStatus() == null ? 0 : s.getAppealStatus())).append(",");
				}
				sb.append(s.getCallbackStatus()==null || s.getCallbackStatus() == 0 ? "未回访" : "已回访");
				sb.append(",");
			}else {
				sb.append(DateUtils.formatDateTime_CN(s.getCreateDate()));
				sb.append(",");

				sb.append(DateUtils.formatDateTime_CN(s.getOrderFinishTime()));
				sb.append(",");

				sb.append((s.getAppraisalStatus()!=null&&s.getAppraisalStatus()==0)?"有效":"无效");
				sb.append(",");

				sb.append(s.getIsAllowedAppeal() == 0 ? "不可申诉" : "可申诉");
				sb.append(",");
				DriverAppraisalAppeal appeal = appraisalMap.get(s.getAppraisalId());
				String appealTime = "";
				int appealStatus = 0;
				if (appeal != null) {
					appealTime = DateUtils.formatDateTime_CN(appeal.getCreateTime());
					appealStatus = appeal.getAppealStatus();
				}
				sb.append(AppealStatusEnum.getMsg(appealStatus)).append(",");
				if(appealStatus!=4){
					sb.append(appealTime);
				}
			}
			list.add(sb.toString());
		}
			return list;
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

	enum AppealStatusEnum{
		UN_APPEAL(0,"未申诉"),APPEALED(1,"已申诉"),APPEAL_SUCCESS(2,"申诉成功 "),APPEAL_FAIL(3,"申诉失败"),REVOKE_APPEAL(4,"撤销");

		private int code;
		private String msg;

		AppealStatusEnum(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public static String getMsg(int code){
			for (AppealStatusEnum status:AppealStatusEnum.values()) {
				if(status.code==code){
					return status.msg;
				}
			}
			return "";
		}
	}

}
