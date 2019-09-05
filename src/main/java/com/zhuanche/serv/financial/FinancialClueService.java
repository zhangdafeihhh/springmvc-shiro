package com.zhuanche.serv.financial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.financial.FinancialClueDTO;
import com.zhuanche.dto.financial.FinancialClueGoodsDTO;
import com.zhuanche.entity.driver.FinancialClue;
import com.zhuanche.util.BeanUtil;

import mapper.driver.FinancialClueMapper;
import mapper.driver.ex.FinancialClueExMapper;
import mapper.driver.ex.FinancialClueGoodsExMapper;

/**  
 * ClassName:FinancialClueService <br/>  
 * Date:     2019年4月23日 下午6:59:12 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Service
public class FinancialClueService {
	private static final Logger logger = LoggerFactory.getLogger(FinancialClueService.class);
	
	@Autowired
	private FinancialClueMapper financialClueMapper;
	
	@Autowired
	private FinancialClueExMapper financialClueExMapper;
	
	@Autowired
	private FinancialClueGoodsExMapper financialClueGoodsExMapper;
	public PageDTO queryfinancialClueForList(Integer page, Integer pageSize, String purposeName,Integer goodsId,
			String startDate, String endDate, Set<Integer> supplierIds, Set<Integer> cityIds, Byte status,Byte goodsType) {
		if(page==null || page.intValue()<=0) {
			page = new Integer(1);
		}
		if(pageSize==null || pageSize.intValue()<=0) {
			pageSize = new Integer(10);
		}
		//执行SQL查询
    	int total = 0;
    	List<FinancialClueDTO> financialClueDTOs=null;
    	Page p = PageHelper.startPage( page, pageSize, true );
    	try{
    		financialClueDTOs=financialClueExMapper.queryfinancialClueForList(purposeName,goodsId,
    				startDate,endDate,supplierIds,cityIds,status,goodsType);
        	total  = (int)p.getTotal();
    	}catch (Exception e) {
    		logger.error("查询线索信息异常",e);
		}finally {
        	PageHelper.clearPage();
    	}
    	//判断返回结果
    	if(financialClueDTOs==null || financialClueDTOs.size()==0) {
    		return new PageDTO(page, pageSize, total, new ArrayList());
    	}
    	//返回
    	return new PageDTO(page,pageSize,total,financialClueDTOs);
	}

	public Map queryfinancialClueById(Integer clueId) {
		Map resultMap=new HashMap<String,Object>();
		FinancialClue financialClue = financialClueMapper.selectByPrimaryKey(clueId);
		FinancialClueDTO financialClueDTO=BeanUtil.copyObject(financialClue, FinancialClueDTO.class);
		FinancialClueGoodsDTO financialClueGoods = financialClueGoodsExMapper.queryfinancialClueGoodsForObject(clueId);
		resultMap.put("financialClue", financialClueDTO);
		resultMap.put("financialClueGoods", financialClueGoods);
		return resultMap;
	}

}
  
