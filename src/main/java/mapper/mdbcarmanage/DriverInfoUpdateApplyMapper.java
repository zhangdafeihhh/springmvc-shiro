package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverInfoUpdateApply;

public interface DriverInfoUpdateApplyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverInfoUpdateApply record);

    int insertSelective(DriverInfoUpdateApply record);

    DriverInfoUpdateApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverInfoUpdateApply record);

    int updateByPrimaryKey(DriverInfoUpdateApply record);
}