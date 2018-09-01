package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizDistrict;

public interface CarBizDistrictMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarBizDistrict record);

    int insertSelective(CarBizDistrict record);

    CarBizDistrict selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizDistrict record);

    int updateByPrimaryKeyWithBLOBs(CarBizDistrict record);

    int updateByPrimaryKey(CarBizDistrict record);
}