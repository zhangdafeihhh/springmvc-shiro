package mapper.driver;

import com.zhuanche.entity.driver.FinancialSalesPlanFinancing;

public interface FinancialSalesPlanFinancingMapper {
    int deleteByPrimaryKey(Integer salesId);

    int insert(FinancialSalesPlanFinancing record);

    int insertSelective(FinancialSalesPlanFinancing record);

    FinancialSalesPlanFinancing selectByPrimaryKey(Integer salesId);

    int updateByPrimaryKeySelective(FinancialSalesPlanFinancing record);

    int updateByPrimaryKey(FinancialSalesPlanFinancing record);
}