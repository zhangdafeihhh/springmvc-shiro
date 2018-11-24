package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarMessagePost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarMessagePostExMapper {

    /**
     * 撤回操作
     * @param messageId
     * @return
     */
    int withDraw(@Param("id")Long messageId);

    /**
     * 获取已读、未读、草稿列表
     * @param messageId
     * @param userId
     * @param status
     * @return
     */
    List<CarMessagePost> listCarMessagePost(@Param("messageId")Integer messageId,
                                            @Param("userId")Integer userId,
                                            @Param("status")Integer status);

    /**
     * 根据messageIds获取列表
     * @param ids
     * @return
     */
    List<CarMessagePost> listCarMessagePostBymesageIds(@Param("ids")List<Long> ids);


    /**
     * 向主表插入数据
     * @param record
     * @return
     */
    int insertSelective(CarMessagePost record);

    /**
     * 更改数据
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(CarMessagePost record);


    int deleteByPrimaryKey(Long id);

    int insert(CarMessagePost record);


    CarMessagePost selectByPrimaryKey(Long id);






}