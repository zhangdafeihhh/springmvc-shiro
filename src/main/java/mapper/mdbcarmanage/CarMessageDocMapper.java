package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarMessageDoc;

public interface CarMessageDocMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarMessageDoc record);

    int insertSelective(CarMessageDoc record);

    CarMessageDoc selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CarMessageDoc record);

    int updateByPrimaryKey(CarMessageDoc record);
}