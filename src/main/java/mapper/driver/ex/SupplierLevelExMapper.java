package mapper.driver.ex;

import com.zhuanche.entity.driver.SupplierLevel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierLevelExMapper {

    public List<SupplierLevel> findPage(SupplierLevel item);

    void doPublishSupplierLevel(@Param("list") List<Integer> idList);

    void doUnPublishSupplierLevel(@Param("list") List<Integer> idList);
}