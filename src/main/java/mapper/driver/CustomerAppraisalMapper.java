package mapper.driver;

import com.zhuanche.entity.driver.CustomerAppraisal;

public interface CustomerAppraisalMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerAppraisal record);

    int insertSelective(CustomerAppraisal record);

    CustomerAppraisal selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerAppraisal record);

    int updateByPrimaryKey(CustomerAppraisal record);
}