package mapper.bigdata;

import com.zhuanche.entity.bigdata.BigDataCarBizDriverInfo;

public interface BigDataCarBizDriverInfoMapper {
    int insert(BigDataCarBizDriverInfo record);

    int insertSelective(BigDataCarBizDriverInfo record);

    BigDataCarBizDriverInfo selectByPrimaryKey(Integer driverId);

    int updateByPrimaryKeySelective(BigDataCarBizDriverInfo record);

    int updateByPrimaryKeyWithBLOBs(BigDataCarBizDriverInfo record);

    int updateByPrimaryKey(BigDataCarBizDriverInfo record);
}