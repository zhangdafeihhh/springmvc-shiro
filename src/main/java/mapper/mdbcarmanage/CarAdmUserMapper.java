package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarAdmUser;

import java.util.List;

public interface CarAdmUserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(CarAdmUser record);

    int insertSelective(CarAdmUser record);

    CarAdmUser selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(CarAdmUser record);

    int updateByPrimaryKey(CarAdmUser record);
}