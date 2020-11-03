package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;

public interface InterDriverLineRelExMapper {
    int insert(InterDriverLineRel record);

    int insertSelective(InterDriverLineRel record);

    InterDriverLineRel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InterDriverLineRel record);

    int updateByPrimaryKey(InterDriverLineRel record);
}