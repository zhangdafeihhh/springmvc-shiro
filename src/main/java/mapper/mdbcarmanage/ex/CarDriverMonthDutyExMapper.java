package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.driverDuty.CarDriverMonthDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverMonthDuty;
import com.zhuanche.request.DriverMonthDutyRequest;

import java.util.List;
import java.util.Map;

public interface CarDriverMonthDutyExMapper {

    /** 查询月排班列表*/
    List<CarDriverMonthDTO> queryDriverDutyList(DriverMonthDutyRequest param);

    /** 更新排班信息*/
    int updateDriverMonthDutyData(CarDriverMonthDuty record);

    /** 批量插入*/
    int saveDriverMonthDutyList(Map<String, Object> params);

    /** 批量修改*/
    int updateDriverMonthDutyList(Map<String, Object> params);



}