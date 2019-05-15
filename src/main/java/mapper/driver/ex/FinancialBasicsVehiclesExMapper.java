package mapper.driver.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.financial.FinancialBasicsVehiclesDTO;
import com.zhuanche.entity.driver.FinancialBasicsVehicles;

public interface FinancialBasicsVehiclesExMapper {

	List<FinancialBasicsVehiclesDTO> queryFinancialBasicsVehiclesForList(@Param("vehiclesDetailedName") String vehiclesDetailedName,
			@Param("energyType")Integer energyType);

	List<FinancialBasicsVehicles> queryBasicsVehiclesAllList();

	FinancialBasicsVehicles queryFinancialBasicsVehiclesByName(@Param("vehiclesDetailedName")String vehiclesDetailedName);

	List<FinancialBasicsVehicles> queryFinancialBasicsVehiclesList(@Param("basicsVehiclesIds")List<Integer> basicsVehiclesIds);
}