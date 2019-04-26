package mapper.driver.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.financial.FinancialGoodsDTO;

public interface FinancialGoodsExMapper {

	List<FinancialGoodsDTO> queryFinancialGoodsForList(@Param(value = "goodsName") String goodsName,@Param(value = "basicsVehiclesId") Integer basicsVehiclesId,@Param(value = "salesTarget") Byte salesTarget,
			@Param(value = "supplierId")Integer supplierId,@Param(value = "cityId") Integer cityId,@Param(value = "status") Byte status);
   
}