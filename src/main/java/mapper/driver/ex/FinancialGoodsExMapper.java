package mapper.driver.ex;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.financial.FinancialGoodsDTO;

public interface FinancialGoodsExMapper {

	List<FinancialGoodsDTO> queryFinancialGoodsForList(@Param(value = "goodsName") String goodsName,@Param(value = "basicsVehiclesId") Integer basicsVehiclesId,@Param(value = "salesTarget") Byte salesTarget,
			@Param(value = "supplierIds")Set<Integer> supplierIds,@Param(value = "cityIds") Set<Integer> cityIds,@Param(value = "status") Byte status);
   
}