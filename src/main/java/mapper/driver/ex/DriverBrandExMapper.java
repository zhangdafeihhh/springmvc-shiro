package mapper.driver.ex;

import java.util.List;

import com.zhuanche.entity.driver.DriverBrand;
import org.apache.ibatis.annotations.Param;

public interface DriverBrandExMapper {

	List<DriverBrand> queryDriverBrandList(@Param("brandName") String brandName);

    DriverBrand queryDriverBrandByName(@Param("brandName") String brandName);
}