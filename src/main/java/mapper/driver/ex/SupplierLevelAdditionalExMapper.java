package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierLevelAdditional;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierLevelAdditionalExMapper {

    List<SupplierLevelAdditional> findbySupplierLevelId(@Param("supplierLevelId") Integer supplierLevelId );
}