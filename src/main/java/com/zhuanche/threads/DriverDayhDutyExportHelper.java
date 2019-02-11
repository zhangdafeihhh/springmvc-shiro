package com.zhuanche.threads;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.driverScheduling.CarDriverDutyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

public class DriverDayhDutyExportHelper extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(DriverDayhDutyExportHelper.class);

    private CarDriverDutyService carDriverDutyService;

    DutyParamRequest param;


    Hashtable<String,PageInfo<CarDriverDayDutyDTO> > hashtable;

    private CountDownLatch endGate;

    public DriverDayhDutyExportHelper(CarDriverDutyService carDriverDutyService,
                                      Hashtable<String,PageInfo<CarDriverDayDutyDTO> > hashtable,
                                      DutyParamRequest param,
                                      CountDownLatch endGate) {
        this.carDriverDutyService = carDriverDutyService;
        this.param = param;

        this.hashtable = hashtable;
        this.endGate = endGate;


    }

    @Override
    public void run() {
        try{
            logger.info("多线程分页查询司机司机排班信息,参数为：pageNo="+param.getPageNo()+",pageSize="+param.getPageSize()
                            +",param="+(param==null?"null": JSON.toJSONString(param))
                    );
            PageInfo<CarDriverDayDutyDTO> pageInfos = carDriverDutyService.queryDriverDayDutyList(param);
            hashtable.put("page_"+param.getPageNo(),pageInfos);

        }catch (Exception e){
            logger.info("多线程分页查询司机司机排班信息异常,参数为：pageNo="+param.getPageNo()+",pageSize="+param.getPageSize()
                    +",param="+(param==null?"null": JSON.toJSONString(param)),e);
        }finally {
            endGate.countDown();
        }


    }
}
