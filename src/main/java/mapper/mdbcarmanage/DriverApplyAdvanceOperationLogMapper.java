package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverApplyAdvanceOperationLog;

public interface DriverApplyAdvanceOperationLogMapper {
    int insert(DriverApplyAdvanceOperationLog record);

    int insertSelective(DriverApplyAdvanceOperationLog record);

    DriverApplyAdvanceOperationLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverApplyAdvanceOperationLog record);

    int updateByPrimaryKey(DriverApplyAdvanceOperationLog record);
}