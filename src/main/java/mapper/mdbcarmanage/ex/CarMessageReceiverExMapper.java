package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarMessageReceiver;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarMessageReceiverExMapper {

    /**
     * 撤回消息后删除已发的消息
     * @param messageId
     * @return
     */
    int deleteByMessageId(@Param("messageId")Long messageId);

    /**
     * 获取未读数量
     * @param receiveUserId
     * @return
     */
    Integer messageUnreadCount(@Param("receiveUserId")Long receiveUserId);

    /**
     * 查询列表
     * @param messageId
     * @param receiveUserId
     * @param status
     * @return
     */
    List<CarMessageReceiver> carMessageReceiverList(@Param("messageId")Integer messageId,
                                                    @Param("receiveUserId")Integer receiveUserId,
                                                    @Param("status")Integer status);


    /**
     * 向字表插入数据
     * @param record
     * @return
     */
    int insert(CarMessageReceiver record);


    /**
     * 未读变已读
     * @param id
     * @return
     */
    int updateReadState(@Param("id")Long id);

}