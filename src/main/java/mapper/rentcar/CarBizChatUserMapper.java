package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizChatUser;

public interface CarBizChatUserMapper {
    int deleteByPrimaryKey(Integer chatUserId);

    int insert(CarBizChatUser record);

    int insertSelective(CarBizChatUser record);

    CarBizChatUser selectByPrimaryKey(Integer chatUserId);

    int updateByPrimaryKeySelective(CarBizChatUser record);

    int updateByPrimaryKey(CarBizChatUser record);
}