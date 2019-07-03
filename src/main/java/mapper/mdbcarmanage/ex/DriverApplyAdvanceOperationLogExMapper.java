package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.DriverApplyAdvanceOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriverApplyAdvanceOperationLogExMapper {

    List<DriverApplyAdvanceOperationLog>querySuccessApplyLogByOrderNum(@Param("orderNumList") List<String> orderNumList);
}