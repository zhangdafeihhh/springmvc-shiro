package com.zhuanche.controller.busManage;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.serv.busManage.BusSupplierService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

import mapper.rentcar.ex.BusCarBizSupplierExMapper;

@RestController
@RequestMapping("/bus/supplier")
@Validated
public class BusSupplierController {

	private static final Logger logger = LoggerFactory.getLogger(BusSupplierController.class);

	// ===========================巴士业务拓展mapper==================================

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

	// ===========================专车业务拓展service==================================

	// ===========================巴士业务拓展service==================================
	@Autowired
	private BusSupplierService busSupplierService;

	/**
	 * @Title: saveSupplier
	 * @Description: 保存/修改供应商
	 * @param baseDTO
	 * @param detailDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/saveSupplier")
	public AjaxResponse saveSupplier(BusSupplierBaseDTO baseDTO, BusSupplierDetailDTO detailDTO) {// TODO 封装分佣、返点信息
		return busSupplierService.saveSupplierInfo(baseDTO, detailDTO);
	}
	
	/**
	 * @Title: querySupplierPageList
	 * @Description: 查询供应商分页列表
	 * @param baseDTO
	 * @param detailDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@SuppressWarnings("resource")
	@RequestMapping(value = "/querySupplierPageList")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse querySupplierPageList(BusSupplierQueryDTO queryDTO) {
		Integer pageNum = queryDTO.getPageNum();
		Integer pageSize = queryDTO.getPageSize();
		
		// 查询列表
		List<BusSupplierPageVO> resultList = busSupplierService.queryBusSupplierPageList(queryDTO);
        
        // 计算total
		queryDTO.setPageNum(pageNum);
		queryDTO.setPageSize(pageSize);
        queryDTO.setContractIds(null);
		queryDTO.setExcludeContractIds(null);
        List<BusSupplierPageVO> totalList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
        Page<BusSupplierPageVO> page = (Page<BusSupplierPageVO>) totalList;
		return AjaxResponse.success(new PageDTO(queryDTO.getPageNum(), queryDTO.getPageSize(), page.getTotal(), resultList));
	}
	
	/**
	 * @Title: exportSupplierList
	 * @Description: 导出供应商列表
	 * @param queryDTO
	 * @param request
	 * @param response
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@SuppressWarnings("resource")
	@RequestMapping(value = "/exportSupplierList")
	public void exportSupplierList(BusSupplierQueryDTO queryDTO, HttpServletRequest request,
			HttpServletResponse response) {
        long start = System.currentTimeMillis(); // 获取开始时间
		try {
			// 数据权限控制SSOLoginUser
			Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
			Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID
			Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); // 普通管理员可以管理的所有车队ID
			queryDTO.setAuthOfCity(permOfCity);
			queryDTO.setAuthOfSupplier(permOfSupplier);
			queryDTO.setAuthOfTeam(permOfTeam);

			// 文件名
			LocalDateTime now = LocalDateTime.now();
			String suffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
			String fileName = "供应商信息" + suffix + ".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); // 获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) { // IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else { // 其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}

			CsvUtils utilEntity = new CsvUtils();
			// 表头
			List<String> csvHeaderList = new ArrayList<>();
			String headerStr = "供应商,城市 ,分佣比例,加盟费,保证金,是否有返点,合同开始时间,合同到期时间,状态";
			csvHeaderList.add(headerStr);

			/**导出逻辑*/
			int pageNum = 0;
			int pages = 1;
			boolean isFirst = true;
			boolean isLast = false;
			do {
				// 页码+1
				pageNum++;
				
				// 查询数据
				queryDTO.setPageNum(pageNum);
				queryDTO.setPageSize(CsvUtils.downPerSize);
				List<BusSupplierExportVO> list = busSupplierService.querySupplierExportList(queryDTO);
				Page<BusSupplierExportVO> page = (Page<BusSupplierExportVO>) list;
				// 总页数(以第一次查询结果为准)
				if (pageNum == 1 && page != null) {
					pages = page.getPages();
				}
				// 判断是否为第一页
				if (pageNum > 1) {
					isFirst = false;
				}
				// 判断是否为最后一页
				if (pages <= 1 || pageNum == pages) {
					isLast = true;
				}
				
				// 数据区
				// 如果查询结果为空
				if (list == null || list.isEmpty()) {
					logger.info("[ BusSupplierController-exportSupplierList ] 导出条件params={}没有查询出对应的巴士供应商信息", JSON.toJSONString(queryDTO));
					if (isFirst) {
						List<String> csvDataList = new ArrayList<>();
						csvDataList.add("没有查到符合条件的数据");
						utilEntity.exportCsvV2(response, csvDataList, csvHeaderList, fileName, true, true);
					}
					break;
				}
				// 导出查询数据
				List<String> csvDataList = busSupplierService.completeSupplierExportList(list);// 补充其它字段
				utilEntity.exportCsvV2(response, csvDataList, csvHeaderList, fileName, isFirst, isLast);
			} while (!isLast);// 不到最后一页则继续导出

			// 获取结束时间
			long end = System.currentTimeMillis();
			logger.info("巴士供应商导出成功，参数为：" + JSON.toJSONString(queryDTO) + ",耗时=" + (end - start) + "ms");
		} catch (Exception e) {
			// 获取结束时间
			long end = System.currentTimeMillis();
			logger.error("巴士供应商导出成功，参数为：" + JSON.toJSONString(queryDTO) + ",耗时=" + (end - start) + "ms", e);
		}
	}

	/**
	 * @Title: querySupplierPageList
	 * @Description: 查询供应商详情
	 * @param queryDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/querySupplierById")
	public AjaxResponse querySupplierById(@NotNull(message = "供应商ID不能为空") Integer supplierId) {
		BusSupplierInfoVO supplierVO = busSupplierService.querySupplierById(supplierId);
		return AjaxResponse.success(supplierVO);
	}
}
