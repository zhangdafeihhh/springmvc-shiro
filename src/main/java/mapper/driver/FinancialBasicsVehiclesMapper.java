package mapper.driver;

import com.zhuanche.entity.driver.FinancialBasicsVehicles;

public interface FinancialBasicsVehiclesMapper {
    int deleteByPrimaryKey(Integer basicsVehiclesId);

    int insert(FinancialBasicsVehicles record);

    int insertSelective(FinancialBasicsVehicles record);

    FinancialBasicsVehicles selectByPrimaryKey(Integer basicsVehiclesId);

    int updateByPrimaryKeySelective(FinancialBasicsVehicles record);

    int updateByPrimaryKey(FinancialBasicsVehicles record);
}