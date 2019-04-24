package com.zhuanche.serv.financial;

import org.springframework.stereotype.Service;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.financial.FinancialGoodsDTO;
import com.zhuanche.dto.financial.FinancialGoodsParamDTO;

/**  
 * ClassName:FinancialGoodsService <br/>  
 * Date:     2019年4月23日 下午6:58:34 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Service
public class FinancialGoodsService {

	public PageDTO queryFinancialGoodsForList(Integer page, Integer pageSize, String goodsName,
			Integer basicsVehiclesId, Byte salesTarget, Integer supplierId, Integer cityId, Byte status) {
		  
		return null;
	}

	public int saveFinancialGoods(FinancialGoodsParamDTO financialGoodsParamDTO) {
		  
		return 0;
	}

	public int updateFinancialGoods(FinancialGoodsParamDTO financialGoodsParamDTO) {
		  
		return 0;
	}

	public int updateFinancialGoodsByStatus(Integer goodsId, Byte status) {
		  
		return 0;
	}

	public FinancialGoodsDTO queryFinancialGoodsById(Integer goodsId) {
		  
		return null;
	}

}
  
