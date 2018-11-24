package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarMessageDoc;
import org.apache.ibatis.annotations.Param;

public interface CarMessageDocExMapper {
    int deleteByPrimaryKey(Long id);

    /**
     * 发消息后上传附件
     * @param record
     * @return
     */
    int insert(CarMessageDoc record);

    int updateStatus(@Param("messageId")Long messageId,@Param("state")Integer state);
    /**
     * 撤回消息后删除附件消息
     * @param messageId
     * @return
     */
    int deleteByMessaeId(@Param("messageId")Long messageId);

    int insertSelective(CarMessageDoc record);

    CarMessageDoc selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CarMessageDoc record);

    int updateByPrimaryKey(CarMessageDoc record);


}