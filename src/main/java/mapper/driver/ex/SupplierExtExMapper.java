package mapper.driver.ex;

 import com.zhuanche.entity.driver.SupplierExt;
 import com.zhuanche.entity.driver.SupplierExtDto;
 import org.apache.ibatis.annotations.Param;

 import java.util.List;

public interface SupplierExtExMapper {


    List<SupplierExtDto> extDtoList(SupplierExtDto dto);


    SupplierExtDto extDtoDetail(@Param("supplierId") Integer supplierId);

    int editExtDto(SupplierExtDto dto);

    SupplierExtDto recordDetail(@Param("id") Integer id);

    int editExtStatus(SupplierExtDto dto);

}