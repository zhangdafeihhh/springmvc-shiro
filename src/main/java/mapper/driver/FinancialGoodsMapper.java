package mapper.driver;

import com.zhuanche.entity.driver.FinancialGoods;

public interface FinancialGoodsMapper {
    int deleteByPrimaryKey(Integer goodsId);

    int insert(FinancialGoods record);

    int insertSelective(FinancialGoods record);

    FinancialGoods selectByPrimaryKey(Integer goodsId);

    int updateByPrimaryKeySelective(FinancialGoods record);

    int updateByPrimaryKey(FinancialGoods record);
}