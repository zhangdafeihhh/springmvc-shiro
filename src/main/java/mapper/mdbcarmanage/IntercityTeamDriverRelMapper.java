package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.IntercityTeamDriverRel;

public interface IntercityTeamDriverRelMapper {
    int insert(IntercityTeamDriverRel record);

    int insertSelective(IntercityTeamDriverRel record);

    IntercityTeamDriverRel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IntercityTeamDriverRel record);

    int updateByPrimaryKey(IntercityTeamDriverRel record);
}