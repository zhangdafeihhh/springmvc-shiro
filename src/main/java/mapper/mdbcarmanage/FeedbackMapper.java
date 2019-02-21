package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.Feedback;

public interface FeedbackMapper {
    int insert(Feedback record);

    int insertSelective(Feedback record);

    Feedback selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feedback record);

    int updateByPrimaryKey(Feedback record);
}