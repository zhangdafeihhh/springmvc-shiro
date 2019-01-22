package com.zhuanche.controller.busManage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.objcompare.CompareObjectUtils;

import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;

/**
 * @ClassName: BusCommonController
 * @Description: 巴士公用接口
 * @author: yanyunpeng
 * @date: 2018年12月6日 上午10:09:31
 */
@RestController
@RequestMapping("/bus/common")
@Validated
public class BusCommonController {

    @Autowired
    private BusCommonService busCommonService;

    /**
     * @param businessType
     * @param businessKey
     * @return AjaxResponse
     * @throws
     * @Title: changeLogs
     * @Description: 查询操作日志
     */
    @RequestMapping(value = "/changeLogs")
    public AjaxResponse changeLogs(@NotNull(message = "业务类型不能为空") Integer businessType,
                                   @NotBlank(message = "业务主键不能为空") String businessKey) {
        // 一、校验业务类型是否存在
        if (!BusinessType.isExist(businessType)) {
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "业务类型不存在");
        }

        // 二、查询日志
        Date now = Date.from(LocalDate.now().plusWeeks(-1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Map<Object, Object>> logs = busCommonService.queryChangeLogs(businessType, businessKey, now);
        logs.forEach(log -> {
			String newValue = Optional.ofNullable((String) log.get("description")).map(value -> {
				return value.replaceAll(CompareObjectUtils.separator, ",");
			}).orElse("");
			log.put("description", newValue);
        });
        return AjaxResponse.success(logs);
    }

    /**
     * @param cityId
     * @return AjaxResponse
     * @throws
     * @Title: suppliers
     * @Description: 查询供应商
     */
    @RequestMapping(value = "/suppliers")
    public AjaxResponse suppliers(@NotNull(message = "城市ID不能为空") Integer cityId) {
        List<Map<Object, Object>> suppliers = busCommonService.querySuppliers(cityId);
        return AjaxResponse.success(suppliers);
    }

    /**
     * @return AjaxResponse
     * @throws
     * @Title: groups
     * @Description: 巴士车型类别
     */
    @RequestMapping(value = "/groups")
    public AjaxResponse groups() {
        List<Map<Object, Object>> groups = busCommonService.queryGroups();
        return AjaxResponse.success(groups);
    }

    /**
     * @return AjaxResponse
     * @throws
     * @Title: services
     * @Description: 查询巴士服务类型
     */
    @RequestMapping(value = "/services")
    public AjaxResponse services() {
        List<Map<Object, Object>> services = busCommonService.queryServices();
        return AjaxResponse.success(services);
    }

    /**
     * @Description: 下载车辆和司机导入的错误信息
     * @Param: []
     * @return: void
     * @Date: 2019/1/4
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/downLoadErrorMsg")
    public AjaxResponse downLoadErrorMsg(@RequestParam(value = "errorMsgKey", required = true) String errorMsgKey, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List errorReasons = RedisCacheUtil.get(errorMsgKey, List.class);
        if (errorReasons == null) {
            return AjaxResponse.failMsg(RestErrorCode.FILE_IMPORT_ERROR, "错误信息不存在，或者已经被删除");
        }
        List<String> headerList = new ArrayList<>();
        headerList.add("错误信息");
        String filename = BusConstant.buidFileName(request, "错误信息");
        CsvUtils utilEntity = new CsvUtils();
        boolean isStart = true;
        boolean isList = false;
        List<String> csvDataList = new ArrayList<>();
        for (int i = 0; i < errorReasons.size(); i++) {
            if (i != 0) {
                isStart = false;
            }
            if (i == errorReasons.size() - 1) {
                isList = true;
            }
            String reason = String.valueOf(errorReasons.get(i));
            csvDataList.clear();
            csvDataList.add(reason);
            utilEntity.exportCsvV2(response, csvDataList, headerList, filename, isStart, isList);
        }
        //导出后删除key
        RedisCacheUtil.delete(errorMsgKey);
        return AjaxResponse.success(null);
    }
}
