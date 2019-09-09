package com.zhuanche.controller.driver;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.DriverInfoLicenseUpdateApplyDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoLicenseUpdateApply;
import com.zhuanche.serv.mdbcarmanage.DriverInfoLicenseUpdateService;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

import static com.zhuanche.common.enums.MenuEnum.*;

@Controller
@RequestMapping("/driverInfoLicenseUpdateApply")
public class DriverInfoLicenseUpdateApplyController {

    private static final Logger logger = LoggerFactory.getLogger(DriverInfoLicenseUpdateApplyController.class);
    private static final String LOGTAG = "[司机换车换牌修改申请]: ";

    @Autowired
    private DriverInfoLicenseUpdateService driverInfoLicenseUpdateService;

    /**
     * 司机\车辆修改申请信息列表（有分页）
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param status 状态
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param createDateBegin 开始时间
     * @param createDateEnd 结束时间
     * @param changeStatus 车辆更换状态 0：未更换 1：已更换
     * @param initiatorType 发起方类型  1：加盟商  2：司机
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findDriverInfoLicenseUpdateList")
	@RequiresPermissions(value = { "FranchiserDriverChange_look" , "SupplierCarModifyApply_look" } ,logical=Logical.OR )
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = DRIVER_INFO_CHANGE_APPLY_LIST)
    public AjaxResponse findDriverInfoLicenseUpdateList(String name, String phone, String licensePlates,
                                                        Integer status, Integer cityId, Integer supplierId, Integer teamId,
                                                        Integer teamGroupId, String createDateBegin, String createDateEnd,
                                                        Integer changeStatus, Integer initiatorType,
                                                        @RequestParam(value="page", defaultValue="0")Integer page,
                                                        @Verify(param = "pageSize",rule = "max(50)")@RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        DriverInfoLicenseUpdateApplyDTO licenseUpdateApplyDTO = new DriverInfoLicenseUpdateApplyDTO();
        licenseUpdateApplyDTO.setDriverName(name);
        licenseUpdateApplyDTO.setDriverPhone(phone);
        licenseUpdateApplyDTO.setLicensePlates(licensePlates);
        licenseUpdateApplyDTO.setStatus(status);
        licenseUpdateApplyDTO.setCityId(cityId);
        licenseUpdateApplyDTO.setSupplierId(supplierId);
        licenseUpdateApplyDTO.setTeamId(teamId);
        licenseUpdateApplyDTO.setTeamGroupId(teamGroupId);
        licenseUpdateApplyDTO.setCreateDateBegin(createDateBegin);
        licenseUpdateApplyDTO.setCreateDateEnd(createDateEnd);
        licenseUpdateApplyDTO.setChangeStatus(changeStatus);
        licenseUpdateApplyDTO.setInitiatorType(initiatorType);
        //数据权限
        licenseUpdateApplyDTO.setCityIds(permOfCity);
        licenseUpdateApplyDTO.setSupplierIds(permOfSupplier);
        licenseUpdateApplyDTO.setTeamIds(permOfTeam);

        int total = 0;
        List<DriverInfoLicenseUpdateApplyDTO> list =  Lists.newArrayList();
        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = driverInfoLicenseUpdateService.queryDriverInfoLicenseUpdateList(licenseUpdateApplyDTO);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * 根据ID查询信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findDriverInfoLicenseUpdateById")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    @RequestFunction(menu = DRIVER_INFO_CHANGE_APPLY_DETAIL)
    public AjaxResponse findDriverInfoLicenseUpdateById(@Verify(param = "id", rule = "required") Integer id) {

        DriverInfoLicenseUpdateApply DriverInfoLicenseUpdateApply = driverInfoLicenseUpdateService.selectByPrimaryKey(id);
        return AjaxResponse.success(DriverInfoLicenseUpdateApply);
    }
}