package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarMessagePost;

public interface CarMessagePostMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarMessagePost record);

    int insertSelective(CarMessagePost record);

    CarMessagePost selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CarMessagePost record);

    int updateByPrimaryKey(CarMessagePost record);
}