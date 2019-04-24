package mapper.driver;

import com.zhuanche.entity.driver.FinancialClueGoodsClause;

public interface FinancialClueGoodsClauseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinancialClueGoodsClause record);

    int insertSelective(FinancialClueGoodsClause record);

    FinancialClueGoodsClause selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinancialClueGoodsClause record);

    int updateByPrimaryKey(FinancialClueGoodsClause record);
}