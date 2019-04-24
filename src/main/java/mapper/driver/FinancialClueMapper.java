package mapper.driver;

import com.zhuanche.entity.driver.FinancialClue;

public interface FinancialClueMapper {
    int deleteByPrimaryKey(Integer clueId);

    int insert(FinancialClue record);

    int insertSelective(FinancialClue record);

    FinancialClue selectByPrimaryKey(Integer clueId);

    int updateByPrimaryKeySelective(FinancialClue record);

    int updateByPrimaryKey(FinancialClue record);
}