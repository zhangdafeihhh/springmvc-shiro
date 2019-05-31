package com.zhuanche.serv.order.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.DriverCostDetailVO;
import com.zhuanche.entity.rentcar.OrderCostDetailInfo;
import com.zhuanche.entity.rentcar.OrderDriverCostDetailVO;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.order.DriverFeeDetailService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@Service
public class DriverFeeDetailServiceImpl implements DriverFeeDetailService {

    private static ExecutorService executor = new ThreadPoolExecutor(5, 10, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(20));
    private static final int MAX_DEAL = 500;//对多数据进行分组，500条一组，一组使用一个线程进行执行
    @Value("${driver.fee.server.api.base.url}")
    private String DRIVER_FEE_SERVICE_API_BASE_URL;

    private static final Logger logger = LoggerFactory.getLogger(DriverFeeDetailServiceImpl.class);

    @Value("${ordercost.server.api.base.url}")
    private String ORDERCOST_SERVICE_API_BASE_URL;

    /**
     * http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21053623
     * 费用明细 For H5 司机端费用详情
     * <p>
     * orderNo/orderId传一个即可
     *
     * @param orderNo    订单号
     * @param orderId    订单id
     * @param buyoutFlag 0-非一口价 1-一口价
     */
    public DriverCostDetailVO getDriverCostDetail(String orderNo, int orderId, Integer buyoutFlag) {
        if (StringUtils.isBlank(orderNo) && orderId != 0)
            return null;
        if(null == buyoutFlag){
            OrderCostDetailInfo info = getOrderCostDetailInfo(orderNo);
            buyoutFlag = info.getBuyOutFlag();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        params.put("orderId", orderId);
        params.put("isFix", buyoutFlag);
        params.put("isDriver", 1);
        String detail = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.GET, ORDERCOST_SERVICE_API_BASE_URL + "/orderCostdetailDriver/getCostDetailForH5", params, null, "UTF-8");
        if (detail == null) {
            logger.error("查询/orderCostdetailDriver/getCostDetailForH5返回空，入参为：orderNo:" + orderNo + "  orderId:" + orderId + "  buyoutFlag:" + buyoutFlag);
            return null;
        }
        logger.info("调用计费查询司机费用明细接口返回：" + detail);
        RPCResponse detailResponse = RPCResponse.parse(detail);
        if (null == detailResponse || detailResponse.getCode() != 0 || detailResponse.getData() == null) {
            logger.error("查询/orderCostdetailDriver/getCostDetailForH5返回空，入参为：orderNo:" + orderNo + "  orderId:" + orderId + "  buyoutFlag:" + buyoutFlag);
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(detailResponse.getData()), DriverCostDetailVO.class);
    }

    @Override
    public OrderDriverCostDetailVO getOrderDriverCostDetailVO(String orderNo, long orderId){
        if (StringUtils.isBlank(orderNo)){
            return null;
        }
        try{
            Map<String, Object> params = new HashMap<>(1);
            params.put("orderNo", orderNo);
            params.put("orderId", orderId);
            String url = DRIVER_FEE_SERVICE_API_BASE_URL + "/orderCost/getOrderDriverCostDetail";
            JSONObject result = MpOkHttpUtil.okHttpGetBackJson(url, params, 1, "");
            if (result == null){
                logger.error("查询计费接口返回为空");
                return null;
            }
            Integer resultCode = result.getInteger(Constants.CODE);
            if (resultCode == null || resultCode != Constants.SUCCESS_CODE){
                logger.info("查询计费司机详情无数据 响应结果: {}", result.toJSONString());
                return null;
            }
            return result.getJSONObject(Constants.DATA).toJavaObject(OrderDriverCostDetailVO.class);
        }catch (Exception e){
        }
        return null;
    }

    /**
     * 根据订单号获取司机费用明细（批量）
     *
     * @param orderIds
     * @return
     */
    public List<OrderDriverCostDetailVO> getOrderDriverCostDetailVOBatch(List<String> orderIds) {
        if (null == orderIds || orderIds.size() == 0) {
            logger.info("接口调用入参订单号为空");
            return new ArrayList<>();
        }
        List<OrderDriverCostDetailVO> resultList = new ArrayList<>();
        int times = (orderIds.size() + MAX_DEAL - 1) / MAX_DEAL;
        CountDownLatch countDownLatch = new CountDownLatch(times);//一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
        try {
            for (int i = 0; i < times; i++) {
                if (i == times - 1) {
                    //查询指派改派时间
                    executor.execute(new addListRunnable(orderIds.subList(i * MAX_DEAL, orderIds.size()), countDownLatch,resultList,i));//调用业务逻辑
                } else {
                    executor.execute(new addListRunnable(orderIds.subList(i * MAX_DEAL, (i + 1) * MAX_DEAL), countDownLatch,resultList,i));
                }
            }
            countDownLatch.await();//一个线程(或者多个)， 等待另外N个线程完成某个事情之后才能执行
        } catch (Exception e) {
            e.printStackTrace();
            }

        return resultList;
    }

    private class addListRunnable implements Runnable{

        private List<String> list;
        private CountDownLatch countDownLatch;
        private List<OrderDriverCostDetailVO> resultList;
        private int times;
        public addListRunnable(List<String> list,CountDownLatch countDownLatch,List<OrderDriverCostDetailVO> resultList,int times){
            super();
            this.list = list;
            this.countDownLatch = countDownLatch;
            this.resultList = resultList;
            this.times = times;
        }
        @Override
        public void run() {
            try {
                List<OrderDriverCostDetailVO> batchList = buildOrderDriverCostDetailBatch(list);
                //resultList.addAll(batchList);
                //模拟业务执行，这里并没有对list进行操作
                //Thread.sleep(1000);
                //System.out.println("当前线程为"+Thread.currentThread().getId());//输出当前线程id

//                List<OrderDriverCostDetailVO> newList = new ArrayList<>();
//                OrderDriverCostDetailVO v  = new OrderDriverCostDetailVO();
//                v.setOrderId(times);
//                v.setTotalAmount(new BigDecimal(1.0));
//                newList.add(v);
                synchronized (resultList){
                    resultList.addAll(batchList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                countDownLatch.countDown();//完成一次操作，计数减一
            }

        }

    }


    private List<OrderDriverCostDetailVO> buildOrderDriverCostDetailBatch(List<String> orderIds){

        try {
            Map<String, Object> httpParams = new HashMap<>();
            httpParams.put("orderIds", String.join(",", orderIds));
            String orderInfo = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.GET, DRIVER_FEE_SERVICE_API_BASE_URL + "/orderCost/getBatchOrderAmountInfo", httpParams, null, "UTF-8");
            if (orderInfo == null) {
                logger.error("查询/orderCost/findOrderDriverCostDetails返回空，入参为：" + String.join(",", orderIds));
                return new ArrayList<>();
            }
            logger.info("调用计费查询司机费用明细接口返回：" + orderInfo);
            RPCResponse orderResponse = RPCResponse.parse(orderInfo);
            if (null == orderResponse || orderResponse.getCode() != 0 || orderResponse.getData() == null) {
                logger.info("相关司机费用信息不存在，入参订单号：" + String.join(",", orderIds));
                return new ArrayList<>();
            }
            return JSON.parseArray(JSON.toJSONString(orderResponse.getData()), OrderDriverCostDetailVO.class);
        } catch (Exception e) {
            logger.error("查询/orderCost/getOrderDriverCostDetails异常:", e);
            return new ArrayList<>();
        }
    }



    /**
     * 查询订单明细(批量)
     *
     * @param orderNos “P1439635871018071”，“P1439635928276329”
     * @return
     */
    public List<OrderCostDetailInfo> getOrdersCostDetailInfo(String orderNos) {
        if (StringUtils.isBlank(orderNos))
            return null;
        String orderInfo = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.GET, String.format(ORDERCOST_SERVICE_API_BASE_URL + "/orderCostdetail/%s", orderNos), null, null, "UTF-8");
        if (StringUtils.isBlank(orderInfo) || orderInfo.equals("true\r\n")) {
            logger.error("查询/getOrderCostDetailInfo，入参为：" + orderNos + "，返回空");
            return null;
        }
        RPCResponse orderResponse = RPCResponse.parse(orderInfo);
        if (null == orderResponse || orderResponse.getCode() != 0 || orderResponse.getData() == null) {
            logger.info("查询订单明细，入参订单号：" + orderNos);
            return null;
        }
        return JSON.parseArray(JSON.toJSONString(orderResponse.getData()), OrderCostDetailInfo.class);
    }

    /**
     * 查询订单明细(单个)
     *
     * @param orderNo P1439635871018071
     * @return
     */
    public OrderCostDetailInfo getOrderCostDetailInfo(String orderNo) {
        List<OrderCostDetailInfo> list = getOrdersCostDetailInfo(orderNo);
        if (null != list && list.size() > 0)
            return list.get(0);
        return null;
    }

}
