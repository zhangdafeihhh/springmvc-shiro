package mapper.driver;

import com.zhuanche.entity.driver.FinancialGoodsClause;

public interface FinancialGoodsClauseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinancialGoodsClause record);

    int insertSelective(FinancialGoodsClause record);

    FinancialGoodsClause selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinancialGoodsClause record);

    int updateByPrimaryKey(FinancialGoodsClause record);
}