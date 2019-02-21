package mapper.mdbcarmanage.ex;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;
import com.zhuanche.util.objcompare.entity.supplier.BusSupplierDetailCO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

public interface BusBizSupplierDetailExMapper {

	/**
	 * @Title: insertSelective
	 * @Description: 新增
	 * @param record
	 * @return 
	 * @return int
	 * @throws
	 */
	int insertSelective(BusSupplierDetailDTO record);

	/**
	 * @Title: updateBySupplierIdSelective
	 * @Description: 修改
	 * @param record
	 * @return 
	 * @return int
	 * @throws
	 */
	int updateBySupplierIdSelective(BusSupplierDetailDTO record);

	/**
	 * @Title: checkExist
	 * @Description: 校验是否存在
	 * @param supplierId
	 * @return 
	 * @return int
	 * @throws
	 */
	int checkExist(Integer supplierId);

	/**
	 * @Title: querySupplierList
	 * @Description: 查询巴士供应商分页信息
	 * @param param
	 * @return 
	 * @return List<BusBizSupplierDetail>
	 * @throws
	 */
	List<BusSupplierPageVO> querySupplierContractExpireSoonList(Map<Object, Object> param);

	/**
	 * @Title: selectBySupplierId
	 * @Description: 根据供应商ID查询巴士供应商其它信息
	 * @param supplierId
	 * @return 
	 * @return BusBizSupplierDetail
	 * @throws
	 */
	BusBizSupplierDetail selectBySupplierId(Integer supplierId);

	/**
	 * 批量查询供应商的结算信息
	 * @param supplierIds
	 * @return
	 */
	List<BusBizSupplierDetail> querySettleInfoByIds(Map<String,Set<Integer>> supplierIds);

	/**
	 * @Title: queryDetailCOBySupplierId
	 * @Description: 查询供应商其它信息比对信息
	 * @param supplierId
	 * @return BusSupplierDetailCO
	 * @throws
	 */
	BusSupplierDetailCO queryDetailCOBySupplierId(Integer supplierId);
}