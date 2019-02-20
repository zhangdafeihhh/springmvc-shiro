package com.zhuanche.serv.deiver;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.entity.driver.DriverActionVO;

public interface DriverActionService {
    PageDTO getActionList(DriverActionVO driverActionVO, String table, String orderNo, int pageNum, int pageSize);

}
