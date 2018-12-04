package mapper.mdbcarmanage.ex;

import java.util.List;
import java.util.Map;

import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;
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
	 * @Title: 校验是否存在
	 * @Description: TODO
	 * @param supplierId
	 * @return 
	 * @return boolean
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

}