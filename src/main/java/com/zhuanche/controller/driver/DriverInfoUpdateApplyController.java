package com.zhuanche.controller.driver;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.DriverInfoUpdateApplyDTO;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoUpdateApply;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.serv.CarBizCarInfoService;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.mdbcarmanage.DriverInfoUpdateService;
import com.zhuanche.serv.rentcar.CarBizModelService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/driverInfoUpdateApply")
public class DriverInfoUpdateApplyController {

    private static final Logger logger = LoggerFactory.getLogger(DriverInfoUpdateApplyController.class);
    private static final String LOGTAG = "[司机,车辆信息修改申请]: ";

    @Autowired
    private DriverInfoUpdateService driverInfoUpdateService;

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;

    @Autowired
    private CarBizCarInfoService carBizCarInfoService;

    @Autowired
    private CarBizModelService carBizModelService;

    /**
     * 司机\车辆修改申请信息列表（有分页）
     * @param name 司机姓名
     * @param phone 司机手机号
     * @param idCardNo 身份证号
     * @param status 状态
     * @param cityId 城市ID
     * @param supplierId 供应商ID
     * @param teamId 车队ID
     * @param createDateBegin 开始时间
     * @param createDateEnd 结束时间
     * @param type 1.司机申请 2.车辆申请
     * @param page 起始页，默认0
     * @param pageSize 取N条，默认20
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findDriverInfoUpdateList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse findDriverInfoUpdateList(String name, String phone, String idCardNo, String licensePlates, Integer status, Integer cityId,
                                                 Integer supplierId,Integer teamId, Integer teamGroupId, String createDateBegin, String createDateEnd,
                                                 @Verify(param = "type", rule = "required") Integer type,
                                                 @RequestParam(value="page", defaultValue="0")Integer page,
                                                 @Verify(param = "pageSize",rule = "max(50)")@RequestParam(value="pageSize", defaultValue="20")Integer pageSize) {

        // 数据权限控制SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //普通管理员可以管理的所有城市ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //普通管理员可以管理的所有供应商ID
        Set<Integer> permOfTeam        = WebSessionUtil.getCurrentLoginUser().getTeamIds(); //普通管理员可以管理的所有车队ID

        DriverInfoUpdateApplyDTO driverInfoUpdateApplyDTO = new DriverInfoUpdateApplyDTO();
        driverInfoUpdateApplyDTO.setDriverName(name);
        driverInfoUpdateApplyDTO.setDriverPhone(phone);
        driverInfoUpdateApplyDTO.setIdCardNo(idCardNo);
        driverInfoUpdateApplyDTO.setLicensePlates(licensePlates);
        driverInfoUpdateApplyDTO.setStatus(status);
        driverInfoUpdateApplyDTO.setCityId(cityId);
        driverInfoUpdateApplyDTO.setSupplierId(supplierId);
        driverInfoUpdateApplyDTO.setTeamId(teamId);
        driverInfoUpdateApplyDTO.setTeamGroupId(teamGroupId);
        driverInfoUpdateApplyDTO.setCreateDateBegin(createDateBegin);
        driverInfoUpdateApplyDTO.setCreateDateEnd(createDateEnd);
        driverInfoUpdateApplyDTO.setType(type);
        //数据权限
        driverInfoUpdateApplyDTO.setCityIds(permOfCity);
        driverInfoUpdateApplyDTO.setSupplierIds(permOfSupplier);
        driverInfoUpdateApplyDTO.setTeamIds(permOfTeam);

        int total = 0;
        List<DriverInfoUpdateApplyDTO> list =  Lists.newArrayList();
        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = driverInfoUpdateService.queryDriverInfoUpdateList(driverInfoUpdateApplyDTO);
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
    @RequestMapping(value = "/findDriverInfoUpdateById")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse findDriverInfoUpdateById(@Verify(param = "id", rule = "required") Integer id) {

        DriverInfoUpdateApply driverInfoUpdateApply = driverInfoUpdateService.selectByPrimaryKey(id);
        return AjaxResponse.success(driverInfoUpdateApply);
    }

    /**
     * 保存司机修改申请
     * @param driverId 司机ID
     * @param driverPhone 司机手机号
     * @param driverPhoneNew 新司机手机号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveDriverInfoUpdateApply",method = RequestMethod.POST)
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse saveDriverInfoUpdateApply(@Verify(param = "driverId", rule = "required") Integer driverId,
                                                  @Verify(param = "driverPhone", rule = "required") String driverPhone,
                                                  @Verify(param = "driverPhoneNew", rule = "required") String driverPhoneNew) {

        if (StringUtils.isEmpty(driverPhoneNew) || !ValidateUtils.validatePhone(driverPhoneNew)) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_NOT_LEGAL);
        }
        if(driverPhoneNew.equals(driverPhone)){
            return AjaxResponse.fail(RestErrorCode.PHONE_NEW_SAME);
        }
        //查询手机号是否存在
        Boolean had = carBizDriverInfoService.checkPhone(driverPhoneNew, null);
        if (had) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_PHONE_EXIST);
        }
        CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(driverId);
        if(carBizDriverInfo==null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
        }
        CarBizDriverInfoDTO carBizDriverInfoDTO = BeanUtil.copyObject(carBizDriverInfo, CarBizDriverInfoDTO.class);
        carBizDriverInfoDTO = carBizDriverInfoService.getBaseStatis(carBizDriverInfoDTO);

        DriverInfoUpdateApply driverInfoUpdateApply = new DriverInfoUpdateApply();
        driverInfoUpdateApply.setDriverId(driverId);
        driverInfoUpdateApply.setDriverName(carBizDriverInfoDTO.getName());
        driverInfoUpdateApply.setDriverPhone(driverPhone);
        driverInfoUpdateApply.setDriverPhoneNew(driverPhoneNew);
        driverInfoUpdateApply.setIdCardNo(carBizDriverInfoDTO.getIdCardNo());
        driverInfoUpdateApply.setLicensePlates(carBizDriverInfoDTO.getLicensePlates());
        driverInfoUpdateApply.setCityId(carBizDriverInfoDTO.getServiceCity());
        driverInfoUpdateApply.setCityName(carBizDriverInfoDTO.getCityName());
        driverInfoUpdateApply.setSupplierId(carBizDriverInfoDTO.getSupplierId());
        driverInfoUpdateApply.setSupplierName(carBizDriverInfoDTO.getSupplierName());
        driverInfoUpdateApply.setTeamId(carBizDriverInfoDTO.getTeamId());
        driverInfoUpdateApply.setTeamName(carBizDriverInfoDTO.getTeamName());
        driverInfoUpdateApply.setCarModelId(carBizDriverInfoDTO.getModelId());
        driverInfoUpdateApply.setCarModelName(carBizDriverInfoDTO.getModelName());
        driverInfoUpdateApply.setCreateId(WebSessionUtil.getCurrentLoginUser().getId());
        driverInfoUpdateApply.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
        driverInfoUpdateApply.setCreateTime(new Date());
        driverInfoUpdateApply.setType(1);
        driverInfoUpdateApply.setStatus(1);

        int i = driverInfoUpdateService.insertSelective(driverInfoUpdateApply);
        if(i>0){
            return AjaxResponse.success(null);
        }else{
            return AjaxResponse.success(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 保存车辆修改申请
     * @param licensePlates 车牌号
     * @param carModelIdNew 新的车型ID
     * @param modelDetailNew 新的具体车型
     * @param carPurchaseDateNewStr 购买时间(String yyyy-MM-dd)
     * @param colorNew 新的颜色
     * @param idCardNoNew 新的绑定司机身份证号
     * @param driverNameNew 新的绑定司机名称
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveCarInfoUpdateApply")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public AjaxResponse saveCarInfoUpdateApply(@Verify(param = "licensePlates", rule = "required") String licensePlates,
                                               @Verify(param = "carModelIdNew", rule = "required") Integer carModelIdNew,
                                               @Verify(param = "modelDetailNew", rule = "required") String modelDetailNew,
                                               @Verify(param = "carPurchaseDateNewStr", rule = "required") String carPurchaseDateNewStr,
                                               @Verify(param = "colorNew", rule = "required") String colorNew,
                                               String idCardNoNew, String driverNameNew) {

        if((StringUtils.isNotEmpty(idCardNoNew) && StringUtils.isEmpty(driverNameNew))
                || (StringUtils.isEmpty(idCardNoNew) && StringUtils.isNotEmpty(driverNameNew))){
            return AjaxResponse.fail(RestErrorCode.INFORMATION_NOT_COMPLETE);
        }
        //查询车牌号是否存在
        CarBizCarInfoDTO carBizCarInfoDTO = carBizCarInfoService.selectModelByLicensePlates(licensePlates);
        if(carBizCarInfoDTO==null){
            return AjaxResponse.fail(RestErrorCode.BUS_NOT_EXIST);
        }
        DriverInfoUpdateApply driverInfoUpdateApply = new DriverInfoUpdateApply();
        driverInfoUpdateApply.setLicensePlates(licensePlates);
        driverInfoUpdateApply.setCarPurchaseDate(carBizCarInfoDTO.getCarPurchaseDate());
        driverInfoUpdateApply.setModelDetail(carBizCarInfoDTO.getModelDetail());
        driverInfoUpdateApply.setColor(carBizCarInfoDTO.getColor());
        driverInfoUpdateApply.setCityId(carBizCarInfoDTO.getCityId());
        driverInfoUpdateApply.setCityName(carBizCarInfoDTO.getCityName());
        driverInfoUpdateApply.setSupplierId(carBizCarInfoDTO.getSupplierId());
        driverInfoUpdateApply.setSupplierName(carBizCarInfoDTO.getSupplierName());

        //老司机信息
        if(carBizCarInfoDTO.getDriverId()!=null && carBizCarInfoDTO.getDriverId()!=0){
            CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(carBizCarInfoDTO.getDriverId());
            if(carBizDriverInfo==null){
                return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
            }
            //存在，加入
            driverInfoUpdateApply.setDriverId(carBizDriverInfo.getDriverId());
            driverInfoUpdateApply.setDriverName(carBizDriverInfo.getName());
            driverInfoUpdateApply.setDriverPhone(carBizDriverInfo.getPhone());
            driverInfoUpdateApply.setIdCardNo(carBizDriverInfo.getIdCardNo());
        }
        if(carBizCarInfoDTO.getCarModelId()!=null){
            CarBizModel carBizModel = carBizModelService.selectByPrimaryKey(carBizCarInfoDTO.getCarModelId());
            if(carBizModel==null){
                return AjaxResponse.fail(RestErrorCode.MODEL_NOT_EXIST);
            }
            driverInfoUpdateApply.setCarModelId(carBizCarInfoDTO.getCarModelId());
            driverInfoUpdateApply.setCarModelName(carBizModel.getModelName());
        }

        CarBizModel carBizModel = carBizModelService.selectByPrimaryKey(carModelIdNew);
        if(carBizModel==null){
            return AjaxResponse.fail(RestErrorCode.MODEL_NOT_EXIST);
        }
        //车型相关信息
        driverInfoUpdateApply.setCarModelIdNew(carModelIdNew);
        driverInfoUpdateApply.setCarModelNameNew(carBizModel.getModelName());
        driverInfoUpdateApply.setModelDetailNew(modelDetailNew);
        driverInfoUpdateApply.setColorNew(colorNew);

        //新司机信息
        if(StringUtils.isNotEmpty(idCardNoNew) && StringUtils.isNotEmpty(driverNameNew)){
                driverInfoUpdateApply.setIdCardNoNew(idCardNoNew);
                driverInfoUpdateApply.setDriverNameNew(driverNameNew);
        }else {//不存在，删除
            driverInfoUpdateApply.setIdCardNoNew("");
            driverInfoUpdateApply.setDriverNameNew("");
            driverInfoUpdateApply.setDriverPhoneNew("");
        }
        Date carPurchaseDateNew = null;
        try {
            carPurchaseDateNew = new SimpleDateFormat("yyyy-MM-dd").parse(carPurchaseDateNewStr);
        } catch (ParseException e) {
            return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST, "购买时间error=" + e.getMessage());
        }
        driverInfoUpdateApply.setCarPurchaseDateNew(carPurchaseDateNew);

        driverInfoUpdateApply.setCreateId(WebSessionUtil.getCurrentLoginUser().getId());
        driverInfoUpdateApply.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
        driverInfoUpdateApply.setCreateTime(new Date());
        driverInfoUpdateApply.setStatus(1);
        driverInfoUpdateApply.setType(2);

        int i = driverInfoUpdateService.insertSelective(driverInfoUpdateApply);
        if(i>0){
            return AjaxResponse.success(RestErrorCode.SUCCESS);
        }else{
            return AjaxResponse.success(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
}