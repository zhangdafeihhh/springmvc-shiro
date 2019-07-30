package mapper.driver;

import com.zhuanche.entity.driver.SupplierLevel;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierLevelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SupplierLevel record);

    int insertSelective(SupplierLevel record);

    SupplierLevel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierLevel record);

    int updateByPrimaryKey(SupplierLevel record);


}