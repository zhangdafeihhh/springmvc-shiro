package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarMessageReceiver;

public interface CarMessageReceiverMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarMessageReceiver record);

    int insertSelective(CarMessageReceiver record);

    CarMessageReceiver selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CarMessageReceiver record);

    int updateByPrimaryKey(CarMessageReceiver record);


}