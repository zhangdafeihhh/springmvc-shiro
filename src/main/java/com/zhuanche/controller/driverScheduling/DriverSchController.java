package com.zhuanche.controller.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.dto.driverDuty.CarDriverDurationDTO;
import com.zhuanche.dto.driverDuty.CarDriverMustDutyDTO;
import com.zhuanche.dto.driverDuty.DutyExcelDTO;
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
import com.zhuanche.util.PageUtils;
import com.zhuanche.util.dateUtil.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.excel.ExportExcelUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
     * 设置文件下载 response格式
     */
    private HttpServletResponse setResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        return response;
    }


    /** 
    * @Desc: 导出排班信息 
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/5 
    */ 
    @RequestMapping("/exportDutyToExcel")
    @ResponseBody
    public void exportDutyToExcel(HttpServletResponse response, HttpServletRequest request,DutyParamRequest param){

        try{

            logger.info("下载符合条件排班列表入参:"+ JSON.toJSONString(param));
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
                return  ;
            }
            if(Check.NuNObj(param) || Check.NuNObj(param.getUnpublishedFlag()) ){
                return  ;
            }
            long start = System.currentTimeMillis();
            param.setPage(1);
            //设置导出单文件阈值 3000
            param.setPageSize(20);
            PageDTO pageDTO = carDriverDutyService.queryDriverDayDutyList(param);

            List<CarDriverDayDutyDTO> result = pageDTO.getResult();
            logger.info("下载符合条件排班列表入参:"+ JSON.toJSONString(param)+"，pageNumber="+0+";总条数为："+pageDTO.getTotal()+"；查询结果为："+(result==null?"null":JSON.toJSONString(result)));
            List<String> csvDataList = new ArrayList<>();
            dataTrans( result,  csvDataList);

            String fileName = "司机排班信息"+DateUtil.dateFormat(new Date(),DateUtil.intTimestampPattern)+".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            Integer total = pageDTO.getTotal();
            //计算总页数
            Integer totalPage = PageUtils.getTotalPage(total,pageDTO.getPageSize());

            for(int pageNumber = 2; pageNumber <= totalPage; pageNumber++){
                param.setPageNo(pageNumber);
                PageDTO page = carDriverDutyService.queryDriverDayDutyList(param);
                logger.info("下载符合条件排班列表入参:"+ JSON.toJSONString(param)+"，pageNumber="+pageNumber+";总条数为："+pageDTO.getTotal()+"；查询结果为："+(result==null?"null":JSON.toJSONString(result)));

                result = page.getResult();
                dataTrans( result,  csvDataList);

            }
            List<String> headerList = new ArrayList<>();
            headerList.add("司机姓名,手机号,城市,供应商,车队,排班日期,强制上班时间,排班时长,状态");

            CsvUtils.exportCsv(response,csvDataList,headerList,fileName);
            long end = System.currentTimeMillis();
            logger.info("下载符合条件排班列表入参:"+ JSON.toJSONString(param)+"，耗时："+(end-start)+"毫秒;总条数："+total);

        }catch (Exception e){
            logger.error("导出排班信息 异常:{}",e);
            return ;
        }
        return ;
    }
    private  void dataTrans(List<CarDriverDayDutyDTO> result,List<String> csvDataList){
        if(result != null){
            return;
        }
        for (CarDriverDayDutyDTO carDriverDayDutyDTO : result) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(carDriverDayDutyDTO.getDriverName());
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getPhone());
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getCityName());
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getSupplierName());
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getTeamName());
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getDutyTimes());
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getForcedTimes());
            stringBuffer.append(",");
            stringBuffer.append(carDriverDayDutyDTO.getTime());

            stringBuffer.append(",");

            if(carDriverDayDutyDTO.getStatus() == 2){
                stringBuffer.append("已发布");
            }else if(carDriverDayDutyDTO.getStatus() == 1){
                stringBuffer.append("未发布");
            }else{
                stringBuffer.append("未发布");
            }
            csvDataList.add(stringBuffer.toString());
        }
    }


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
        PageDTO pageDTO = carDriverShiftsService.queryDriverTeamReList(teamGroupRequest);
        return AjaxResponse.success(pageDTO);
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
        List<CarDriverDurationDTO> list = carDriverShiftsService.queryDurationListByField(cityId, supplierId, teamId);
        return AjaxResponse.success(list);
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
        List<CarDriverMustDutyDTO> list = carDriverShiftsService.queryMustListByField(teamId);
        return AjaxResponse.success(list);
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
        CarDriverMustDutyDTO detail = carDriverMustDutyService.getCarDriverMustDetail(Integer.parseInt(paramId));
        return AjaxResponse.success(detail);
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
        int result = carDriverMustDutyService.saveOrUpdateDriverMust(param);
        if(result >0){
            return AjaxResponse.success(result);
        }else{
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
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
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
            return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
        }
        PageDTO pageDTO = carDriverMustDutyService.getDriverMustDutyList(param);
        return AjaxResponse.success(pageDTO);
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
        CarDriverDurationDTO detail = carDriverDurationService.getCarDriverDurationDetail(Integer.parseInt(paramId));
        return AjaxResponse.success(detail);
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
        int result = carDriverDurationService.saveOrUpdateCarDriverDuration(param);
        if(result >0){
            return AjaxResponse.success(result);
        }else{
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
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
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
            return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
        }
        PageDTO pageDTO = carDriverDurationService.getDriverDurationList(param);
        return AjaxResponse.success(pageDTO);
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
    * @Desc: 查看符合条件排班列表 (包含发布司机排班数据列表 status 1)
    * @param:
    * @return:  
    * @Author: lunan
    * @Date: 2018/9/4 
    */ 
    @ResponseBody
    @RequestMapping(value = "/queryDriverMonthDutyData")
    public AjaxResponse queryDriverMonthDutyData(DutyParamRequest param) {
        logger.info("查看符合条件排班列表入参:"+ JSON.toJSONString(param));
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
            return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
        }
        if(Check.NuNObj(param) || Check.NuNObj(param.getUnpublishedFlag()) ){
            return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
        }
        long start = System.currentTimeMillis();
        PageDTO pageDTO = carDriverDutyService.queryDriverDayDutyList(param);
        long end = System.currentTimeMillis();
        logger.info("分页查询符合条件排班列表入参:"+ JSON.toJSONString(param)+"，耗时："+(end-start)+"毫秒，pageNumber="+0+";总条数为："+pageDTO.getTotal()+"；查询结果为："+(pageDTO.getResult()==null?"null":JSON.toJSONString(pageDTO.getResult())));
        return AjaxResponse.success(pageDTO);
    }

}