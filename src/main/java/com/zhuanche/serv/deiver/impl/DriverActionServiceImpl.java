package com.zhuanche.serv.deiver.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zhuanche.common.enums.DriverActionEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.entity.driver.DriverActionDto;
import com.zhuanche.entity.driver.DriverActionVO;
import com.zhuanche.exception.PermissionException;
import com.zhuanche.serv.deiver.DriverActionService;
import com.zhuanche.serv.order.OrderService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.driverOrderRecord.ex.DriverActionDtoExMapper;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriverActionServiceImpl implements DriverActionService {


    private Logger logger = LoggerFactory.getLogger(DriverActionServiceImpl.class);

    @Resource
    private DriverActionDtoExMapper actionDtoExMapper;

    @Resource
    private OrderService orderService;

    @Resource
    private CarBizDriverInfoExMapper driverInfoExMapper;

    @Resource
    private CarRelateTeamExMapper carRelateTeamExMapper;


    @Override
    public PageDTO getActionList(DriverActionVO driverActionVO, String table, String orderNo,
                                 int pageNum, int pageSize) {
        if (StringUtils.isNotBlank(orderNo)) {
            JSONObject result = orderService.getOrderInfoByParams(orderNo, "driver_id", Constants.ORDER_INFO_TAG);
            if (result != null && Constants.SUCCESS_CODE == result.getInteger(Constants.CODE)) {
                JSONArray jsonArray = result.getJSONArray(Constants.DATA);
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    Integer driverId = jsonArray.getJSONObject(0).getInteger("driverId");
                    if (driverId != null) {
                        driverActionVO.setDriverId(driverId);
                    } else if (StringUtils.isEmpty(driverActionVO.getDriverName())
                            && StringUtils.isEmpty(driverActionVO.getDriverLicense())
                            && StringUtils.isEmpty(driverActionVO.getDriverPhone())) {
                        return new PageDTO(pageNum, pageSize, 0, Collections.emptyList());
                    }
                }
            }
        }
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String, Object> params = generateParamsMap(driverActionVO, loginUser);
        CarBizDriverInfoDTO driverInfoDTO = driverInfoExMapper.queryDriverIdByActionVO(params);
        if (driverInfoDTO.getDriverId() != null) {
            Integer teamId = carRelateTeamExMapper.getTeamIdByDriverId(driverInfoDTO.getDriverId());
            if (teamId != null) {
                Set<Integer> teamIds = loginUser.getTeamIds();
                if (teamIds != null && !teamIds.isEmpty() && !teamIds.contains(teamId)) {
                    throw new PermissionException("查询权限不足");
                }
            }
            params.clear();
            params.put("driverId", driverInfoDTO.getDriverId());
            params.put("orderNo", orderNo);
            params.put("actionId", driverActionVO.getActionId());
            params.put("tableName", table);
        } else {
            throw new PermissionException("司机信息不存在");
        }
        List<DriverActionVO> list;
        int total;
        Page p = PageHelper.startPage(pageNum, pageSize, true);
        try {
            list = transferDataType(actionDtoExMapper.queryActionList(params), driverInfoDTO);
            total = (int) p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        return new PageDTO(pageNum, pageSize, total, list);
    }

    private List<DriverActionVO> transferDataType(List<DriverActionDto> dataList, CarBizDriverInfoDTO driverInfoDTO) {
        return dataList.stream().map(driverActionDto -> {
            DriverActionVO vo = new DriverActionVO();
            BeanUtils.copyProperties(driverActionDto, vo);
            vo.setActionName(DriverActionEnum.getActionNameById(driverActionDto.getAction()));
            vo.setActionId(driverActionDto.getAction());
            vo.setDriverId(driverInfoDTO.getDriverId());
            vo.setDriverLicense(driverInfoDTO.getLicensePlates());
            vo.setDriverPhone(driverInfoDTO.getPhone());
            vo.setDriverName(driverInfoDTO.getName());
            return vo;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> generateParamsMap(DriverActionVO vo, SSOLoginUser loginUser) {
        Map<String, Object> map = new HashMap<>(8);
        if (StringUtils.isNotBlank(vo.getDriverName())) {
            map.put("driverName", vo.getDriverName());
        }
        if (StringUtils.isNotBlank(vo.getDriverPhone())) {
            map.put("driverPhone", vo.getDriverPhone());
        }
        if (StringUtils.isNotBlank(vo.getDriverLicense())) {
            map.put("driverLicense", vo.getDriverLicense());
        }
        if (vo.getDriverId() != null) {
            map.put("driverId", vo.getDriverId());
        }
        if (loginUser.getSupplierIds() != null && !loginUser.getSupplierIds().isEmpty()) {
            map.put("supplierIds", loginUser.getSupplierIds());
        }
        if (loginUser.getCityIds() != null && !loginUser.getCityIds().isEmpty()) {
            map.put("cities", loginUser.getCityIds());
        }
        return map;
    }
}
