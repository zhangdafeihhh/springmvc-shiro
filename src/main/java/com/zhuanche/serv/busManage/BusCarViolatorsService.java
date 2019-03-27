package com.zhuanche.serv.busManage;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.BusDriverViolatorsQueryDTO;
import com.zhuanche.vo.busManage.BusBizDriverViolatorsVO;
import mapper.mdbcarmanage.ex.*;
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

    public boolean insertDriverViolator(){
        return false;
    }
}
