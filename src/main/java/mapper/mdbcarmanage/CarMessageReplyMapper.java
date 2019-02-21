package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarMessageReply;

public interface CarMessageReplyMapper {
    int insert(CarMessageReply record);

    int insertSelective(CarMessageReply record);

    CarMessageReply selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CarMessageReply record);

    int updateByPrimaryKey(CarMessageReply record);
}