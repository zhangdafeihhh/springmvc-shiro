package mapper.driver;

import com.zhuanche.dto.driver.DriverVoEntity;

import java.util.List;

public interface DriverMapper {

    int selectDriverByKeyCountAddCooperation(DriverVoEntity params);

    public List<DriverVoEntity> selectDriverByKeyAddCooperation(DriverVoEntity params);
}
