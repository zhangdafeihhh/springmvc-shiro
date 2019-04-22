package mapper.bigdata.ex;


import com.zhuanche.entity.bigdata.SAASDriverEvaluateDetailDto;
import com.zhuanche.entity.bigdata.SAASEvaluateDetailQuery;

import java.util.List;

public interface DriverEvaluateDetailExMapper {

    List<SAASDriverEvaluateDetailDto> getDriverEvaluateDetail(SAASEvaluateDetailQuery saasIndexQuery);

    List<SAASDriverEvaluateDetailDto> getDriverEvaluateDetailList(SAASEvaluateDetailQuery saasIndexQuery);
}