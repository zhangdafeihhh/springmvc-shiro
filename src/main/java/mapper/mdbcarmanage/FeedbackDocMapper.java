package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.FeedbackDoc;

public interface FeedbackDocMapper {
    int insert(FeedbackDoc record);

    int insertSelective(FeedbackDoc record);

    FeedbackDoc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FeedbackDoc record);

    int updateByPrimaryKey(FeedbackDoc record);
}