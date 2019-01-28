package mapper.rentcar.ex;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.util.objcompare.entity.supplier.BusSupplierBaseCO;
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

public interface BusCarBizSupplierExMapper {

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
	
	/**
	 * @Title: querySuppliers
	 * @Description: 查询供应商
	 * @param param
	 * @return 
	 * @return List<CarBizSupplier>
	 * @throws
	 */
	List<Map<Object, Object>> querySuppliers(Map<String, Object> param);

	/**
	 * 传入批量城市查询供应商ID
	 * @param cityIds
	 * @return
	 */

	List<Integer> querySupplierIdByCitys(Map<String,Set<Integer>> cityIds);

	/**
	 * 根据供应商Id查询供应商的基本信息
	 * @param supplierIds
	 * @return
	 */
	List<BusSupplierInfoVO> queryBasicInfoByIds(Map<String,Set<Integer>> supplierIds);

	/**
	 * @Title: querySupplierCoById
	 * @Description: 查询供应商比对信息
	 * @param supplierId
	 * @return BusSupplierBaseCO
	 * @throws
	 */
	BusSupplierBaseCO querySupplierCOById(Integer supplierId);

	/**
	 * @Title: checkIfExists
	 * @Description: 供应商是否存在
	 * @param params
	 * @return int
	 * @throws
	 */
	int checkIfExists(Map<String, Object> params);
}