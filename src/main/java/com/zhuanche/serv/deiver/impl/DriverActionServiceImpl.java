package com.zhuanche.serv.deiver.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.enums.DriverActionEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.driver.DriverActionDto;
import com.zhuanche.entity.driver.DriverActionVO;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.deiver.DriverActionService;
import com.zhuanche.serv.order.OrderService;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.SignatureUtils;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.driver.ex.DriverActionDtoExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DriverActionServiceImpl implements DriverActionService {


    private Logger logger = LoggerFactory.getLogger(DriverActionServiceImpl.class);

    @Resource
    private DriverActionDtoExMapper actionDtoExMapper;

    @Resource
    private OrderService orderService;


    @Override
    public PageDTO getActionList(DriverActionVO driverActionVO, String table, String orderNo,
                                 int pageNum, int pageSize) {
        if (StringUtils.isNotBlank(orderNo)){
            JSONObject result = orderService.getOrderInfoByParams(orderNo, "driver_id", Constants.ORDER_INFO_TAG);
            if (result != null && Constants.SUCCESS_CODE == result.getInteger(Constants.CODE)){
                JSONArray jsonArray = result.getJSONArray(Constants.DATA);
                if (jsonArray != null && !jsonArray.isEmpty()){
                    Integer driverId = jsonArray.getJSONObject(0).getInteger("driver_id");
                    if(driverId != null){
                        driverActionVO.setDriverId(driverId);
                    }
                }
            }
        }

        List<DriverActionVO> list;
        int total;
        Page p = PageHelper.startPage(pageNum, pageSize, true);
        try {
            list = transferDataType(actionDtoExMapper.queryActionList(driverActionVO,table));
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        return new PageDTO(pageNum, pageSize, total, list);
    }

    private List<DriverActionVO> transferDataType(List<DriverActionDto> dataList){
        return dataList.stream().map( driverActionDto -> {
            DriverActionVO vo = new DriverActionVO();
            BeanUtils.copyProperties(driverActionDto, vo);
            vo.setActionName(DriverActionEnum.getActionNameById(driverActionDto.getAction()));
            return vo;
        }).collect(Collectors.toList());
    }
}
