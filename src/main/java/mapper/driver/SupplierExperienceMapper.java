package mapper.driver;

import com.zhuanche.entity.driver.SupplierExperience;

public interface SupplierExperienceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SupplierExperience record);

    int insertSelective(SupplierExperience record);

    SupplierExperience selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SupplierExperience record);

    int updateByPrimaryKey(SupplierExperience record);
}