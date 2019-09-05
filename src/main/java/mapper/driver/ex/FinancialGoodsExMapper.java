package mapper.driver.ex;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.dto.financial.FinancialGoodsDTO;
import com.zhuanche.entity.driver.FinancialGoods;

public interface FinancialGoodsExMapper {

	List<FinancialGoodsDTO> queryFinancialGoodsForList(@Param(value = "goodsName") String goodsName,@Param(value = "basicsVehiclesId") Integer basicsVehiclesId,@Param(value = "salesTarget") Byte salesTarget,
			@Param(value = "supplierIds")Set<Integer> supplierIds,@Param(value = "cityIds") Set<Integer> cityIds,@Param(value = "status") Byte status,@Param(value = "goodsType") Byte goodsType);

	FinancialGoods queryFinancialGoodsForObject(@Param(value = "basicsVehiclesId")Integer basicsVehiclesId,@Param(value = "cityId")Integer cityId,@Param(value = "supplierId")Integer supplierId);

	FinancialGoods queryFinancialGoodsByName(@Param(value = "goodsName")String goodsName);
   
}