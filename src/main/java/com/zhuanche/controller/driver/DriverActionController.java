package com.zhuanche.controller.driver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.enums.DriverActionEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.driver.DriverActionVO;
import com.zhuanche.exception.PermissionException;
import com.zhuanche.serv.deiver.DriverActionService;
import org.apache.commons.lang3.StringUtils;
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

@RequestMapping("/driverAction")
@Controller
public class DriverActionController {


    private Logger logger = LoggerFactory.getLogger(DriverActionController.class);

    @Resource
    private DriverActionService actionService;

    @RequestMapping("/actionList")
    @ResponseBody
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
        String tableDate;
        try {
            tableDate = transferDate(driverActionVO.getTime());
        }catch (IllegalArgumentException e){
            return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID, "日期格式错误");
        }catch (Exception e){
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        String table = TABLE_PREFIX + tableDate;
        try {
            PageDTO actionList = actionService.getActionList(driverActionVO, table, orderNo, pageNum, pageSize);
            return AjaxResponse.success(actionList);
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

    private String transferDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parse = dateFormat.parse(date);
            SimpleDateFormat format= new SimpleDateFormat("yyyy_MM_dd");
            return format.format(parse);
        } catch (ParseException e) {
            logger.error("转换日期格式错误", e);
            throw new IllegalArgumentException("date日期错误");
        }
    }

    private static final String TABLE_PREFIX = "car_biz_driver_record_";
}
