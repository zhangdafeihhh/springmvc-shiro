package com.zhuanche.serv.system;

import com.zhuanche.common.web.AjaxResponse;

import java.util.Date;

public interface OperationLogService {
    AjaxResponse getUserDailyOperationCount(Date startDate, Date endDate);

    AjaxResponse getMenuOperationCount(Date startDate, Date endDate);

    AjaxResponse getUserOperationCount(Date startDate, Date endDate);
}
