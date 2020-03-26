package mapper.bigdata.ex;

import com.zhuanche.entity.bigdata.DriverOperAnalyIndex;
import com.zhuanche.entity.bigdata.QueryTermDriverAnaly;

import java.util.List;

public interface BiDriverDisinfectMeasureDayExMapper {

    DriverOperAnalyIndex query(QueryTermDriverAnaly queryTerm);

    List<DriverOperAnalyIndex> trend(QueryTermDriverAnaly queryTerm);

}