package mapper.bigdata.ex;


import com.zhuanche.dto.IndexBiDriverMeasureDto;
import com.zhuanche.dto.bigdata.BiDriverMeasureDayDto;

public interface BiDriverMeasureDayExtMapper {
  public IndexBiDriverMeasureDto findForStatistics(BiDriverMeasureDayDto param);
}