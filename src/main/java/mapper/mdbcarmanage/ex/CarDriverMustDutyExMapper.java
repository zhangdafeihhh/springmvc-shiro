package mapper.mdbcarmanage.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.driverDuty.CarDriverMustDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMustDuty;
import com.zhuanche.request.DutyParamRequest;

import java.util.List;

public interface CarDriverMustDutyExMapper {
    /** 查询司机强制排班列表*/
    List<CarDriverMustDutyDTO> selectDriverMustDutyList(DutyParamRequest dutyParamRequest);

    /** 查询司机强制排班时间段信息*/
    List<CarDriverMustDutyDTO> selectDriverMustDutyListByField(DutyParamRequest dutyParamRequest);


    CarDriverMustDutyDTO selectDriverMustDutyDetail(DutyParamRequest dutyParamRequest);

}