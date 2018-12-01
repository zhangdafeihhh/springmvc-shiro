package mapper.rentcar.ex;

import com.zhuanche.dto.busManage.BusSupplierBaseDTO;

public interface BusCarBizSupplierExMapper{

	int insertSelective(BusSupplierBaseDTO baseDTO);
	
	int updateByPrimaryKeySelective(BusSupplierBaseDTO baseDTO);

}