package mapper.driver.ex;

import org.apache.ibatis.annotations.Param;

public interface FinancialGoodsClauseExMapper {

	int updateFinancialGoodsByStatus(@Param(value = "goodsId") Integer goodsId
			,@Param(value = "status") Byte status);
	
  
}