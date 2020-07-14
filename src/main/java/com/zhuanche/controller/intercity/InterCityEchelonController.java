package com.zhuanche.controller.intercity;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.intercity.IntegerCityTeamDriverRelService;
import com.zhuanche.serv.intercity.IntegerCityTeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author fanht
 * @Description 城际拼车梯队
 * @Date 2020/7/13 下午3:58
 * @Version 1.0
 */
@Controller
@RequestMapping("/interCityEchelonController")
public class InterCityEchelonController extends IntegerCityController{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IntegerCityTeamService teamService;

    @Autowired
    private IntegerCityTeamDriverRelService relService;

    @RequestMapping("/addTeam")
    @ResponseBody
    public AjaxResponse addTeam(@Verify(param = "cityId",rule = "required") Integer cityId,
                                @Verify(param = "supplierId",rule = "required")Integer supplierId,
                                @Verify(param = "teamName",rule = "required")String teamName){

        logger.info("新增车队入参：cityId:{0},supplierId:{1},teamName:{2}",cityId,supplierId,teamName);
        int code = 0;
        try {
            code = teamService.addTeam(cityId,supplierId,teamName.trim());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if(code > 0){
            return AjaxResponse.success(null);
        }else {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

    @RequestMapping("/updateTeam")
    @ResponseBody
    public AjaxResponse addTeam(@Verify(param = "id",rule = "required") Integer id,
                                @Verify(param = "cityId",rule = "required") Integer cityId,
                                @Verify(param = "supplierId",rule = "required")Integer supplierId,
                                @Verify(param = "teamName",rule = "required")String teamName){

        logger.info("修改车队入参：cityId:{0},supplierId:{1},teamName:{2}",cityId,supplierId,teamName);
        int code = 0;
        try {
            code = teamService.updateTeam(id,cityId,supplierId,teamName.trim());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if(code > 0){
            return AjaxResponse.success(null);
        }else {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }


    @RequestMapping("/addDriver")
    @ResponseBody
    public AjaxResponse addDriver(@Verify(param = "driverIds",rule = "required") String driverIds,
                                  @Verify(param = "teamId",rule = "required") Integer teamId){
        logger.info("新城际拼车车队添加司机入参:driverIds:{0},teamId:{1}",driverIds,teamId);
        relService.addDriver(driverIds,teamId);

        return AjaxResponse.success(null);
    }


    public AjaxResponse queryList(){

        return AjaxResponse.success(null);
    }

    @RequestMapping("/deleteDriver")
    @ResponseBody
    public AjaxResponse deleteDriver(Integer driverId){

        return AjaxResponse.success(null);
    }
}
