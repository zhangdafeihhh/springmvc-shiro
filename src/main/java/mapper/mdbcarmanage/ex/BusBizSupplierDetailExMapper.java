package mapper.mdbcarmanage.ex;

import java.util.List;
import java.util.Map;

import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;

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
	 * @Description: 供应商其它信息查询
	 * @param param
	 * @return 
	 * @return List<BusBizSupplierDetail>
	 * @throws
	 */
	List<BusBizSupplierDetail> querySupplierList(Map<Object, Object> param);

}