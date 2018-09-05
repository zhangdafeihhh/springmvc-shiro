package com.zhuanche.controller.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driverDuty.CarDriverDurationDTO;
import com.zhuanche.dto.driverDuty.CarDriverMustDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMustDuty;
import com.zhuanche.entity.mdbcarmanage.CarDutyDuration;
import com.zhuanche.request.CommonRequest;
import com.zhuanche.request.DriverMonthDutyRequest;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.request.TeamGroupRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.driverScheduling.*;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Check;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 司机排班
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author lunan
 * @version 1.0
 * @since 1.0
 * @create: 2018-08-29 16:54
 *
 */
@Controller
@RequestMapping("/saas/driverDayhDuty")
public class DriverSchController {

    private static final Logger logger = LoggerFactory.getLogger(DriverSchController.class);

    @Autowired
    private CitySupplierTeamCommonService commonService;

    @Autowired
    private CarDriverDutyService carDriverDutyService;

    @Autowired
    private CarDriverDurationService carDriverDurationService;

    @Autowired
    private CarDriverMustDutyService carDriverMustDutyService;

    @Autowired
    private CarDriverShiftsService carDriverShiftsService;

    /**
     * @Desc: 保存司机日排班信息
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/saveDriverDayDuty")
    public AjaxResponse saveDriverDayDuty(DutyParamRequest param){
        logger.info("保存司机日排班信息入参:"+ JSON.toJSONString(param));
        try{
            String result = carDriverShiftsService.saveDriverDayDuty(param);
            if("已接收请求，正在处理，稍后可在发布司机排版菜单查看".equals(result)){
                AjaxResponse success = AjaxResponse.success(result);
                Map<String,String> map = new HashedMap();
                map.put("successMsg",result);
                success.setData(map);
                return success;
            }else {
                AjaxResponse fail = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
                Map<String,String> map = new HashedMap();
                map.put("errorMsg",result);
                fail.setData(map);
                return fail;
            }
        }catch (Exception e){
            logger.error("保存司机日排班信息异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 获取班制设置司机列表
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/queryDriverTeamReList")
    public AjaxResponse queryDriverTeamReList(TeamGroupRequest teamGroupRequest){
        logger.info("获取班制设置司机列表入参:"+ JSON.toJSONString(teamGroupRequest));
        try{
            List<CarDriverInfoDTO> list = carDriverShiftsService.queryDriverTeamReList(teamGroupRequest);
            return AjaxResponse.success(list);
        }catch (Exception e){
            logger.error("获取班制设置司机列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 获取排班时长时间段
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/queryDurationListByField")
    public AjaxResponse queryDurationListByField(@Verify(param = "teamId", rule = "required") Integer teamId,
                                                 @Verify(param = "cityId", rule = "required") Integer cityId,
                                                 @Verify(param = "supplierId", rule = "required") Integer supplierId){
        logger.info("获取排班时长时间段入参:"+ JSON.toJSONString(teamId));
        try{
            List<CarDriverDurationDTO> list = carDriverShiftsService.queryDurationListByField(cityId, supplierId, teamId);
            return AjaxResponse.success(list);
        }catch (Exception e){
            logger.error("获取排班时长时间段异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
    * @Desc: 查询司机强制排班时间段
     * @param:
    * @return:
    * @Author: lunan
    * @Date: 2018/9/4
    */
    @ResponseBody
    @RequestMapping(value = "/queryMustListByField")
    public AjaxResponse queryMustListByField(@Verify(param = "teamId", rule = "required") Integer teamId){
        logger.info("查询司机强制排班时间段入参:"+ JSON.toJSONString(teamId));
        try{
            List<CarDriverMustDutyDTO> list = carDriverShiftsService.queryMustListByField(teamId);
            return AjaxResponse.success(list);
        }catch (Exception e){
            logger.error("查询司机强制排班时间段异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 获取强制排班详情
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/getCarDriverMustDetail")
    public AjaxResponse getCarDriverMustDetail(@Verify(param = "paramId", rule = "required") String paramId){
        logger.info("获取强制排班详情入参:"+ JSON.toJSONString(paramId));
        try{
            CarDriverMustDuty detail = carDriverMustDutyService.getCarDriverMustDetail(Integer.parseInt(paramId));
            return AjaxResponse.success(detail);
        }catch (Exception e){
            logger.error("获取强制排班详情异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 新增或者修改强制排班信息
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdateDriverMust")
    public AjaxResponse saveOrUpdateDriverMust(CarDriverMustDuty param){
        logger.info("新增或者修改强制排班信息入参:"+ JSON.toJSONString(param));
        try{
            int result = carDriverMustDutyService.saveOrUpdateDriverMust(param);
            if(result >0){
                return AjaxResponse.success(result);
            }else{
                return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
            }
        }catch (Exception e){
            logger.error("新增或者修改强制排班信息异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 查询强制上班配置列表
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/getDriverMustDutyList")
    public AjaxResponse getDriverMustDutyList(DutyParamRequest param){
        logger.info("查询强制上班配置列表 入参:"+ JSON.toJSONString(param));
        try{
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
                return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
            }
            PageDTO pageDTO = carDriverMustDutyService.getDriverMustDutyList(param);
            return AjaxResponse.success(pageDTO);
        }catch (Exception e){
            logger.error("查询强制上班配置列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 获取排班时长详情
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/getCarDriverDurationDetail")
    public AjaxResponse getCarDriverDurationDetail(@Verify(param = "paramId", rule = "required") String paramId){
        logger.info("获取排班时长详情入参:"+ JSON.toJSONString(paramId));
        try{
            CarDriverDurationDTO detail = carDriverDurationService.getCarDriverDurationDetail(Integer.parseInt(paramId));
            return AjaxResponse.success(detail);
        }catch (Exception e){
            logger.error("获取排班时长详情异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * @Desc: 保存/修改排班时长
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdateCarDriverDuration")
    public AjaxResponse saveOrUpdateCarDriverDuration(CarDutyDuration param){
        logger.info("保存/修改排班时长入参:"+ JSON.toJSONString(param));
        try{
            int result = carDriverDurationService.saveOrUpdateCarDriverDuration(param);
            if(result >0){
                return AjaxResponse.success(result);
            }else{
                return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
            }
        }catch (Exception e){
            logger.error("保存/修改排班时长异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
    * @Desc: 查询排班时长列表
    * @param:
    * @return:
    * @Author: lunan
    * @Date: 2018/9/4
    */
    @ResponseBody
    @RequestMapping(value = "/getDriverDurationList")
    public AjaxResponse getDriverDurationList(DutyParamRequest param){
        logger.info("查询排班时长列表入参:"+ JSON.toJSONString(param));
        try{
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
                return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
            }
            PageDTO pageDTO = carDriverDurationService.getDriverDurationList(param);
            return AjaxResponse.success(pageDTO);
        }catch (Exception e){
            logger.error("查询排班时长异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /** 
    * @Desc: 发布排班 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/issueDriverDuty")
    public AjaxResponse issueDriverDuty(DutyParamRequest param){
        logger.info("发布排班入参:"+ JSON.toJSONString(param));
        try{
            int result = carDriverDutyService.issueDriverDuty(param);
            ServiceReturnCodeEnum typeByCode = ServiceReturnCodeEnum.getTypeByCode(result);
            if(result < 0 ){
                AjaxResponse fail = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
                Map<String,String> map = new HashedMap();
                map.put("errorMsg",typeByCode.getName());
                fail.setData(map);
                return fail;
            }else if(result == 1){
                return AjaxResponse.success(result);
            }else if(result == 2){
                AjaxResponse success = AjaxResponse.success(result);
                Map<String,String> map = new HashedMap();
                map.put("successMsg",typeByCode.getName());
                success.setData(map);
                return success;
            }
            return AjaxResponse.success(result);
        }catch (Exception e){
            logger.error("发布排班异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /** 
    * @Desc: 查看符合条件排班列表 (包含发布司机排班数据列表 status 1)
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/queryDriverMonthDutyData")
    public AjaxResponse queryDriverMonthDutyData(DutyParamRequest param) {
        logger.info("查询月排班列表数据入参:"+ JSON.toJSONString(param));
        try{
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
                return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
            }
            PageDTO pageDTO = carDriverDutyService.queryDriverDayDutyList(param);
            return AjaxResponse.success(pageDTO);
        }catch (Exception e){
            logger.error("查询排班列表异常:{}",e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }





}