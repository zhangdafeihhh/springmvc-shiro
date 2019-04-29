package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierExperience;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SupplierExperienceExMapper {

    List<SupplierExperience> selectAllBySupplierId(@Param("supplierId") Integer supplierId);

    int deleteBySupplierId(@Param("supplierId") Integer supplierId);

}