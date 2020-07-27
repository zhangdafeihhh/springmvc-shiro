package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.InterCityEchelonDto;
import com.zhuanche.dto.mdbcarmanage.InterCityTeamDto;
import com.zhuanche.dto.mdbcarmanage.InterDriverTeamRelDto;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import com.zhuanche.entity.mdbcarmanage.InterCityTeam;
import com.zhuanche.serv.intercity.DriverInfoInterCityService;
import com.zhuanche.serv.intercity.IntegerCityTeamDriverRelService;
import com.zhuanche.serv.intercity.IntegerCityTeamService;
import com.zhuanche.serv.mdbcarmanage.service.InterCityEchelonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.List;

/**
 * @Author fanht
 * @Description 城际拼车梯队
 * @Date 2020/7/13 下午3:58
 * @Version 1.0
 */
@Controller
@RequestMapping("/interCityEchelonController")
public class InterCityEchelonController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IntegerCityTeamService teamService;

    @Autowired
    private IntegerCityTeamDriverRelService relService;


    @Autowired
    private DriverInfoInterCityService cityService;

    @Autowired
    private InterCityEchelonService interCityEchelonService;


    @RequestMapping("/addTeam")
    @ResponseBody
    public AjaxResponse addTeam(@Verify(param = "cityId", rule = "required") Integer cityId,
                                @Verify(param = "supplierId", rule = "required") Integer supplierId,
                                @Verify(param = "teamName", rule = "required|min(1)|max(99)") Integer teamName) {

        logger.info(MessageFormat.format("新增车队入参：cityId:{0},supplierId:{1},teamName:{2}", cityId, supplierId, teamName));
        try {
            return teamService.saveOrupdateTeam(null,cityId, supplierId, teamName.toString());
        } catch (Exception e) {
            logger.error("新增车队异常",e);
        }
        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }

    @RequestMapping("/updateTeam")
    @ResponseBody
    public AjaxResponse addTeam(@Verify(param = "id", rule = "required") Integer id,
                                @Verify(param = "cityId", rule = "required") Integer cityId,
                                @Verify(param = "supplierId", rule = "required") Integer supplierId,
                                @Verify(param = "teamName", rule = "required|min(1)|max(99)") Integer teamName) {

        logger.info(MessageFormat.format("修改车队入参：cityId:{0},supplierId:{1},teamName:{2}", cityId, supplierId, teamName));
        try {
            String teamNameStr = teamName.toString();
            return teamService.saveOrupdateTeam(id, cityId, supplierId, teamNameStr);
        } catch (Exception e) {
            logger.error("修改车队异常",e);
        }
        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }


    @RequestMapping("/teamDetail")
    @ResponseBody
    public AjaxResponse addTeam(@Verify(param = "id", rule = "required") Integer id) {

        logger.info(MessageFormat.format("车队详情入参：id:{0}", id));
        try {
            return AjaxResponse.success(teamService.teamDetail(id));
        } catch (Exception e) {
            logger.error("获取详情异常" , e);
        }
        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }


    @RequestMapping("/addDriver")
    @ResponseBody
    public AjaxResponse addDriver(@Verify(param = "driverIds", rule = "required") String driverIds,
                                  @Verify(param = "teamId", rule = "required") Integer teamId) {
        logger.info(MessageFormat.format("新城际拼车车队添加司机入参:driverIds:{0},teamId:{1}", driverIds, teamId));
        try {
            return relService.addDriver(driverIds, teamId);
        } catch (Exception e) {
            logger.error("添加司机异常",e);
        }
        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }


    /***
     * 查询列表
     * @param driverInfoInterCity
     * @param teamId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("/queryDriverTeamList")
    @ResponseBody
    public AjaxResponse queryDriverTeamList(DriverInfoInterCity driverInfoInterCity,
                                            Integer teamId,
                                            @Verify(param = "pageNo", rule = "required|min(0)") Integer pageNo,
                                            @Verify(param = "pageSize", rule = "required|min(10)") Integer pageSize) {

        try {
            PageDTO pageDTO = cityService.queryDriverRelTeam(pageSize, pageNo, driverInfoInterCity, teamId);
            return AjaxResponse.success(pageDTO);
        } catch (Exception e) {
           logger.info("查询列表异常",e);
        }
        return null;
    }

    /**
     * 删除
     *
     * @param driverId
     * @param teamId
     * @return
     */
    @RequestMapping("/deleteDriver")
    @ResponseBody
    public AjaxResponse deleteDriver(Integer driverId, Integer teamId) {
        logger.info("删除入参driverId:{0},teamId:{1}", driverId, teamId);
        try {
            relService.del(driverId, teamId);
        } catch (Exception e) {
            logger.error("删除异常",e);
        }
        return AjaxResponse.success(null);
    }


    /**
     * 添加编辑梯队
     */
    @RequestMapping("/addOrEditEchelon")
    @ResponseBody
    public AjaxResponse addOrEditEchelon(Integer id,
                                         Integer cityId,
                                         Integer supplierId,
                                         Integer teamId,
                                         String echelonDate,
                                         Integer sort,
                                         String echelonMonth) {
        try {
            return interCityEchelonService.addOrEdit(id, cityId, supplierId, teamId, echelonDate, sort, echelonMonth);
        } catch (Exception e) {
           logger.error("添加编辑车队异常",e);
        }
        return AjaxResponse.success(null);
    }


    @RequestMapping("/editEchelonDetail")
    @ResponseBody
    public AjaxResponse editEchelonDetail(@Verify(param = "teamId", rule = "required") Integer teamId) {

        try {
            List<InterCityEchelon> echelonList= interCityEchelonService.detailList(teamId);
            return AjaxResponse.success(echelonList);
        } catch (Exception e) {
            logger.error("获取编辑梯队详情异常",e);
        }
        return AjaxResponse.success(null);
    }


    /***
     * 查询小队下的司机
     * @param teamId
     * @return
     */
    @RequestMapping("/queryDriverByTeamId")
    @ResponseBody
    public AjaxResponse queryDriverByTeamId(@Verify(param = "teamId",rule = "required | min(1)") Integer teamId){
        logger.info("查询车队下的司机入参：teamId:" + teamId);
        return cityService.queryDriverByTeam(teamId);
    }




    /**
     * 查询司机
     * @param queryParam
     * @return
     */
    @RequestMapping("/queryDriver")
    @ResponseBody
    public AjaxResponse queryDriver(@Verify(param = "queryParam",rule = "required") String queryParam){
        logger.info("查询司机信息入参" + queryParam );
        return cityService.queryDriverByParam(queryParam);
    }


    /**
     * 查询梯队列表
     * @param driverInfoInterCity
     * @param echelonMonth
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryEchelonList")
    public AjaxResponse queryEchelonList(DriverInfoInterCity driverInfoInterCity,
                                         String  echelonMonth,
                                         @Verify(param = "pageNo", rule = "required|min(0)") Integer pageNo,
                                         @Verify(param = "pageSize", rule = "required|min(10)") Integer pageSize){
        logger.info(MessageFormat.format("查询梯队列表入参,driverInfoInterCity:{0},echelonMonth:{1},pageNo:{1},pageSize:{2}", JSONObject.toJSON(driverInfoInterCity),
                echelonMonth,pageNo,pageSize));
        return interCityEchelonService.queryEchelonList(driverInfoInterCity,echelonMonth,pageNo,pageSize);

    }

    @ResponseBody
    @RequestMapping("/queryTeam")
    public AjaxResponse queryTeam(@Verify(param = "cityId", rule = "required|min(0)") Integer cityId,
                                  @Verify(param = "supplierId", rule = "required") Integer supplierId){
        logger.info(MessageFormat.format("获取车堵入参,cityId:{0},supplierId:{1}",cityId,supplierId));
        try {
            List<InterCityTeamDto> queryTeamDto = interCityEchelonService.queryTeam(cityId,supplierId);
            return AjaxResponse.success(queryTeamDto);
        } catch (Exception e) {
            logger.error("获取车队异常",e);
        }
        return AjaxResponse.success(null);
    }
}
