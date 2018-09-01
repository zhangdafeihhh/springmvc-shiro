package mapper.rentcar.ex;

import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.DutyParamRequest;

import java.util.List;

public interface CarBizDriverInfoExMapper {

    List<CarDriverInfoDTO> selectDriverList(DriverTeamRequest driverTeamRequest);

    List<CarDriverInfoDTO> queryListByLimits(DriverTeamRequest driverTeamRequest);

    CarDriverInfoDTO queryOneDriver(DutyParamRequest dutyParamRequest);
}