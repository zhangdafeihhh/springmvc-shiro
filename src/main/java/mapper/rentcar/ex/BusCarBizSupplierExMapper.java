package mapper.rentcar.ex;

import java.util.List;

import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
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

	/**
	 * @Title: querySupplierExportList
	 * @Description: 查询巴士供应商导出列表
	 * @param queryDTO
	 * @return 
	 * @return List<BusSupplierExportVO>
	 * @throws
	 */
	List<BusSupplierExportVO> querySupplierExportList(BusSupplierQueryDTO queryDTO);

	/**
	 * @Title: selectBusSupplierById
	 * @Description: 查询巴士供应商基础信息
	 * @param supplierId
	 * @return 
	 * @return BusSupplierInfoVO
	 * @throws
	 */
	BusSupplierInfoVO selectBusSupplierById(Integer supplierId);

}