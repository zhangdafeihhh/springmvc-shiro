package mapper.driver.ex;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.financial.FinancialClueGoodsDTO;

public interface FinancialClueGoodsExMapper {

	FinancialClueGoodsDTO queryfinancialClueGoodsForObject(@Param(value = "clueId")Integer clueId);
   
}