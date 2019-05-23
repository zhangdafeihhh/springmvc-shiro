package mapper.driver.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.driver.FinancialAdditionalClause;

public interface FinancialAdditionalClauseExMapper {

	int deleteByPrimaryKeyS(@Param(value = "clauseIds") List<Integer> clauseIds);

	List<FinancialAdditionalClause> queryFinancialAdditionalClause(@Param(value = "clauseIds")List<Integer> clauseIds);
	
}