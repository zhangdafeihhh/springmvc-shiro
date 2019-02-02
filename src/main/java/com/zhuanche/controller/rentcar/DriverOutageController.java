package com.zhuanche.controller.rentcar;

import com.google.common.collect.Maps;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.DriverOutageDTO;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.DriverUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_TEMPORARY_STOP_MANAGE;


/**
 * ClassName: DriverOutageController 
 * date: 2017年5月4日 下午7:19:45 
 *
 * @author zhulingling
 * @version
 * @since JDK 1.6 
 */
@RestController
@RequestMapping("/driverOutage")
public class DriverOutageController {

    private static final Logger logger = LoggerFactory.getLogger(DriverOutageController.class);

    @Autowired
    private DriverOutageService driverOutageService;
    @Autowired
    private CarDriverTeamService carDriverTeamService;


    /**
     * 查询临时停运司机列表
     * @param cityId    城市
     * @param supplierId    供应商
     * @param carGroupId    服务类型
     * @param outageSource  停运来源
     * @param driverName    司机姓名
     * @param driverPhone   手机号
     * @param startDateBegin    停运开始日期
     * @param startDateEnd      停运结束日期
     * @param removeStatus      解除状态
     * @return
     */
    @RequestMapping(value = "/queryDriverOutageData")
	@RequiresPermissions(value = { "TemporaryCarry_look" } )
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
	} )
    @RequestFunction(menu = DRIVER_TEMPORARY_STOP_MANAGE)
    public AjaxResponse queryDriverOutageData(@Verify(param = "cityId",rule = "") Integer cityId,
                                              Integer supplierId,
                                              Integer carGroupId,
                                              Integer outageSource,
                                              String driverName,
                                              String driverPhone,
                                              @Verify(param = "startDateBegin",rule = "")String startDateBegin,
                                              String startDateEnd,
                                              Integer removeStatus,
                                              Integer page,
                                              Integer pageSize){
        logger.info("【司机停运】司机停运列表数据:queryDriverOutageData.json");
        DriverOutage params = new DriverOutage();
        params.setCityId(cityId);
        params.setSupplierId(supplierId);
        params.setCarGroupId(carGroupId);
        params.setOutageSource(outageSource);
        params.setDriverName(driverName);
        params.setDriverPhone(driverPhone);
        params.setStartDateBegin(startDateBegin);
        params.setStartDateEnd(startDateEnd);
        params.setRemoveStatus(removeStatus);
        if(null != page && page > 0)
            params.setPage(page);
        if(null != pageSize && pageSize > 0)
            params.setPagesize(pageSize);

        List<DriverOutage> rows = new ArrayList<>();
        int total = 0;
        //权限
        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
        params.setCities(cities);
        params.setSupplierIds(suppliers);
        //查询用户权限范围内里的司机信息
        String driverIds = DriverUtils.getDriverIdsByUserTeams(carDriverTeamService,WebSessionUtil.getCurrentLoginUser().getTeamIds());
        params.setDriverIds(driverIds);

        //查数量
        total = driverOutageService.queryForInt(params);
        if(total==0){
            PageDTO result = new PageDTO(params.getPage(), params.getPagesize(), 0, rows);
            return AjaxResponse.success(result);
        }
        //查数据
        rows = driverOutageService.queryForListObject(params);
        return AjaxResponse.success(new PageDTO(params.getPage(), params.getPagesize(), total, BeanUtil.copyList(rows, DriverOutageDTO.class)));
    }

    /**
     *
     *导出司机停运操作
     * @return
     */

    @RequestMapping("/exportDriverOutage")
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
	} )
    public void exportDriverOutage(@Verify(param = "cityId",rule = "") Integer cityId,
                                   Integer supplierId,
                                   Integer carGroupId,
                                   Integer outageSource,
                                   String driverName,
                                   String driverPhone,
                                   @Verify(param = "startDateBegin",rule = "")String startDateBegin,
                                   String startDateEnd,
                                   Integer removeStatus,
                                   HttpServletRequest request,
                                   HttpServletResponse response){
        logger.info("【司机停运】导出司机停运操作");
        try {
            DriverOutage params = new DriverOutage();
            params.setCityId(cityId);
            params.setSupplierId(supplierId);
            params.setCarGroupId(carGroupId);
            params.setOutageSource(outageSource);
            params.setDriverName(driverName);
            params.setDriverPhone(driverPhone);
            params.setStartDateBegin(startDateBegin);
            params.setStartDateEnd(startDateEnd);
            params.setRemoveStatus(removeStatus);
            //权限
            String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
            String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
            params.setCities(cities);
            params.setSupplierIds(suppliers);
            //查询用户权限范围内里的司机信息
            String driverIds = DriverUtils.getDriverIdsByUserTeams(carDriverTeamService,WebSessionUtil.getCurrentLoginUser().getTeamIds());
            params.setDriverIds(driverIds);

            List<DriverOutage> rows = driverOutageService.queryForListObjectNoLimit(params);
            @SuppressWarnings("deprecation")
            Workbook wb = driverOutageService.exportExcelDriverOutage(rows,request.getRealPath("/")+File.separator+"template"+File.separator+"driverOutage_info.xlsx");
            exportExcelFromTemplet(request, response, wb, new String("临时停运司机".getBytes("gb2312"), "iso8859-1"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportExcelFromTemplet(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) throws IOException {
        if(StringUtils.isEmpty(fileName)) {
            fileName = "exportExcel";
        }
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");//指定下载的文件名
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ServletOutputStream os =  response.getOutputStream();
        wb.write(os);
        os.close();
    }


    /**
     * 根据手机号查询司机名称
     * @param driverPhone
     * @return
     */
    @RequestMapping(value = "/queryDriverNameByPhone")
    @MasterSlaveConfigs(configs={ 
			@MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
	} )
    public AjaxResponse queryDriverNameByPhone(@Verify(param = "driverPhone", rule = "mobile") String driverPhone){
        logger.info("【司机停运】查询手机号"+driverPhone+"所对应的司机姓名");
        Map<String,Object> result = new HashMap<String,Object>();
        //查数据
        DriverOutage params = new DriverOutage();
        params.setDriverPhone(driverPhone);
        DriverOutage outage = this.driverOutageService.queryDriverNameByPhone(params);
        if(outage==null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
        }else{
            //返回为1 ==========成功
            result.put("driverPhone", params.getDriverPhone());
            result.put("driverName", outage.getDriverName());
            result.put("driverId", outage.getDriverId());
            return AjaxResponse.success(result);
        }
    }

    /**
     *
     * @param outStopLongTime   停运时常
     * @param driverName    司机姓名
     * @param driverPhone   司机手机号
     * @param outStartDate  停运时间
     * @param outageReason  停运原因
     * @param driverId  司机id
     * @return
     */
    @RequestMapping(value="/saveDriverOutage")
    public AjaxResponse saveDriverOutage(
                                    @Verify(param = "outStopLongTime",rule = "required|max(10000)")Double outStopLongTime,
                                    @Verify(param = "driverName",rule = "required")String driverName,
                                    @Verify(param = "driverPhone",rule = "mobile")String driverPhone,
                                    @Verify(param = "outStartDate",rule = "required")String outStartDate,
                                    @Verify(param = "outageReason",rule = "required")String outageReason,
                                    @Verify(param = "driverId",rule = "required")Integer driverId){
        if(outStopLongTime%0.5 != 0){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"请设定停运时长为0.5的倍数");
        }
        if(outageReason.length() > 50)
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"请输入50字以内的停运说明");
//        DateUtil.beforeNDayDate()
        DriverOutage params = new DriverOutage();
        params.setDriverPhone(driverPhone);
        params.setDriverName(driverName);
        params.setOutStartDateStr(outStartDate);
        try {
            params.setOutStartDate(DateUtils.parse(outStartDate, "yyyy-MM-dd HH:mm:ss", Date.class));
        } catch (Exception e){
            logger.error("保存临时停运开始时间格式错误,e={}" + e);
            return AjaxResponse.fail(998, "始时间格式错误,yyyy-MM-dd HH:mm:ss");
        }
        params.setOutageReason(outageReason);
        params.setOutStopLongTime(outStopLongTime);
        params.setDriverId(driverId);

        logger.info("【司机停运】临时停运新增=="+params.toString());
        Map<String,Object> result = new HashMap<String,Object>();
        if(params.getDriverId()==null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_NOT_EXIST);
        }
        DriverOutage outage = driverOutageService.queryDriverOutageAllByDriverId(params);
        if(outage!=null){
            return AjaxResponse.fail(RestErrorCode.DRIVER_OUTAGEALL_EXIST);
        }else{
            result = this.driverOutageService.saveDriverOutage(params);
        }
        return getResponse(result);
    }

    /**
     *
     * @param outageId  停运id
     * @param removeReason  解除原因
     * @return
     */
    @RequestMapping(value="/updateDriverOutage")
    public AjaxResponse updateDriverOutage(@Verify(param = "outageId",rule = "required")Integer outageId,
                                     @Verify(param = "removeReason",rule = "required")String removeReason,
                                           @Verify(param = "removeStatus",rule = "required|min(1)|max(4)")Integer removeStatus){
        DriverOutage params = new DriverOutage();
        params.setOutageId(outageId);
        params.setRemoveStatus(removeStatus);//解除状态 1：已执行 2：执行中 3：待执行 4：撤销(未执行解除)',
        params.setRemoveReason(removeReason);


        logger.info("【司机停运】临时停运解除数据=="+params.toString());
        Map<String,Object> result = new HashMap<String,Object>();
        result = this.driverOutageService.updateDriverOutage(params);
        logger.info("" + result);
        return getResponse(result);
    }

    public AjaxResponse getResponse(Map<String,Object> result){
        try{
//            JSONObject jsonStr = (JSONObject)result.get("jsonStr");

            Integer result1 = Integer.valueOf( result.get("result").toString() );
            Map response = Maps.newHashMap();
            if( 0 == result1 ){
                String exception = result.get("exception").toString();
                return AjaxResponse.fail(996, exception);
            } else if(1 == result1){
                Object success = result.get("success");
                Object error = result.get("error");
                if(success != null)
                    response.put("success", success);
                if(error != null)
                    response.put("error", error);
                return AjaxResponse.success(response);
            }
            return AjaxResponse.fail(999);
        } catch (Exception e){
            return AjaxResponse.fail(999);
        }
    }

}
