package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.FeedbackDoc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FeedbackDocExMapper {

    List<FeedbackDoc> selectFeedBackDocListByFeedBackId(@Param("feedBackId") Integer feedBackId);
}