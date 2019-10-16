package com.zhuanche.controller.interCity;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/14 下午4:19
 * @Version 1.0
 */
@Controller
@RequestMapping("/interCityMainOrder")
public class InterCityMainOrderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 订单查询
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param supplierId
     * @param orderState 待指派、待服务、已出发、已上车、服务中、已送达、已完成、已取消
     * @param serviceType 服务类别: 新城际拼车、新城际包车
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param licensePlates 司机车牌号
     * @param mainOrderNo 主订单号
     * @param beginCreateDate 下单开始时间
     * @param endCreateDate 下单结束时间
     * @param beginCostStartDate 订单完成开始时间
     * @param beginCostEndDate 订单完成结束时间
     * @return
     */
    public AjaxResponse mainOrderQuery(@Verify(param = "pageNum",rule = "required") Integer pageNum,
                                       @Verify(param = "pageSize",rule = "required") Integer pageSize,
                                       Integer cityId,
                                       Integer supplierId,
                                       Integer orderState,
                                       Integer serviceType,
                                       String driverName,
                                       String driverPhone,
                                       String licensePlates,
                                       String mainOrderNo,
                                       String beginCreateDate,
                                       String endCreateDate,
                                       String beginCostStartDate,
                                       String beginCostEndDate){


        return null;
    }


    /**
     * 拼车主订单详情
     * @param mainOrderNo
     * @return
     */
    @RequestMapping(value = "/mainOrderDetail",method = RequestMethod.GET)
    public AjaxResponse mainOrderDetail(String mainOrderNo){
        logger.info("获取拼车单订单详情入参:mainOrderNo" + mainOrderNo);
        return null;
    }
}
