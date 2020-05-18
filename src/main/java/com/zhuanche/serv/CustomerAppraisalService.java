package com.zhuanche.serv;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.le.config.dict.Dicts;
import com.sq.component.utils.CollectionUtils;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.entity.driver.CustomerAppraisal;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.util.MobileOverlayUtil;
import mapper.driver.CustomerAppraisalMapper;
import mapper.mdbcarmanage.ex.CarDriverTeamExMapper;
import mapper.rentcar.ex.CarBizCustomerAppraisalExMapper;
import mapper.rentcar.ex.CarBizCustomerAppraisalStatisticsExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author kjeakiry
 */
@Service
public class CustomerAppraisalService {

    private static final Logger log =  LoggerFactory.getLogger(CustomerAppraisalService.class);

    @Autowired
    private CarBizCustomerAppraisalExMapper carBizCustomerAppraisalExMapper;

    @Autowired
    private CarBizCustomerAppraisalStatisticsExMapper carBizCustomerAppraisalStatisticsExMapper;

    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;
    @Autowired
    private CarDriverTeamExMapper carDriverTeamExMapper;
    @Autowired
    private CustomerAppraisalMapper customerAppraisalMapper;

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<CarBizCustomerAppraisalDTO> queryCustomerAppraisalList(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {

        //乘客评价司机迁移配置中心 开关控制 customer_appraisal
        String appraisalStatus = Dicts.getString("customer_appraisal", "true");
        if ("true".equals(appraisalStatus)){
            //司机评价迁移 查询mp-driver库
            try{
                List<CustomerAppraisal> list = customerAppraisalMapper.queryCustomerAppraisalList(carBizCustomerAppraisalDTO);
                return turnAppraisalDTO(list);
            }catch (Exception e){
                log.error("CustomerAppraisalService.queryCustomerAppraisalList异常",e);
                return null;
            }

        }

        return carBizCustomerAppraisalExMapper.queryCustomerAppraisalList(carBizCustomerAppraisalDTO);
    }

    /**
     * 查询订单评分
     * @param orderNo
     * @return
     */
    public CarBizCustomerAppraisalDTO queryForObject(String orderNo) {
        CarBizCustomerAppraisalDTO param = new CarBizCustomerAppraisalDTO();
        param.setOrderNo(orderNo);
        List<CarBizCustomerAppraisalDTO> list = this.queryCustomerAppraisalList(param);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 转换实体类
     */
    public List<CarBizCustomerAppraisalDTO> turnAppraisalDTO(List<CustomerAppraisal> customerAppraisalList){

        if(CollectionUtils.isEmpty(customerAppraisalList)){
            return Lists.newArrayList();
        }

        List<CarBizCustomerAppraisalDTO> list = Lists.newArrayListWithCapacity(customerAppraisalList.size());

        customerAppraisalList.forEach(x -> {
            CarBizCustomerAppraisalDTO dto = new CarBizCustomerAppraisalDTO();
            dto.setOrderNo(x.getOrderNo());
            dto.setCreateDate(x.getCreateAt());
            dto.setInstrumentAndService(x.getInstrumentAndService());
            dto.setEnvironmentAndEquipped(x.getEnvironmentAndEquipped());
            dto.setEfficiencyAndSafety(x.getEfficiencyAndSafety());
            dto.setEvaluateScore(x.getEvaluateScore());
            dto.setEvaluate(x.getEvaluate());
            dto.setMemo(x.getMemo());
            dto.setDriverId(x.getDriverId());
            dto.setDriverName(x.getDriverName());
            dto.setDriverPhone(x.getDriverPhone());
            dto.setLicensePlates(x.getLicensePlates());

            list.add(dto);
        });

        return list;
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
        //乘客评价司机迁移配置中心 开关控制 customer_appraisal
        String appraisalStatus = Dicts.getString("customer_appraisal", "true");
        if ("true".equals(appraisalStatus)){
            return customerAppraisalMapper.queryDriverAppraisalDetail(carBizCustomerAppraisalDTO);
        }

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
        Map<String, CarBizDriverInfoDTO> cacheItem = Maps.newHashMapWithExpectedSize(driverInfoDTOList.size());

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
            List<Integer> ids = new ArrayList<>();
            for(CarBizCustomerAppraisalStatisticsDTO item : list){
                ids.add(item.getDriverId());
                driverInfotemp = cacheItem.get("d_"+item.getDriverId());
                if(driverInfotemp != null){
                    item.setIdCardNo(driverInfotemp.getIdCardNo());
                    item.setCityId(driverInfotemp.getServiceCity());
                }
                item.setDriverPhone(MobileOverlayUtil.doOverlayPhone(item.getDriverPhone()));
            }
            List<CarRelateTeam> relateTeams = carDriverTeamExMapper.queryDriverTeamListByDriverIds(ids);
            relateTeams.forEach( carRelateTeam -> {
                list.forEach( appraisalStatisticsDTO-> {
                    if (appraisalStatisticsDTO.getDriverId().equals(carRelateTeam.getDriverId())){
                        appraisalStatisticsDTO.setTeamId(carRelateTeam.getTeamId());
                        appraisalStatisticsDTO.setTeamName(carRelateTeam.getTeamName());
                        appraisalStatisticsDTO.setTeamGroupId(StringUtils.isNotBlank(carRelateTeam.getGroupId()) ? Integer.valueOf(carRelateTeam.getGroupId()): null );
                        appraisalStatisticsDTO.setTeamGroupName(carRelateTeam.getGroupName());
                    }
                });
            });
        }
        return pageInfo;
    }
}
