package com.zhuanche.threads;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.serv.CustomerAppraisalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

public class CustomerAppraisalExportHelper extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(CustomerAppraisalExportHelper.class);

    private CustomerAppraisalService customerAppraisalService;

    private int pageNo;

    private int pageSize;

    private CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO;

    Hashtable<String,PageInfo<CarBizCustomerAppraisalStatisticsDTO> > hashtable;

    private CountDownLatch endGate;

    public CustomerAppraisalExportHelper(CustomerAppraisalService customerAppraisalService, int pageNo, int pageSize,
                                         Hashtable<String,PageInfo<CarBizCustomerAppraisalStatisticsDTO> > hashtable,
                                         CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO,
                                         CountDownLatch endGate) {
        this.customerAppraisalService = customerAppraisalService;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.hashtable = hashtable;
        this.carBizCustomerAppraisalStatisticsDTO = carBizCustomerAppraisalStatisticsDTO;
        this.endGate = endGate;

    }

    @Override
    public void run() {
        try{
            PageInfo<CarBizCustomerAppraisalStatisticsDTO> pageInfo = customerAppraisalService.queryCustomerAppraisalStatisticsListV2(carBizCustomerAppraisalStatisticsDTO,1
                    ,  pageSize  );
            hashtable.put("page_"+pageNo,pageInfo);

        }catch (Exception e){
            logger.error("多线程查询司机评分异常,参数为：pageNo="+pageNo+",pageSize="+pageSize
                            +",carBizCustomerAppraisalStatisticsDTO="+(carBizCustomerAppraisalStatisticsDTO==null?"null": JSON.toJSONString(carBizCustomerAppraisalStatisticsDTO)),
                    e);
        }finally {
            endGate.countDown();
        }


    }
}
