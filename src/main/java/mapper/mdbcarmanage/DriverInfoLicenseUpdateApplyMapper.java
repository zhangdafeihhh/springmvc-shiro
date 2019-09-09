package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverInfoLicenseUpdateApply;

public interface DriverInfoLicenseUpdateApplyMapper {
    int insert(DriverInfoLicenseUpdateApply record);

    int insertSelective(DriverInfoLicenseUpdateApply record);

    DriverInfoLicenseUpdateApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverInfoLicenseUpdateApply record);

    int updateByPrimaryKey(DriverInfoLicenseUpdateApply record);
}