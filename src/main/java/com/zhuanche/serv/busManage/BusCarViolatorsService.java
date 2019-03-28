package com.zhuanche.serv.busManage;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.BusDriverViolatorsQueryDTO;
import com.zhuanche.dto.busManage.BusDriverViolatorsSaveDTO;
import com.zhuanche.entity.mdbcarmanage.BusBizDriverViolators;
import com.zhuanche.util.DateUtils;
import com.zhuanche.vo.busManage.BusBizDriverViolatorsVO;
import com.zhuanche.vo.busManage.BusDriverDetailInfoVO;
import mapper.mdbcarmanage.BusBizDriverViolatorsMapper;
import mapper.mdbcarmanage.ex.*;
import mapper.rentcar.ex.BusCarBizCustomerAppraisalStatisticsExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @ClassName: BusCarViolatorsService
 * @Description:
 * @author: tianye
 * @date: 2019年03月25日 下午16:50:23
 */

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BusCarViolatorsService implements BusConst {

    private static final Logger logger = LoggerFactory.getLogger(BusCarViolatorsService.class);

    @Autowired
    private BusBizDriverViolatorsExMapper busBizDriverViolatorsExMapper;

    @Autowired
    private BusBizDriverViolatorsMapper busBizDriverViolatorsMapper;

    @Autowired
    private BusCarBizCustomerAppraisalStatisticsExMapper appraisalStatisticsExMapper;


    /**
     * @param queryDTO
     * @return List<BusBizDriverViolators>
     * @throws
     * @Title: queryDriverList
     * @Description: 分页查询违规司机处理列表
     *
     */
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE))
    public List<BusBizDriverViolatorsVO> queryDriverViolatorsList(BusDriverViolatorsQueryDTO queryDTO) {
        return busBizDriverViolatorsExMapper.selectDriverViolatorsByQueryDTO(queryDTO);
    }

    public boolean insertDriverViolator(BusDriverViolatorsSaveDTO saveDTO, BusDriverDetailInfoVO driverInfo){
        try{
            BusBizDriverViolators violator=convertViolatorsBean(saveDTO,driverInfo);
            DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DynamicRoutingDataSource.DataSourceMode.MASTER);
            Integer count=busBizDriverViolatorsMapper.insertSelective(violator);
            return count==1;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("【新增违规巴士司机】程序异常",e);
            return false;
        }
    }

    private BusBizDriverViolators convertViolatorsBean(BusDriverViolatorsSaveDTO saveDTO, BusDriverDetailInfoVO driverInfo){
        BusBizDriverViolators violator=new BusBizDriverViolators();
        Date petDate=DateUtils.afterNHoursDate(saveDTO.getPunishStartTime(),saveDTO.getPunishDuration());
        violator.setBusDriverId(driverInfo.getDriverId());
        violator.setBusDriverName(driverInfo.getName());
        violator.setBusDriverPhone(saveDTO.getBusDriverPhone());
        violator.setCityId(driverInfo.getCityId());
        violator.setCityName(driverInfo.getCityName());
        violator.setSupplierId(driverInfo.getSupplierId());
        violator.setSupplierName(driverInfo.getSupplierName());
        violator.setGroupId(driverInfo.getGroupId());
        violator.setGroupName(driverInfo.getGroupName());
        Double evaluateScore=appraisalStatisticsExMapper.queryAvgAppraisal(driverInfo.getDriverId());
        violator.setEvaluateScore(String.valueOf(evaluateScore==null?0:evaluateScore));
        violator.setIdNumber(driverInfo.getIdCardNo());
        violator.setPunishStatus(saveDTO.getPunishStatus()==null?(short)1:Short.parseShort(String.valueOf(saveDTO.getPunishStatus())));//默认1停运，暂不支持冻结
        violator.setPunishReason(saveDTO.getPunishReason());
        violator.setPunishDuration(saveDTO.getPunishDuration());
        violator.setPunishStartTime(saveDTO.getPunishStartTime());
        violator.setPunishEndTime(petDate);
        return violator;
    }

}
