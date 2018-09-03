package mapper.driver;

import com.zhuanche.entity.driver.DriverCert;

public interface DriverCertMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DriverCert record);

    int insertSelective(DriverCert record);

    DriverCert selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DriverCert record);

    int updateByPrimaryKeyWithBLOBs(DriverCert record);

    int updateByPrimaryKey(DriverCert record);
}