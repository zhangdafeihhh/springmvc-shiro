package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.DriverApplyAdvanceAudit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DriverApplyAdvanceAuditExMapper {
    List<DriverApplyAdvanceAudit> queryListDataByOrderNum(@Param("orderNumList") List<String> orderNumList);
}