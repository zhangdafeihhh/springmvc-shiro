package mapper.rentcar.ex;

import java.util.List;

import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

public interface BusCarBizSupplierExMapper{

	int insertSelective(BusSupplierBaseDTO baseDTO);
	
	int updateByPrimaryKeySelective(BusSupplierBaseDTO baseDTO);

	/**
	 * @Title: querySupplierContractExpireSoonList
	 * @Description: 查询巴士供应商
	 * @param queryDTO
	 * @return 
	 * @return List<BusSupplierPageVO>
	 * @throws
	 */
	List<BusSupplierPageVO> querySupplierPageListByMaster(BusSupplierQueryDTO queryDTO);

}