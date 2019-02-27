package com.zhuanche.serv.deiver;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.entity.driver.DriverActionVO;

import java.util.List;

public interface DriverActionService {
    PageDTO getActionList(DriverActionVO driverActionVO, String table, String orderNo, int pageNum, int pageSize);

    List<DriverActionVO> queryTimeLine(DriverActionVO driverActionVO, String tableName);
}
