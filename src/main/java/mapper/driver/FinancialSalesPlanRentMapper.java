package mapper.driver;

import com.zhuanche.entity.driver.FinancialSalesPlanRent;

public interface FinancialSalesPlanRentMapper {
    int deleteByPrimaryKey(Integer salesId);

    int insert(FinancialSalesPlanRent record);

    int insertSelective(FinancialSalesPlanRent record);

    FinancialSalesPlanRent selectByPrimaryKey(Integer salesId);

    int updateByPrimaryKeySelective(FinancialSalesPlanRent record);

    int updateByPrimaryKey(FinancialSalesPlanRent record);
}