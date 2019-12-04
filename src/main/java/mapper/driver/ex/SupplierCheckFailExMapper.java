package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierCheckFail;

import java.util.List;

public interface SupplierCheckFailExMapper {

    int insert(SupplierCheckFail record);


    SupplierCheckFail selectByPrimaryKey(Integer id);


    List<SupplierCheckFail> failList(Integer supplierId);
}