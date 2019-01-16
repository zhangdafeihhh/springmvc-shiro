package com.zhuanche.serv;


import com.alibaba.fastjson.JSON;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;

import com.zhuanche.dto.CarDriverInfoDTO;

import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalExtDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;

import com.zhuanche.request.DutyParamRequest;

import com.zhuanche.serv.driverteam.CarDriverTeamService;
import mapper.rentcar.ex.CarBizCustomerAppraisalExMapper;
import mapper.rentcar.ex.CarBizCustomerAppraisalStatisticsExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;

import org.apache.commons.lang3.StringUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.*;

@Service
public class CustomerAppraisalService {

    private static final Logger log =  LoggerFactory.getLogger(CustomerAppraisalService.class);

    @Autowired
    private CarBizCustomerAppraisalExMapper carBizCustomerAppraisalExMapper;

    @Autowired
    private CarBizCustomerAppraisalStatisticsExMapper carBizCustomerAppraisalStatisticsExMapper;

    @Autowired
    private CarDriverTeamService carDriverTeamService;
    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<CarBizCustomerAppraisalDTO> queryCustomerAppraisalList(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {
        return carBizCustomerAppraisalExMapper.queryCustomerAppraisalList(carBizCustomerAppraisalDTO);
    }

    /**
     * 查询司机评分
     * @param carBizCustomerAppraisalStatisticsDTO
     * @return
     */
    @Deprecated
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<CarBizCustomerAppraisalStatisticsDTO> queryCustomerAppraisalStatisticsList(CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO) {
        return carBizCustomerAppraisalStatisticsExMapper.queryCustomerAppraisalStatisticsList(carBizCustomerAppraisalStatisticsDTO);
    }

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    public List<CarBizCustomerAppraisalDTO> queryDriverAppraisalDetail(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {
        return carBizCustomerAppraisalExMapper.queryDriverAppraisalDetail(carBizCustomerAppraisalDTO);
    }

    public static String pingDriverIds(List<CarBizCustomerAppraisalStatisticsDTO> list) {
        String driverId = "";
        if(list!=null&&list.size()>0){
            int j=0;
            for(int i=0;i<list.size();i++){
                if(!"".equals(list.get(i))&&list.get(i)!=null&&!"".equals(list.get(i).getDriverId())&&list.get(i).getDriverId()!=null){
                    if(j==0){
                        driverId = "'"+list.get(i).getDriverId()+"'";
                    }else{
                        driverId +=",'"+list.get(i).getDriverId()+"'";
                    }
                    j++	;
                }
            }
        }
        return driverId;
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public PageInfo<CarBizCustomerAppraisalStatisticsDTO> queryCustomerAppraisalStatisticsListV2(CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO
            , int pageNo, int pageSize) {

        //查询司机信息
        CarBizDriverInfoDTO carBizDriverInfoDTO = new CarBizDriverInfoDTO();

        carBizDriverInfoDTO.setPhone(carBizCustomerAppraisalStatisticsDTO.getDriverPhone());
        carBizDriverInfoDTO.setName(carBizCustomerAppraisalStatisticsDTO.getDriverName());

        carBizDriverInfoDTO.setCityIds(carBizCustomerAppraisalStatisticsDTO.getCityIds());
        carBizDriverInfoDTO.setServiceCity(carBizCustomerAppraisalStatisticsDTO.getCityId());
        carBizDriverInfoDTO.setSupplierIds(carBizCustomerAppraisalStatisticsDTO.getSupplierIds());
        carBizDriverInfoDTO.setSupplierId(carBizCustomerAppraisalStatisticsDTO.getSupplierId());
        carBizDriverInfoDTO.setDriverIds(carBizCustomerAppraisalStatisticsDTO.getDriverIds());
        List<CarBizDriverInfoDTO>  driverInfoDTOList = carBizDriverInfoExMapper.queryCarBizDriverList(carBizDriverInfoDTO);
        if(driverInfoDTOList == null || driverInfoDTOList.isEmpty()){
            return null;
        }
        Set<Integer> driverIdSet = new HashSet<>();
        Map<String,CarBizDriverInfoDTO> cacheItem = new HashMap<>();

        for(CarBizDriverInfoDTO item :driverInfoDTOList){
            driverIdSet.add(item.getDriverId());
            cacheItem.put("d_"+item.getDriverId(),item);
        }
        carBizCustomerAppraisalStatisticsDTO.setDriverIds(driverIdSet);
        PageHelper.startPage(pageNo, pageSize, true);
        List<CarBizCustomerAppraisalStatisticsDTO> list  = carBizCustomerAppraisalStatisticsExMapper.queryCustomerAppraisalStatisticsListV2(carBizCustomerAppraisalStatisticsDTO);
        PageInfo<CarBizCustomerAppraisalStatisticsDTO> pageInfo = new PageInfo<>(list);
        if(list != null && list.size() >= 1){
            CarBizDriverInfoDTO driverInfotemp = null;
            for(CarBizCustomerAppraisalStatisticsDTO item : list){
                driverInfotemp = cacheItem.get("d_"+item.getDriverId());
                if(driverInfotemp != null){
                    item.setIdCardNo(driverInfotemp.getIdCardNo());
                    item.setCityId(driverInfotemp.getServiceCity());
                }
            }
        }
        return pageInfo;
    }
}
