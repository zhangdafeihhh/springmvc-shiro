package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarMessageDoc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarMessageDocExMapper {

    /**
     * 更新附件后删除原来的附件
     * @param id
     * @return
     */
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


    /**
     * 根据messageId获取详情
     * @param messageId
     * @return
     */
    List<CarMessageDoc> listDoc(@Param("messageId")Long messageId);




}