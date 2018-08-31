package mapper.rentcar.ex;

import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.request.DutyRequest;

import java.util.List;

public interface CarBizDriverInfoExMapper {

    List<CarDriverInfoDTO> selectDriverList(DutyRequest dutyRequest);

    List<CarDriverInfoDTO> queryListByLimits(DutyRequest dutyRequest);

}