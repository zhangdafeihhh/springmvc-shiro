package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SaasRole;

public interface SaasRoleMapper {
    int deleteByPrimaryKey(Integer roleId);

    int insert(SaasRole record);

    int insertSelective(SaasRole record);

    SaasRole selectByPrimaryKey(Integer roleId);

    int updateByPrimaryKeySelective(SaasRole record);

    int updateByPrimaryKey(SaasRole record);
}