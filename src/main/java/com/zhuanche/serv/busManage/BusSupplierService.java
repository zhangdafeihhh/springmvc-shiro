package com.zhuanche.serv.busManage;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.sq.common.okhttp.builder.OkHttpRequest;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverInfoTempService;
import com.zhuanche.shiro.session.WebSessionUtil;

import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.ex.BusBizSupplierDetailExMapper;
import mapper.rentcar.CarBizCityMapper;
import mapper.rentcar.CarBizCooperationTypeMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;
import okhttp3.Request;

@Service
public class BusSupplierService implements BusConst {

	private static final Logger logger = LoggerFactory.getLogger(BusSupplierService.class);

	// ===========================表基础mapper==================================
	@Autowired
	private CarBizCityMapper carBizCityMapper;

	@Autowired
	private CarBizSupplierMapper carBizSupplierMapper;

	@Autowired
	private CarBizCooperationTypeMapper carBizCooperationTypeMapper;

	@Autowired
	private CarAdmUserMapper carAdmUserMapper;

	// ===========================专车业务拓展mapper==================================

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

	@Autowired
	private BusBizSupplierDetailExMapper busBizSupplierDetailExMapper;

	// ===========================专车业务拓展service==================================
	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private CarBizDriverInfoTempService carBizDriverInfoTempService;

	// ===========================巴士业务拓展service==================================

	// ===============================专车其它服务===================================

	// ===============================巴士其它服务===================================

	/**
	 * @Title: saveSupplierInfo
	 * @Description: 
	 * @param baseDTO
	 * @param detailDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/saveSupplier")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER) })
	public AjaxResponse saveSupplierInfo(BusSupplierBaseDTO baseDTO, BusSupplierDetailDTO detailDTO) {
		String method = "UPDATE";
		int f = 0;
		Integer supplierId = baseDTO.getSupplierId();
		// 一、操作主表
		baseDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
		if (supplierId == null || supplierId == 0) {
			method = "CREATE";
			baseDTO.setAddress(detailDTO.getInvoiceCompanyAddr());// 默认发票公司地址
			baseDTO.setSupplierType(1);// 巴士供应商
			baseDTO.setEnterpriseType(2);// 非客运企业
			baseDTO.setIsTwoShifts(1);// 双班
			baseDTO.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());// 操作人ID
			f = busCarBizSupplierExMapper.insertSelective(baseDTO);
			supplierId = baseDTO.getSupplierId();// 返回ID
		} else {
			f = busCarBizSupplierExMapper.updateByPrimaryKeySelective(baseDTO);
		}
		// 二、操作巴士供应商专用表
		detailDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
		if (busBizSupplierDetailExMapper.checkExist(supplierId) > 0) {// 校验是否存在
			busBizSupplierDetailExMapper.updateBySupplierIdSelective(detailDTO);
		} else {
			// 将主表ID赋值给巴士供应商表
			detailDTO.setSupplierId(supplierId);
			detailDTO.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());// 操作人ID
			busBizSupplierDetailExMapper.insertSelective(detailDTO);
		}

		// 三、查询供应商底下是否有司机，并修改司机加盟类型
		if ("UPDATE".equals(method)) {
			carBizDriverInfoService.updateDriverCooperationTypeBySupplierId(baseDTO.getSupplierId(), baseDTO.getCooperationType());
			carBizDriverInfoTempService.updateDriverCooperationTypeBySupplierId(baseDTO.getSupplierId(), baseDTO.getCooperationType());
		}
		
		// 四、调用分佣接口，修改分佣、返点信息  TODO

		// 五、保存操作记录
		// TODO
		
		// 六、MQ消息写入 供应商
		try {
			Map<String, Object> msgMap = new HashMap<String, Object>();
			msgMap.put("method", method);
			msgMap.put("data", JSON.toJSONString(baseDTO));
			logger.info("专车供应商，同步发送数据：", JSON.toJSONString(msgMap));
			CommonRocketProducer.publishMessage("vipSupplierTopic", method, String.valueOf(baseDTO.getSupplierId()), msgMap);
		} catch (Exception e) {
			logger.error("[ BusSupplierService-saveSupplierInfo ] 供应商信息发送MQ出错", e.getMessage(), e);
		}
		logger.info("***********************新增/修改 厂商信息  END***********************");
		if (f > 0) {
			return AjaxResponse.success(null);
		} else {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "操作失败");
		}
	}
}
