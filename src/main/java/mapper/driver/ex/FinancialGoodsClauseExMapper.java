package mapper.driver.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.driver.FinancialGoodsClause;

public interface FinancialGoodsClauseExMapper {

	int updateFinancialGoodsByStatus(@Param(value = "goodsId") Integer goodsId
			,@Param(value = "status") Byte status);

	List<FinancialGoodsClause> queryFinancialGoodsClauseListForGoodsId(@Param(value = "goodsId")Integer goodsId);

	int deleteFinancialGoodsClause(@Param(value = "goodsId")Integer goodsId);
	
  
}