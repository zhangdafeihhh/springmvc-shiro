package com.zhuanche.serv.deiver.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.enums.DriverActionEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.driver.DriverTeamRelationEntity;
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
    @MasterSlaveConfigs(
            configs = {
                    @MasterSlaveConfig(databaseTag = "orderrecord-master-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
            }
    )
    public PageDTO getActionList(DriverActionVO driverActionVO, String table, String orderNo,
                                 int pageNum, int pageSize) {
        if (StringUtils.isNotBlank(orderNo)) {
            try {
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
            }catch (Exception e){
                logger.error("查询订单信息失败 orderNo {} ", orderNo);
                logger.error("异常信息 ：", e);
                return new PageDTO(pageNum, pageSize, 0, Collections.emptyList());
            }
        }
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String, Object> params = generateParamsMap(driverActionVO, loginUser);
        List<CarBizDriverInfoDTO> driverInfoDTOs = driverInfoExMapper.queryDriverIdsByActionVO(params);
        if (driverInfoDTOs != null && !driverInfoDTOs.isEmpty()) {
            if (driverInfoDTOs.size() == 1) {
                hasDataPermission(driverInfoDTOs.get(0), loginUser);
                params.clear();
                params.put("driverId", driverInfoDTOs.get(0).getDriverId());
                params.put("actionId", driverActionVO.getActionId());
                params.put("tableName", table);
                params.put("orderNo", orderNo);
            }else {
                params.clear();
                String driverIds = hasDataPermissions(driverInfoDTOs, loginUser);
                params.put("driverIds", driverIds);
                params.put("actionId", driverActionVO.getActionId());
                params.put("tableName", table);
                params.put("orderNo", orderNo);
            }
        } else {
            logger.info("司机信息查询失败,查询参数 {}", ((JSONObject)JSONObject.toJSON(params)).toJSONString());
            throw new PermissionException("司机信息不存在");
        }
        List<DriverActionVO> list;
        int total;
        Page p = PageHelper.startPage(pageNum, pageSize, true);
        try {
            list = transferDatasType(actionDtoExMapper.queryActionList(params), driverInfoDTOs);
            total = (int) p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        return new PageDTO(pageNum, pageSize, total, list);
    }

    @Override
    public List<DriverActionVO> queryTimeLine(DriverActionVO driverActionVO, String tableName) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String, Object> params = generateParamsMap(driverActionVO, loginUser);
        CarBizDriverInfoDTO driverInfoDTO = driverInfoExMapper.queryDriverIdByActionVO(params);
        if (driverInfoDTO != null && driverInfoDTO.getDriverId() != null) {
            hasDataPermission(driverInfoDTO, loginUser);
            return transferDataType(actionDtoExMapper.queryActionTimeLine(tableName, driverActionVO.getDriverId(), driverActionVO.getTime()), driverInfoDTO);
        } else {
            logger.info("司机信息查询失败,查询参数 {}", ((JSONObject)JSONObject.toJSON(params)).toJSONString());
            throw new PermissionException("司机信息不存在");
        }
    }

    private void hasDataPermission(CarBizDriverInfoDTO driverInfoDTO, SSOLoginUser loginUser){
        Integer teamId = carRelateTeamExMapper.getTeamIdByDriverId(driverInfoDTO.getDriverId());
        if (teamId != null) {
            Set<Integer> teamIds = loginUser.getTeamIds();
            if (teamIds != null && !teamIds.isEmpty() && !teamIds.contains(teamId)) {
                logger.error("查询权限不足 userId : {} , driverId : {}", loginUser.getId(), driverInfoDTO.getDriverId());
                throw new PermissionException("查询权限不足");
            }
        }
    }

    private String hasDataPermissions(List<CarBizDriverInfoDTO> driverInfoDTOS, SSOLoginUser loginUser){
        Set<String> driverIds = driverInfoDTOS.stream().filter(Objects::nonNull).map(elem -> elem.getDriverId().toString()).collect(Collectors.toSet());
        List<DriverTeamRelationEntity> relationEntityList = carRelateTeamExMapper.selectByDriverIdSet(driverIds);
        Set<Integer> teamIds = loginUser.getTeamIds();
        if (teamIds != null && !teamIds.isEmpty()){
            List<String> idList = relationEntityList.stream().filter(elem -> teamIds.contains(Integer.valueOf(elem.getTeamId()))).map(DriverTeamRelationEntity::getDriverId).collect(Collectors.toList());
            if(idList.isEmpty()){
                logger.error("查询权限不足 userId : {} , driverIds : {}", loginUser.getId(), String.join(Constants.SEPERATER, driverIds));
                throw new PermissionException("查询权限不足");
            }
            return String.join(Constants.SEPERATER, idList);
        }
        return String.join(Constants.SEPERATER,driverIds);
    }

    private List<DriverActionVO> transferDatasType(List<DriverActionDto> dataList, List<CarBizDriverInfoDTO> driverInfoDTO){
        Map<Integer, CarBizDriverInfoDTO> map = driverInfoDTO.stream().
                filter(Objects::nonNull).collect(Collectors.toMap(CarBizDriverInfoDTO::getDriverId, value -> value));
        return dataList.stream().filter(Objects::nonNull).map(
                elem -> {
                    DriverActionVO vo = generateVo(elem);
                    CarBizDriverInfoDTO driverInfo = map.get(elem.getDriverId());
                    if (driverInfo != null){
                        vo.setDriverLicense(driverInfo.getLicensePlates());
                        vo.setDriverPhone(driverInfo.getPhone());
                        vo.setDriverName(driverInfo.getName());
                    }
                    return vo;
                }
        ).collect(Collectors.toList());
    }

    private List<DriverActionVO> transferDataType(List<DriverActionDto> dataList, CarBizDriverInfoDTO driverInfoDTO) {
        return dataList.stream().map(driverActionDto -> {
            DriverActionVO vo = generateVo(driverActionDto);
            vo.setDriverLicense(driverInfoDTO.getLicensePlates());
            vo.setDriverPhone(driverInfoDTO.getPhone());
            vo.setDriverName(driverInfoDTO.getName());
            return vo;
        }).collect(Collectors.toList());
    }

    private DriverActionVO generateVo(DriverActionDto actionDto){
        DriverActionVO vo = new DriverActionVO();
        BeanUtils.copyProperties(actionDto, vo);
        vo.setActionName(DriverActionEnum.getActionNameById(actionDto.getAction()));
        vo.setActionId(actionDto.getAction());
        vo.setDriverId(actionDto.getDriverId());
        return vo;
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
