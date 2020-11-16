package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;

public interface InterDriverLineRelExMapper {


    InterDriverLineRel queryDriverLineRelByUserId(Integer id);

    int updateByPrimaryKeySelective(InterDriverLineRel record);

    int insertSelective(InterDriverLineRel record);
 }