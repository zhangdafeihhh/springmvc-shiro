package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierCheckFail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SupplierCheckFailExMapper {

    int insert(SupplierCheckFail record);


    SupplierCheckFail selectByPrimaryKey(Integer id);


    List<SupplierCheckFail> failList(@Param("supplierId") Integer supplierId);
}