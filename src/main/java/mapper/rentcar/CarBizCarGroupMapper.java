package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizCarGroup;

public interface CarBizCarGroupMapper {
    int deleteByPrimaryKey(Integer groupId);

    int insert(CarBizCarGroup record);

    int insertSelective(CarBizCarGroup record);

    CarBizCarGroup selectByPrimaryKey(Integer groupId);

    int updateByPrimaryKeySelective(CarBizCarGroup record);

    int updateByPrimaryKeyWithBLOBs(CarBizCarGroup record);

    int updateByPrimaryKey(CarBizCarGroup record);
}