package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.request.DutyParamRequest;

import java.util.List;
import java.util.Map;

public interface CarDriverDayDutyExMapper {

    List<CarDriverDayDutyDTO> selectForList(DutyParamRequest dutyParamRequest);

    List<CarDriverDayDutyDTO> queryForList(DutyParamRequest dutyParamRequest);

    Integer getUnIssueCount(DutyParamRequest dutyParamRequest);


    Integer updateDriverDayDutyList(Map<String, Object> params);

    Integer updateDriverDayDutyOne(CarDriverDayDutyDTO dayDutyDTO);

    /** 保存司机排班信息--LN*/
    Integer insertDriverDayDutyList(Map<String, Object> params);

    List<CarDriverDayDutyDTO> selectForListV2(DutyParamRequest dutyParamRequest);

}