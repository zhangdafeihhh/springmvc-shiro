package mapper.driver.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.financial.FinancialBasicsVehiclesDTO;

public interface FinancialBasicsVehiclesExMapper {

	List<FinancialBasicsVehiclesDTO> queryFinancialBasicsVehiclesForList(@Param("vehiclesDetailedName") String vehiclesDetailedName,
			@Param("energyType")Integer energyType);
}