package com.zhuanche.controller.driver;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.serv.CustomerAppraisalService;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/customerAppraisal")
public class CustomerAppraisalController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerAppraisalController.class);
    private static final String LOGTAG = "[订单评分]: ";

    @Autowired
    private CustomerAppraisalService customerAppraisalService;

    /**
     * 订单评分
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param orderNo 订单号
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param teamGroupId 车队下小组ID
     * @param createDateBegin 开始时间
     * @param createDateEnd 结束时间
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCustomerAppraisalList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse queryCustomerAppraisalList(String name, String phone, String orderNo, Integer cityId, Integer supplierId,
                                       Integer teamId, Integer teamGroupId, String createDateBegin, String createDateEnd,
                                       @RequestParam(value="page", defaultValue="0")Integer page,
                                       @RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        //TODO 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID


        CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO = new CarBizCustomerAppraisalDTO();
        carBizCustomerAppraisalDTO.setName(name);
        carBizCustomerAppraisalDTO.setPhone(phone);
        carBizCustomerAppraisalDTO.setOrderNo(orderNo);
        carBizCustomerAppraisalDTO.setCityId(cityId);
        carBizCustomerAppraisalDTO.setSupplierId(supplierId);
        carBizCustomerAppraisalDTO.setTeamId(teamId);
        carBizCustomerAppraisalDTO.setTeamGroupId(teamGroupId);
        carBizCustomerAppraisalDTO.setCreateDateBegin(createDateBegin);
        carBizCustomerAppraisalDTO.setCreateDateEnd(createDateEnd);

        int total = 0;
        List<CarBizCustomerAppraisalDTO> list =  Lists.newArrayList();
        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = customerAppraisalService.queryCustomerAppraisalList(carBizCustomerAppraisalDTO);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 司机评分
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param month 月份 yyyy-M
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCustomerAppraisalStatisticsList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse queryCustomerAppraisalStatisticsList(String name, String phone,
                                                             @Verify(param = "month", rule = "required") String month,
                                                   @RequestParam(value="page", defaultValue="0")Integer page,
                                                   @RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        //TODO 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID


        CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO = new CarBizCustomerAppraisalStatisticsDTO();
        carBizCustomerAppraisalStatisticsDTO.setDriverName(name);
        carBizCustomerAppraisalStatisticsDTO.setDriverPhone(phone);
        carBizCustomerAppraisalStatisticsDTO.setCreateDate(month);

        int total = 0;
        List<CarBizCustomerAppraisalStatisticsDTO> list =  Lists.newArrayList();
        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = customerAppraisalService.queryCustomerAppraisalStatisticsList(carBizCustomerAppraisalStatisticsDTO);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 司机评分一个月详情
     * @param driverId
     * @param month
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCustomerAppraisalStatisticsDetail")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse queryCustomerAppraisalStatisticsDetail(@Verify(param = "driverId", rule = "required") Integer driverId,
                                                             @Verify(param = "month", rule = "required") String month,
                                                             String orderNo) {

        CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO = new CarBizCustomerAppraisalDTO();
        carBizCustomerAppraisalDTO.setDriverId(driverId);
        carBizCustomerAppraisalDTO.setOrderNo(orderNo);
        carBizCustomerAppraisalDTO.setCreateDateBegin(month);

        List<CarBizCustomerAppraisalDTO> list = customerAppraisalService.queryDriverAppraisalDetail(carBizCustomerAppraisalDTO);
        return AjaxResponse.success(list);
    }
}