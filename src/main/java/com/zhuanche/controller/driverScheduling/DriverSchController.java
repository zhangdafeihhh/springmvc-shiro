package com.zhuanche.controller.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.dto.driverDuty.CarDriverDurationDTO;
import com.zhuanche.dto.driverDuty.CarDriverMustDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMustDuty;
import com.zhuanche.entity.mdbcarmanage.CarDutyDuration;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.request.TeamGroupRequest;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.driverScheduling.CarDriverDurationService;
import com.zhuanche.serv.driverScheduling.CarDriverDutyService;
import com.zhuanche.serv.driverScheduling.CarDriverMustDutyService;
import com.zhuanche.serv.driverScheduling.CarDriverShiftsService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import com.zhuanche.util.MobileOverlayUtil;
import com.zhuanche.util.dateUtil.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zhuanche.common.enums.MenuEnum.*;

/**
 * @description: ????????????
 *
 * <PRE>
 * <BR>	????????????
 * <BR>-----------------------------------------------
 * <BR>	????????????			?????????			????????????
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
     * ?????????????????? response??????
     */
    private HttpServletResponse setResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        return response;
    }


    /** 
    * @Desc: ?????????????????? 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/5 
    */ 
    @RequestMapping("/exportDutyToExcel")
	@RequiresPermissions(value = { "LookArrange_export" } )
    @ResponseBody
    @RequestFunction(menu = DRIVER_DUTY_EXPORT)
    public String  exportDutyToExcel(HttpServletResponse response, HttpServletRequest request,DutyParamRequest param){

        try{
            logger.info("????????????????????????????????????:"+ JSON.toJSONString(param));
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
                logger.info("?????????????????????????????????????????????????????????????????????????????????:"+ JSON.toJSONString(param));
                return  "";
            }
            if(Check.NuNObj(param) || Check.NuNObj(param.getUnpublishedFlag()) ){
                logger.info("??????????????????????????????????????????????????????:"+ JSON.toJSONString(param));
                return  "";
            }
            String startTime = param.getStartTime();
            String endTime = param.getEndTime();
            if(StringUtils.isEmpty(startTime)){
                return  "??????????????????????????????";
            }
            if(StringUtils.isEmpty(endTime)){
                return  "??????????????????????????????";
            }
            String fileName = "??????????????????"+DateUtil.dateFormat(new Date(),DateUtil.intTimestampPattern)+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //???????????????????????????????????????
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE????????????Edge?????????
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //???????????????
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            List<String> headerList = new ArrayList<>();
            headerList.add("????????????,?????????,??????,?????????,??????,????????????,??????????????????,????????????,??????");


            long start = System.currentTimeMillis();
            param.setPage(1);
            //??????????????????????????? 3000
            param.setPageSize(10000);
            PageInfo<CarDriverDayDutyDTO> pageInfos = carDriverDutyService.queryDriverDayDutyList(param);
            List<CarDriverDayDutyDTO> result = pageInfos.getList();
            logger.info("????????????????????????????????????:"+ JSON.toJSONString(param)+"???pageNumber="+0+";???????????????"+pageInfos.getTotal()
                    +"??????????????????"+pageInfos.getPages());
            List<String> csvDataList = new ArrayList<>();
            if(result == null || result.size() == 0){
                csvDataList.add("?????????????????????????????????");
                CsvUtils entity = new CsvUtils();
                entity.exportCsvV2(response,csvDataList,headerList,fileName,true,true);
                return "";
            }
            int pages = pageInfos.getPages();//?????????????????????
            boolean isFirst = true;
            boolean isLast = false;
            if(pages == 1){
                isLast = true;
            }
            dataTrans( result,  csvDataList);
            CsvUtils entity = new CsvUtils();
            entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
            csvDataList = null;
            isFirst = false;

            for(int pageNumber = 2; pageNumber <= pages; pageNumber++){
                param.setPage(pageNumber);
                param.setPageNo(pageNumber);
                pageInfos = carDriverDutyService.queryDriverDayDutyList(param);
                result = pageInfos.getList();
                csvDataList = new ArrayList<>();
                if(pageNumber == pages){
                    isLast = true;
                }
                dataTrans( result,  csvDataList);
                entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
            }
            long end = System.currentTimeMillis();
            logger.info("????????????????????????????????????:"+ JSON.toJSONString(param)+"????????????"+(end-start)+"??????;????????????"+pageInfos.getTotal());


        }catch (Exception e){
            logger.error("????????????????????????:????????????"+(param == null?"null":JSON.toJSONString(param)),e);
            return  "????????????????????????";
        }
        return "";
    }
    private  void dataTrans(List<CarDriverDayDutyDTO> result,List<String> csvDataList){
        if(null == result){
            return;
        }
        for (CarDriverDayDutyDTO carDriverDayDutyDTO : result) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(carDriverDayDutyDTO.getDriverName());
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getPhone()==null?"-":("\t"+carDriverDayDutyDTO.getPhone()));
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getCityName()==null?"-":(""+carDriverDayDutyDTO.getCityName()));
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getSupplierName()==null?"-":(""+carDriverDayDutyDTO.getSupplierName()));
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getTeamName()==null?"-":(""+carDriverDayDutyDTO.getTeamName()));
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getTime()==null?"-":("\t"+carDriverDayDutyDTO.getTime()));//????????????
            stringBuffer.append(",");
            stringBuffer.append(StringUtils.isNotEmpty(carDriverDayDutyDTO.getForcedTimes())?carDriverDayDutyDTO.getForcedTimes().replace(",","  "):carDriverDayDutyDTO.getForcedTimes());//??????????????????
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getDutyTimes()==null?"":carDriverDayDutyDTO.getDutyTimes().replaceAll(",","  "));//????????????

            stringBuffer.append(",");

            if(carDriverDayDutyDTO.getStatus() == 2){
                stringBuffer.append("?????????");
            }else if(carDriverDayDutyDTO.getStatus() == 1){
                stringBuffer.append("?????????");
            }else{
                stringBuffer.append("?????????");
            }
            csvDataList.add(stringBuffer.toString());
        }
    }


    /**
     * @Desc: ???????????????????????????
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/saveDriverDayDuty")
    @RequestFunction(menu = DRIVER_DUTY_TIME_SAVE)
    public AjaxResponse saveDriverDayDuty(DutyParamRequest param){
        logger.info("?????????????????????????????????:"+ JSON.toJSONString(param));
        String result = carDriverShiftsService.saveDriverDayDuty(param);
        if("???????????????????????????????????????????????????????????????????????????".equals(result)){
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
    }

    /**
     * @Desc: ??????????????????????????????
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/queryDriverTeamReList")
	@RequiresPermissions(value = { "DriverArrange_look" } )
    @RequestFunction(menu = DRIVER_DUTY_LIST)
    public AjaxResponse queryDriverTeamReList(TeamGroupRequest teamGroupRequest){
        logger.info("????????????????????????????????????:"+ JSON.toJSONString(teamGroupRequest));
        PageDTO pageDTO = carDriverShiftsService.queryDriverTeamReList(teamGroupRequest);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * @Desc: ???????????????????????????
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/queryDurationListByField")
    @RequestFunction(menu = DRIVER_DUTY_TIME_FIELD)
    public AjaxResponse queryDurationListByField(@Verify(param = "teamId", rule = "required") Integer teamId,
                                                 @Verify(param = "cityId", rule = "required") Integer cityId,
                                                 @Verify(param = "supplierId", rule = "required") Integer supplierId){
        logger.info("?????????????????????????????????:"+ JSON.toJSONString(teamId));
        List<CarDriverDurationDTO> list = carDriverShiftsService.queryDurationListByField(cityId, supplierId, teamId);
        return AjaxResponse.success(list);
    }

    /**
    * @Desc: ?????????????????????????????????
     * @param:
    * @return:
    * @Author: lunan
    * @Date: 2018/9/4
    */
    @ResponseBody
    @RequestMapping(value = "/queryMustListByField")
    @RequestFunction(menu = DRIVER_MUST_DUTY_TIME_FIELD)
    public AjaxResponse queryMustListByField(@Verify(param = "teamId", rule = "required") Integer teamId){
        logger.info("???????????????????????????????????????:"+ JSON.toJSONString(teamId));
        List<CarDriverMustDutyDTO> list = carDriverShiftsService.queryMustListByField(teamId);
        return AjaxResponse.success(list);
    }

    /**
     * @Desc: ????????????????????????
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/getCarDriverMustDetail")
    public AjaxResponse getCarDriverMustDetail(@Verify(param = "paramId", rule = "required") String paramId){
        logger.info("??????????????????????????????:"+ JSON.toJSONString(paramId));
        CarDriverMustDutyDTO detail = carDriverMustDutyService.getCarDriverMustDetail(Integer.parseInt(paramId));
        return AjaxResponse.success(detail);
    }

    /**
     * @Desc: ????????????????????????????????????
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdateDriverMust")
    @RequestFunction(menu = TEAM_GROUP_DUTY_SAVE)
    public AjaxResponse saveOrUpdateDriverMust(CarDriverMustDuty param){
        logger.info("??????????????????????????????????????????:"+ JSON.toJSONString(param));
        int result = carDriverMustDutyService.saveOrUpdateDriverMust(param);
        if(result >0){
            return AjaxResponse.success(result);
        }else{
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * @Desc: ??????????????????????????????
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/getDriverMustDutyList")
	@RequiresPermissions(value = { "WorkTimeManage_look" } )
    @RequestFunction(menu = TEAM_GROUP_DUTY_LIST)
    public AjaxResponse getDriverMustDutyList(DutyParamRequest param){
        logger.info("?????????????????????????????? ??????:"+ JSON.toJSONString(param));
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
            return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
        }
        PageDTO pageDTO = carDriverMustDutyService.getDriverMustDutyList(param);
        return AjaxResponse.success(pageDTO);
    }

    /**
     * @Desc: ????????????????????????
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/getCarDriverDurationDetail")
    @RequestFunction(menu = TEAM_GROUP_DURATION_DETAIL)
    public AjaxResponse getCarDriverDurationDetail(@Verify(param = "paramId", rule = "required") String paramId){
        logger.info("??????????????????????????????:"+ JSON.toJSONString(paramId));
        CarDriverDurationDTO detail = carDriverDurationService.getCarDriverDurationDetail(Integer.parseInt(paramId));
        return AjaxResponse.success(detail);
    }

    /**
     * @Desc: ??????/??????????????????
     * @param:
     * @return:
     * @Author: lunan
     * @Date: 2018/9/4
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdateCarDriverDuration")
    @RequestFunction(menu = TEAM_GROUP_DURATION_SAVE)
    public AjaxResponse saveOrUpdateCarDriverDuration(CarDutyDuration param){
        logger.info("??????/????????????????????????:"+ JSON.toJSONString(param));
        int result = carDriverDurationService.saveOrUpdateCarDriverDuration(param);
        if(result >0){
            return AjaxResponse.success(result);
        }else{
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
    * @Desc: ????????????????????????
    * @param:
    * @return:
    * @Author: lunan
    * @Date: 2018/9/4
    */
    @ResponseBody
    @RequestMapping(value = "/getDriverDurationList")
	@RequiresPermissions(value = { "TimeLengthManage_look" } )
    @RequestFunction(menu = TEAM_GROUP_DURATION_LIST)
    public AjaxResponse getDriverDurationList(DutyParamRequest param){
        logger.info("??????????????????????????????:"+ JSON.toJSONString(param));
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
            return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
        }
        PageDTO pageDTO = carDriverDurationService.getDriverDurationList(param);
        return AjaxResponse.success(pageDTO);
    }

    /** 
    * @Desc: ???????????? 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/issueDriverDuty")
    @RequestFunction(menu = DRIVER_DUTY_PUBLISH)
    public AjaxResponse issueDriverDuty(DutyParamRequest param){
        logger.info("??????????????????:"+ JSON.toJSONString(param));
        int result = carDriverDutyService.issueDriverDuty(param);
        ServiceReturnCodeEnum typeByCode = ServiceReturnCodeEnum.getTypeByCode(result);
        if(result == 2){
            AjaxResponse success = AjaxResponse.success(result);
            Map<String,String> map = new HashedMap();
            map.put("successMsg",typeByCode.getName());
            success.setData(map);
            return success;
        }
        return AjaxResponse.success(result);
    }

    /** 
    * @Desc: ?????????????????????????????? (???????????????????????????????????? status 1)
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/queryDriverMonthDutyData")
	@RequiresPermissions(value = { "PublishArrange_look" , "LookArrange_look"} , logical=Logical.OR )
    @RequestFunction(menu = DRIVER_DUTY_PUBLISH_LIST)
    public AjaxResponse queryDriverMonthDutyData(DutyParamRequest param) {
        logger.info("???????????????????????????????????????:"+ (param== null?"null":JSON.toJSONString(param)));
      try{
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
                return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
            }
            if(Check.NuNObj(param) || Check.NuNObj(param.getUnpublishedFlag()) ){
                return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
            }
            long start = System.currentTimeMillis();
            PageInfo<CarDriverDayDutyDTO> pageInfos = carDriverDutyService.queryDriverDayDutyList(param);
            pageInfos.getList().forEach(elem -> {
                elem.setPhone(MobileOverlayUtil.doOverlayPhone(elem.getPhone()));
            });
            PageDTO pageDTO = new PageDTO();
			pageDTO.setTotal((int)pageInfos.getTotal());
			pageDTO.setResult(pageInfos.getList());

            long end = System.currentTimeMillis();
            logger.info("??????????????????????????????????????????:"+ JSON.toJSONString(param)+"????????????"+(end-start)+"?????????pageNumber="+param.getPageNo()
                            +";???????????????"+pageDTO.getTotal()
                    +"????????????totalPage = "+pageInfos.getPages()
            );
            return AjaxResponse.success(pageDTO);
        }catch (Exception e){
            logger.error("?????????????????????????????????????????????:"+ (param== null?"null":JSON.toJSONString(param)),e);
              return AjaxResponse.success(null);
        }
    }

}