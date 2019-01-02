package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierExtDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SupplierExtDtoExMapper {
    List<SupplierExtDto> queryExtDtoByIdList(@Param("idList") List<Integer> idList);

    SupplierExtDto updateBySupplierId(SupplierExtDto extDto);
}
