package mapper.driver.ex;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.financial.FinancialClueDTO;

public interface FinancialClueExMapper {

	List<FinancialClueDTO> queryfinancialClueForList(@Param(value = "purposeName") String purposeName,@Param(value = "goodsId") Integer goodsId,
			@Param(value = "startDate")String startDate,@Param(value = "endDate")String endDate,@Param(value = "supplierIds")Set<Integer> supplierIds,
			@Param(value = "cityIds")Set<Integer> cityIds,@Param(value = "status") Byte status,@Param(value = "goodsType") Byte goodsType);
	
}