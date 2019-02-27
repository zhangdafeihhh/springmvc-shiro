package com.zhuanche.controller.driver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.enums.DriverActionEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.driver.DriverActionVO;
import com.zhuanche.exception.PermissionException;
import com.zhuanche.serv.deiver.DriverActionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.zhuanche.common.enums.MenuEnum.*;

@RequestMapping("/driverAction")
@Controller
public class DriverActionController {


    private Logger logger = LoggerFactory.getLogger(DriverActionController.class);

    @Resource
    private DriverActionService actionService;

    @RequestMapping("/actionList")
    @ResponseBody
    @RequestFunction(menu = DRIVER_ACTION_LIST)
    @RequiresPermissions(value = "DriverAction_Look")
    public AjaxResponse getDriverActionList(String orderNo, DriverActionVO driverActionVO,
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "30") int pageSize) {
        if (StringUtils.isEmpty(driverActionVO.getDriverName())
                && StringUtils.isEmpty(driverActionVO.getDriverLicense())
                && StringUtils.isEmpty(driverActionVO.getDriverPhone())
                && StringUtils.isEmpty(orderNo)) {
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "司机姓名, 手机号, 车牌号, 订单号 必须有一个不为空");
        }
        if (StringUtils.isEmpty(driverActionVO.getTime())){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "查询时间不能为空");
        }
        try {
            String table = transferTableName(driverActionVO.getTime());
            PageDTO actionList = actionService.getActionList(driverActionVO, table, orderNo, pageNum, pageSize);
            return AjaxResponse.success(actionList);
        }
        catch (IllegalArgumentException e){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, e.getMessage());
        }
        catch (PermissionException e){
            return AjaxResponse.failMsg(RestErrorCode.HTTP_FORBIDDEN, e.getMessage());
        }
        catch (Exception e){
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }


    @RequestMapping("/actionEnum")
    @ResponseBody
    @RequestFunction(menu = DRIVER_ACTION_ENUM)
//    @RequiresPermissions(value = "DriverAction_Enums")
    public AjaxResponse getActionList(){
        Map<Integer, String> map = DriverActionEnum.getMap();
        JSONArray result = new JSONArray();
        map.forEach((key, value) ->{
                    JSONObject object = new JSONObject();
                    object.put("actionId", key);
                    object.put("actionName", value);
                    result.add(object);
                }
        );
        return AjaxResponse.success(result);
    }

    @RequestMapping("/timeLine")
    @ResponseBody
    @RequestFunction(menu = DRIVER_ACTION_TIMELINE)
    @RequiresPermissions(value = "DriverAction_TimeLine")
    public AjaxResponse getActionTimeLine(DriverActionVO driverActionVO){
        if (driverActionVO.getDriverId() == null || StringUtils.isBlank(driverActionVO.getTime())){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "司机id 查询时间不能为空");
        }
        try {
            String tableName = transferTableName(driverActionVO.getTime());
            return AjaxResponse.success(actionService.queryTimeLine(driverActionVO, tableName));
        }catch (IllegalArgumentException e){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, e.getMessage());
        }catch (PermissionException e){
            return AjaxResponse.failMsg(RestErrorCode.HTTP_FORBIDDEN, e.getMessage());
        }
    }

    private String transferTableName(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parse = dateFormat.parse(date);
            SimpleDateFormat format= new SimpleDateFormat("yyyy_MM_dd");
            return "car_biz_driver_record_" + format.format(parse);
        } catch (ParseException e) {
            logger.error("转换日期格式错误", e);
            throw new IllegalArgumentException("date日期错误");
        }
    }

}
