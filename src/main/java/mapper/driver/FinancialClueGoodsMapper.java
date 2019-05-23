package mapper.driver;

import com.zhuanche.entity.driver.FinancialClueGoods;

public interface FinancialClueGoodsMapper {
    int deleteByPrimaryKey(Integer clueGoodsId);

    int insert(FinancialClueGoods record);

    int insertSelective(FinancialClueGoods record);

    FinancialClueGoods selectByPrimaryKey(Integer clueGoodsId);

    int updateByPrimaryKeySelective(FinancialClueGoods record);

    int updateByPrimaryKey(FinancialClueGoods record);
}