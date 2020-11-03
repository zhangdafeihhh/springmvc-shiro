package com.zhuanche.controller.intercity;

import com.zhuanche.common.web.AjaxResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author fanht
 * @Description 城际拼车设置司机和线路关联关系
 * @Date 2020/11/3 上午10:05
 * @Version 1.0
 */
@Controller
@RestController("/interDriverLineRel")
@Log4j
public class InterDriverLineRelController {


    /**
     * 根据userId查询
     * @param userId
     * @return
     */
    @RequestMapping("/queryDetail")
    public AjaxResponse queryDetail(Integer userId){
        log.info("根据userId查询线路和司机关系入参:" + userId);


    }
}
