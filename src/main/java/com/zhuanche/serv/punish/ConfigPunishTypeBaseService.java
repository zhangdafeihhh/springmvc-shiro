package com.zhuanche.serv.punish;

import com.zhuanche.entity.mpconfig.ConfigPunishTypeBaseEntity;
import mapper.mpconfig.ConfigPunishTypeBaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author kjeakiry
 */
@Service
public class ConfigPunishTypeBaseService {
    
    private static Logger logger = LoggerFactory.getLogger(ConfigPunishTypeBaseService.class);
    
    @Resource
    private ConfigPunishTypeBaseMapper configPunishTypeBaseMapper;


    public Boolean queryIsYwHandlePunishType(ConfigPunishTypeBaseEntity params){
        boolean had;
        ConfigPunishTypeBaseEntity configPunishTypeBaseEntity = configPunishTypeBaseMapper.queryPunishTypeSpecial(params);
        ConfigPunishTypeBaseEntity params2 = convertConfigPunishTypeBaseEntity(params);
        ConfigPunishTypeBaseEntity configPunishTypeBase = configPunishTypeBaseMapper.queryPunishTypeSpecial(params2);
        logger.info("params2="+params2);
        //不存在特殊处罚，查询普通处罚
        if(configPunishTypeBaseEntity==null && null == configPunishTypeBase){
            configPunishTypeBaseEntity = configPunishTypeBaseMapper.queryPunishTypeBase(params);
        }
        //不存在特殊处罚,也不存在普通处罚
        had = configPunishTypeBaseEntity != null;
        return had;
    }


    /**
     * 查询是否需要业务后台处理
     */
    public ConfigPunishTypeBaseEntity queryIsHandlePuinshType(ConfigPunishTypeBaseEntity params){
        ConfigPunishTypeBaseEntity configPunishTypeBaseEntity = configPunishTypeBaseMapper.queryPunishTypeSpecial(params);
    
        // 不存在特殊处罚，查询普通处罚
        if(configPunishTypeBaseEntity == null){
            ConfigPunishTypeBaseEntity params2 = convertConfigPunishTypeBaseEntity(params);
            configPunishTypeBaseEntity = configPunishTypeBaseMapper.queryPunishTypeSpecial(params2);
            if (null == configPunishTypeBaseEntity) {
                configPunishTypeBaseEntity = configPunishTypeBaseMapper.queryPunishTypeBase(params);
                if (null == configPunishTypeBaseEntity) {
                    configPunishTypeBaseEntity = configPunishTypeBaseMapper.queryPunishTypeBase(params2);
                }
            }
        }
        return configPunishTypeBaseEntity;
    }

    private ConfigPunishTypeBaseEntity convertConfigPunishTypeBaseEntity(ConfigPunishTypeBaseEntity params) {
        ConfigPunishTypeBaseEntity params2 = new ConfigPunishTypeBaseEntity();
        //处罚类型Id
        params2.setConfigid(params.getConfigid());
        params2.setRelatedBasePunishId(params.getRelatedBasePunishId());
        params2.setCooperationType((byte) params.getCooperationType().intValue());
        params2.setServCitys(String.valueOf(params.getServCitys()));
        params2.setDealBackground("0");
        return params2;
    }
}