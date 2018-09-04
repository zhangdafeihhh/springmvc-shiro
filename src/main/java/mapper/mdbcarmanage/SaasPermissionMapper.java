package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SaasPermission;

public interface SaasPermissionMapper {
    int deleteByPrimaryKey(Integer permissionId);

    int insert(SaasPermission record);

    int insertSelective(SaasPermission record);

    SaasPermission selectByPrimaryKey(Integer permissionId);

    int updateByPrimaryKeySelective(SaasPermission record);

    int updateByPrimaryKey(SaasPermission record);
}