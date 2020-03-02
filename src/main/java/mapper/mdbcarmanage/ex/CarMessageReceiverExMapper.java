package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.CarMessagePostDto;
import com.zhuanche.dto.mdbcarmanage.CarMessageReplyDto;
import com.zhuanche.entity.mdbcarmanage.CarMessageGroup;
import com.zhuanche.entity.mdbcarmanage.CarMessageReceiver;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    int queryAllCount(@Param("keyword")String keyword, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                      @Param("idList") List<Integer> idList, @Param("userId")Integer userId);

    int queryCountInTitle(@Param("keyword") String keyword, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                          @Param("idList") List<Integer> idList, @Param("userId") Integer userId);

    int queryCountInAttachment(@Param("keyword")String keyword, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                               @Param("idList") List<Integer> idList, @Param("userId") Integer userId);

    List<CarMessagePostDto> queryALlData(@Param("keyword") String keyword, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                         @Param("idList") List<Integer> idList, @Param("userId") Integer userId,
                                         @Param("offset") Integer offset, @Param("limit") Integer limit);

    List<CarMessagePostDto> queryDataInTitle(@Param("keyword") String keyword, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                             @Param("idList") List<Integer> idList, @Param("userId") Integer userId,
                                             @Param("offset") Integer offset, @Param("limit") Integer limit);

    List<CarMessagePostDto> queryDataInAttachment(@Param("keyword") String keyword, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                                  @Param("idList") List<Integer> idList, @Param("userId") Integer userId,
                                                  @Param("offset") Integer offset, @Param("limit") Integer limit);

    Integer messagePostDtoCount(@Param("receiveUserId") Integer receiveUserId,
                                     @Param("messageId") Integer messageId);

    List<CarMessagePostDto> newSearchMessage(@Param("status") Integer status,@Param("keyword") String keyword, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                             @Param("idList") List<Integer> idList, @Param("userId") Integer userId);

    int replyMessage(@Param("messageId")Integer messageId,@Param("receiveUserId")Integer receiveUserId,@Param("status")Integer status);


    List<CarMessageReplyDto> replyQueryList(CarMessageReplyDto carMessageReplyDto);


    List<CarMessagePostDto> messageReceiveQueryList(@Param("status") Integer status,@Param("keyword") String keyword, @Param("startDate") String startDate, @Param("endDate") String endDate,
                                             @Param("idList") List<Integer> idList, @Param("userId") Integer userId);

}