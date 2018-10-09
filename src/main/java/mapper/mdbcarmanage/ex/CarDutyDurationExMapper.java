package mapper.mdbcarmanage.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.driverDuty.CarDriverDurationDTO;
import com.zhuanche.entity.mdbcarmanage.CarDutyDuration;
import com.zhuanche.request.DutyParamRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarDutyDurationExMapper {

    /** 获取排班时长设置列表*/
    List<CarDriverDurationDTO> selectDutyDurationList(DutyParamRequest dutyParamRequest);

    /** 获取排班时长时间段*/
    List<CarDriverDurationDTO> queryDutyDurationListByField(DutyParamRequest dutyParamRequest);

    CarDriverDurationDTO selectOne(@Param("paramId") Integer paramId);
}