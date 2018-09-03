package mapper.driver;

import com.zhuanche.entity.driver.DriverBigdataDistrict;

public interface DriverBigdataDistrictMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverBigdataDistrict record);

    int insertSelective(DriverBigdataDistrict record);

    DriverBigdataDistrict selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverBigdataDistrict record);

    int updateByPrimaryKeyWithBLOBs(DriverBigdataDistrict record);

    int updateByPrimaryKey(DriverBigdataDistrict record);
}