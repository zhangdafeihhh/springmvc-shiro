package com.zhuanche.serv.financial;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.util.FinancialUtil;
import com.zhuanche.common.util.FinancialUtil.NumType;
import com.zhuanche.constants.financial.FinancialConst.GoodsState;
import com.zhuanche.dto.financial.FinancialGoodsDTO;
import com.zhuanche.dto.financial.FinancialGoodsInfoDTO;
import com.zhuanche.dto.financial.FinancialGoodsParamDTO;
import com.zhuanche.entity.driver.FinancialAdditionalClause;
import com.zhuanche.entity.driver.FinancialGoods;
import com.zhuanche.entity.driver.FinancialGoodsClause;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;

import mapper.driver.FinancialAdditionalClauseMapper;
import mapper.driver.FinancialGoodsClauseMapper;
import mapper.driver.FinancialGoodsMapper;
import mapper.driver.ex.FinancialAdditionalClauseExMapper;
import mapper.driver.ex.FinancialGoodsClauseExMapper;
import mapper.driver.ex.FinancialGoodsExMapper;

/**  
 * ClassName:FinancialGoodsService <br/>  
 * Date:     2019年4月23日 下午6:58:34 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Service
public class FinancialGoodsService {
	private static final Logger logger = LoggerFactory.getLogger(FinancialBasicsVehiclesService.class);
	@Autowired
	private FinancialGoodsMapper financialGoodsMapper;
	
	@Autowired
	private FinancialGoodsExMapper financialGoodsExMapper;
	
	@Autowired
	private FinancialGoodsClauseExMapper financialGoodsClauseExMapper;
	
	@Autowired
	private FinancialGoodsClauseMapper financialGoodsClauseMapper;
	
	@Autowired
	private FinancialAdditionalClauseMapper financialAdditionalClauseMapper;
	
	@Autowired
	private FinancialAdditionalClauseExMapper financialAdditionalClauseExMapper;
	
	public PageDTO queryFinancialGoodsForList(Integer page, Integer pageSize, String goodsName,
			Integer basicsVehiclesId, Byte salesTarget, Set<Integer> supplierIds, Set<Integer> cityIds, Byte status) {
		
		if(page==null || page.intValue()<=0) {
			page = new Integer(1);
		}
		if(pageSize==null || pageSize.intValue()<=0) {
			pageSize = new Integer(10);
		}
		//执行SQL查询
    	int total = 0;
    	List<FinancialGoodsDTO> financialGoodsDTOs=null;
    	Page p = PageHelper.startPage( page, pageSize, true );
    	try{
    		financialGoodsDTOs=financialGoodsExMapper.queryFinancialGoodsForList(goodsName,basicsVehiclesId,salesTarget,supplierIds,cityIds,status);
        	total  = (int)p.getTotal();
    	}catch (Exception e) {
    		logger.error("查询商品信息列表异常",e);
		}finally {
        	PageHelper.clearPage();
    	}
    	//判断返回结果
    	if(financialGoodsDTOs==null || financialGoodsDTOs.size()==0) {
    		return new PageDTO(page, pageSize, total, new ArrayList());
    	}
    	//返回
    	return new PageDTO(page,pageSize,total,financialGoodsDTOs);
	}

	public FinancialGoods saveFinancialGoods(FinancialGoodsParamDTO financialGoodsParamDTO) {
		FinancialGoods financialGoods=BeanUtil.copyObject(financialGoodsParamDTO, FinancialGoods.class);
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		Date now=new Date();
		financialGoods.setCreateBy(user.getName());
		financialGoods.setUpdateBy(user.getName());
		financialGoods.setStatus(GoodsState.STAY_ON_THE_SHELF);
		String goodsNumber = FinancialUtil.genLongNum(NumType.GOODS_SP);
		financialGoods.setGoodsNumber(goodsNumber);
		//financialGoods.setCreateTime(now);
		//financialGoods.setUpdateTime(now);
		int i=financialGoodsMapper.insertSelective(financialGoods);
		if (i>0) {
			String additionalClause = financialGoodsParamDTO.getAdditionalClause();
			if (StringUtils.isNotBlank(additionalClause)) {
				List<FinancialAdditionalClause> financialAdditionalClauses=JSON.parseArray(additionalClause, FinancialAdditionalClause.class);
				for (FinancialAdditionalClause financialAdditionalClause : financialAdditionalClauses) {
					financialAdditionalClause.setCreateBy(user.getName());
					financialAdditionalClause.setUpdateBy(user.getName());
					int f=financialAdditionalClauseMapper.insertSelective(financialAdditionalClause);
					if (f>0) {
						FinancialGoodsClause financialGoodsClause = new FinancialGoodsClause();
						financialGoodsClause.setGoodsNumber(goodsNumber);
						financialGoodsClause.setGoodsId(financialGoods.getGoodsId());
						financialGoodsClause.setClauseId(financialAdditionalClause.getClauseId());
						financialGoodsClause.setCreateBy(user.getName());
						financialGoodsClause.setUpdateBy(user.getName());
						financialGoodsClauseMapper.insertSelective(financialGoodsClause);
					}
				}
			}
		}
		return financialGoods;
	}

	public FinancialGoods updateFinancialGoods(FinancialGoodsParamDTO financialGoodsParamDTO) {
		FinancialGoods financialGoods=BeanUtil.copyObject(financialGoodsParamDTO, FinancialGoods.class);
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		financialGoods.setUpdateBy(user.getName());
		int i=financialGoodsMapper.updateByPrimaryKeySelective(financialGoods);
		if (i>0) {
			//删除附加条款
			List<FinancialGoodsClause> financialGoodsClauseList = financialGoodsClauseExMapper.queryFinancialGoodsClauseListForGoodsId(financialGoods.getGoodsId());
			
			List<Integer> clauseIds= financialGoodsClauseList.stream().map(f -> f.getClauseId()).collect(Collectors.toList());
			
			int dfgc=financialGoodsClauseExMapper.deleteFinancialGoodsClause(financialGoods.getGoodsId());
			
			int dfac=financialAdditionalClauseExMapper.deleteByPrimaryKeyS(clauseIds);
			
			if (i>0) {
					String additionalClause = financialGoodsParamDTO.getAdditionalClause();
					if (StringUtils.isNotBlank(additionalClause)) {
						List<FinancialAdditionalClause> financialAdditionalClauses=JSON.parseArray(additionalClause, FinancialAdditionalClause.class);
					for (FinancialAdditionalClause financialAdditionalClause : financialAdditionalClauses) {
						financialAdditionalClause.setCreateBy(user.getName());
						financialAdditionalClause.setUpdateBy(user.getName());
						int f=financialAdditionalClauseMapper.insertSelective(financialAdditionalClause);
						if (f>0) {
							FinancialGoodsClause financialGoodsClause = new FinancialGoodsClause();
							financialGoodsClause.setGoodsNumber(financialGoods.getGoodsNumber());
							financialGoodsClause.setGoodsId(financialGoods.getGoodsId());
							financialGoodsClause.setClauseId(financialAdditionalClause.getClauseId());
							financialGoodsClause.setCreateBy(user.getName());
							financialGoodsClause.setUpdateBy(user.getName());
							financialGoodsClauseMapper.insertSelective(financialGoodsClause);
						}
					}
				}
			}
		}
		return financialGoods;
	}

	public int updateFinancialGoodsByStatus(Integer goodsId, Byte status) {
		SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
		FinancialGoods financialGoods=new FinancialGoods();
		financialGoods.setGoodsId(goodsId);
		financialGoods.setStatus(status);
		financialGoods.setUpdateBy(user.getName());
		int i=financialGoodsMapper.updateByPrimaryKeySelective(financialGoods);
		return i;
	}

	public FinancialGoodsInfoDTO queryFinancialGoodsById(Integer goodsId) {
		FinancialGoods financialGoods = financialGoodsMapper.selectByPrimaryKey(goodsId);
		FinancialGoodsInfoDTO financialGoodsDTO=BeanUtil.copyObject(financialGoods, FinancialGoodsInfoDTO.class);
		List<FinancialGoodsClause> financialGoodsClauseList = financialGoodsClauseExMapper.queryFinancialGoodsClauseListForGoodsId(financialGoods.getGoodsId());
		
		if (financialGoodsClauseList!=null && financialGoodsClauseList.size()>0) {
			List<Integer> clauseIds= financialGoodsClauseList.stream().map(f -> f.getClauseId()).collect(Collectors.toList());
			List<FinancialAdditionalClause> financialAdditionalClauses=financialAdditionalClauseExMapper.queryFinancialAdditionalClause(clauseIds);
			financialGoodsDTO.setFinancialadditionalclauses(financialAdditionalClauses);
		}
		return financialGoodsDTO;
	}

	public List<FinancialGoodsDTO> selectFinancialGoodsForList(Set<Integer> supplierIds, Set<Integer> cityIds) {
		List<FinancialGoodsDTO> financialGoodsDTOs = financialGoodsExMapper.queryFinancialGoodsForList(null,null,null,supplierIds,cityIds,null);
		return financialGoodsDTOs;
	}

}
  
