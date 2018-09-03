package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarSysMobileClientPublish;

public interface CarSysMobileClientPublishMapper {
    int deleteByPrimaryKey(Integer versionId);

    int insert(CarSysMobileClientPublish record);

    int insertSelective(CarSysMobileClientPublish record);

    CarSysMobileClientPublish selectByPrimaryKey(Integer versionId);

    int updateByPrimaryKeySelective(CarSysMobileClientPublish record);

    int updateByPrimaryKey(CarSysMobileClientPublish record);
}