package mapper.driver.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.financial.FinancialClueDTO;

public interface FinancialClueExMapper {

	List<FinancialClueDTO> queryfinancialClueForList(@Param(value = "purposeName") String purposeName,@Param(value = "goodsId") Integer goodsId,
			@Param(value = "startDate")String startDate,@Param(value = "endDate")String endDate,@Param(value = "supplierId")Integer supplierId,
			@Param(value = "cityId")Integer cityId,@Param(value = "status") Byte status);
	
}