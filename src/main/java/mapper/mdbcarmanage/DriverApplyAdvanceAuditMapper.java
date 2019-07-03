package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverApplyAdvanceAudit;

public interface DriverApplyAdvanceAuditMapper {
    int insert(DriverApplyAdvanceAudit record);

    int insertSelective(DriverApplyAdvanceAudit record);

    DriverApplyAdvanceAudit selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverApplyAdvanceAudit record);

    int updateByPrimaryKey(DriverApplyAdvanceAudit record);
}