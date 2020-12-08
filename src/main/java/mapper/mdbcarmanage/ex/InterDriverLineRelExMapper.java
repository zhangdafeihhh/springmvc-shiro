package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterDriverLineRelExMapper {

    /**
     * 根据userId查询
     * @param userId
     * @return
     */
    InterDriverLineRel queryDriverLineRelByUserId(@Param("userId") Integer userId);


    /**
     * 根据线路id获取线路可以哪些司机使用
     * @param lineId
     * @return
     */
    List<InterDriverLineRel> queryDriversByLineId(@Param("lineId") Integer lineId);


    int updateByPrimaryKeySelective(InterDriverLineRel record);

    int insertSelective(InterDriverLineRel record);
 }