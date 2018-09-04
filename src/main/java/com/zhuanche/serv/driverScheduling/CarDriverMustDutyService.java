package com.zhuanche.serv.driverScheduling;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.dutyEnum.EnumDriverDutyTimeFlag;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDutyTimeInfo;
import com.zhuanche.request.DutyParamRequest;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.util.Check;
import mapper.mdbcarmanage.ex.CarDriverDayDutyExMapper;
import mapper.mdbcarmanage.ex.DriverDutyTimeInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 车队设置
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author lunan
 * @version 1.0
 * @since 1.0
 * @create: 2018-09-03 12:54
 *
 */
@Service
public class CarDriverMustDutyService {

	private static final Logger logger = LoggerFactory.getLogger(CarDriverMustDutyService.class);





}