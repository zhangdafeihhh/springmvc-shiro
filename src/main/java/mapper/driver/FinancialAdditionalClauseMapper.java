package mapper.driver;

import com.zhuanche.entity.driver.FinancialAdditionalClause;

public interface FinancialAdditionalClauseMapper {
    int deleteByPrimaryKey(Integer clauseId);

    int insert(FinancialAdditionalClause record);

    int insertSelective(FinancialAdditionalClause record);

    FinancialAdditionalClause selectByPrimaryKey(Integer clauseId);

    int updateByPrimaryKeySelective(FinancialAdditionalClause record);

    int updateByPrimaryKey(FinancialAdditionalClause record);
}