package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SaasRolePermissionRalation;

public interface SaasRolePermissionRalationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SaasRolePermissionRalation record);

    int insertSelective(SaasRolePermissionRalation record);

    SaasRolePermissionRalation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SaasRolePermissionRalation record);

    int updateByPrimaryKey(SaasRolePermissionRalation record);
}