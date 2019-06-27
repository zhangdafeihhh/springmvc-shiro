package com.zhuanche.serv.deiver;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.driver.DriverAppraisalAppeal;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.vo.driver.DriverAppealDetailVO;
import mapper.driver.DriverAppraisalAppealMapper;
import mapper.driver.ex.DriverAppraisalAppealExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2019-04-18 15:38
 **/
@Service
public class DriverAppraisalAppealService {
    private static final Logger logger = LoggerFactory.getLogger(DriverAppraisalAppealService.class);

    @Autowired
    private DriverAppraisalAppealExMapper appealExMapper;
    @Autowired
    private DriverAppraisalAppealMapper appraisalAppealMapper;

    /**
     * 根据评分表ID 批量查询申诉信息
     * @param appraisalIds
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<DriverAppraisalAppeal> queryBaseInfoByAppraisalIds(List<Integer> appraisalIds){
        try {
            List<DriverAppraisalAppeal> appeals = appealExMapper.queryBaseInfoByAppraisalIds(appraisalIds);
            return appeals;
        } catch (Exception e) {
            logger.error("根据评分表ID 批量查询申诉信息异常：e:{}",e);
            return null;
        }
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public int saveSelective(DriverAppraisalAppeal appeal){
        int result=0;
        try {
            result= appraisalAppealMapper.insertSelective(appeal);
        } catch (Exception e) {
            logger.error("保存申诉信息异常，e:{}",e);
        }
        return result;
    }
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public int revokeAppeal(Integer appealId){
        int result = 0;
        try {
            DriverAppraisalAppeal appeal = new DriverAppraisalAppeal();
            //表示撤销状态
            appeal.setAppealStatus(4);
            appeal.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());
            appeal.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
            appeal.setUpdateTime(new Date());
            appeal.setId(appealId);
            result = appraisalAppealMapper.updateByPrimaryKeySelective(appeal);
        } catch (Exception e) {
            logger.error("撤回申诉信息异常，e:{}",e);
        }
        return result;
    }
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public Integer getAppealStatus(Integer appealId){
        Integer appealStatus=null;
        try {
           appealStatus = appealExMapper.getAppealStatus(appealId);
        } catch (Exception e) {
            logger.error("查询审核状态异常，e:{}",e);
        }
        return appealStatus;
    }
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public DriverAppraisalAppeal getAppealStatusByAppraisalId(Integer appraisalId){
       return appealExMapper.getAppealStatusByAppraisalId(appraisalId);
    }

    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public int updateSelective(DriverAppraisalAppeal appraisalAppeal){
        int result=0;
        try {
            result =appraisalAppealMapper.updateByPrimaryKeySelective(appraisalAppeal);
        } catch (Exception e) {
            logger.error("修改司机申诉记录异常,e:{}",e);
        }
        return result;
    }
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public Set<Integer> getAppraissalIdsByAppealStatus(Integer appealStatus , Integer callbackStatus){
        Set<Integer> result=null;
        try {
            result = appealExMapper.getAppraissalIdsByAppealStatus(appealStatus , callbackStatus);
        } catch (Exception e) {
           logger.error("根据审核状态查询评分ID异常，e:{}",e);
        }
        return result;
    }
}
