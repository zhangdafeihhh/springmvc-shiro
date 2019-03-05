package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.Feedback;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FeedbackExMapper {

    List<Feedback> findDataList(@Param("createTimeStart") String createTimeStart,
                                @Param("createTimeEnd") String createTimeEnd,
                                @Param("manageStatus") Integer manageStatus);

    List<Feedback> findDataListSelf(@Param("createTimeStart") String createTimeStart,
                                    @Param("createTimeEnd") String createTimeEnd,
                                    @Param("manageStatus") Integer manageStatus,
                                    @Param("userId") Integer userId);

}