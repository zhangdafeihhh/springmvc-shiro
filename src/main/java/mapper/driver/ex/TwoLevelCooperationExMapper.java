package mapper.driver.ex;

import com.zhuanche.entity.driver.TwoLevelCooperationDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TwoLevelCooperationExMapper {

    List<TwoLevelCooperationDto> getTwoLevelCooperationTypeByCooperationId(@Param("cooperationId") Integer cooperationId);

    TwoLevelCooperationDto getTwoLevelCooperationById(@Param("id") int id);

    TwoLevelCooperationDto getTwoLevelCooperation(@Param("pid")Integer cooperationType, @Param("id")Integer twoLevelCooperation);
}
