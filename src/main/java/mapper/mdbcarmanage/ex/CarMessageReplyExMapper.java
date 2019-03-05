package mapper.mdbcarmanage.ex;


import com.zhuanche.entity.mdbcarmanage.CarMessageReply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarMessageReplyExMapper {

    List<CarMessageReply> findReplyListByMessageIdPage(@Param("messageId") Long messageId);

    List<CarMessageReply> findReplyListByMessageIdAndSenderIdPage(@Param("messageId") Long messageId, @Param("senderId") Integer senderId);

    Integer deleteByMessageId(@Param("messageId")Integer messageId);
}