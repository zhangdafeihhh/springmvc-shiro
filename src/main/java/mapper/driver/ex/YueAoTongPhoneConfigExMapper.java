package mapper.driver.ex;

import com.zhuanche.controller.driver.YueAoTongPhoneConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/31 下午9:31
 * @Version 1.0
 */
public interface YueAoTongPhoneConfigExMapper {
    List<YueAoTongPhoneConfig> findBySupplierId(@Param("supplierIds") String supplierIds);
}
