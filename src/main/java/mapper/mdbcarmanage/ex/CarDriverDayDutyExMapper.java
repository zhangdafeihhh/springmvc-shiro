package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverDayDuty;
import com.zhuanche.request.DutyParamRequest;

import java.util.List;
import java.util.Map;

public interface CarDriverDayDutyExMapper {

    List<CarDriverDayDutyDTO> queryForList(DutyParamRequest dutyParamRequest);

    Integer getUnIssueCount(DutyParamRequest dutyParamRequest);

    Integer updateDriverDayDutyList(Map<String, Object> params);

}