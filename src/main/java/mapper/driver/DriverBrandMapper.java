package mapper.driver;

import com.zhuanche.entity.driver.DriverBrand;

public interface DriverBrandMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DriverBrand record);

    int insertSelective(DriverBrand record);

    DriverBrand selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DriverBrand record);

    int updateByPrimaryKey(DriverBrand record);
}