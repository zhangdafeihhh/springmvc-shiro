package com.zhuanche.serv.mdbcarmanage;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.CarBizDriverUpdate;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.mdbcarmanage.CarBizDriverUpdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CarBizDriverUpdateService {

    @Autowired
    private CarBizDriverUpdateMapper carBizDriverUpdateMapper;

	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER))
    public void insert (String origin, String updata, Integer driverId, Integer value) {
        try {
            CarBizDriverUpdate driverUpdate = new CarBizDriverUpdate();
            driverUpdate.setDriverid(driverId);
            // 获取当前操作人的ID
            driverUpdate.setCreateby(WebSessionUtil.getCurrentLoginUser().getId());
            driverUpdate.setIdentifier(value);
            driverUpdate.setOrigin(origin);
            driverUpdate.setUpdata(updata);
            driverUpdate.setCreatedate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            carBizDriverUpdateMapper.insert(driverUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
