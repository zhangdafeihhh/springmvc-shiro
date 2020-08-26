package com.zhuanche.controller.intercity;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.intercity.IntegerCityReadyOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.text.MessageFormat;


/**
 * @Author fanht
 * @Description 待指派订单
 * @Date 2020/8/26 上午9:42
 * @Version 1.0
 */
@Controller
@RequestMapping("/interCityReadyOrder")
public class InterCityReadyOrderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IntegerCityReadyOrderService readyOrderService;


    /**
     * 分页查询所有的线路
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/orderWrestQuery", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse orderWrestQuery(@Verify(param = "pageNum", rule = "required") Integer pageNum,
                                        @Verify(param = "pageSize", rule = "required")@RequestParam(value="pageSize", defaultValue="1000") Integer pageSize
                                        ) {
        logger.info(MessageFormat.format("查询线路入参:pageNum:{0},pageSize:{1}", pageNum, pageSize));

        try {
            return readyOrderService.orderWrestQuery(pageNum,pageSize);
        } catch (Exception e) {
            logger.error("查询线路异常",e);
            return AjaxResponse.success(null);
        }
    }




}
