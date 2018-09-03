package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SaasUserRoleRalation;

public interface SaasUserRoleRalationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SaasUserRoleRalation record);

    int insertSelective(SaasUserRoleRalation record);

    SaasUserRoleRalation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SaasUserRoleRalation record);

    int updateByPrimaryKey(SaasUserRoleRalation record);
}