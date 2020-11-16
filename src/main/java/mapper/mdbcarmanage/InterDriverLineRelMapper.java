package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;

public interface InterDriverLineRelMapper {
    int insert(InterDriverLineRel record);

    int insertSelective(InterDriverLineRel record);

    InterDriverLineRel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InterDriverLineRel record);

    int updateByPrimaryKey(InterDriverLineRel record);
}