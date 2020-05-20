package mapper.bigdata.ex;


import com.zhuanche.dto.IndexBiDriverMeasureDto;
import com.zhuanche.dto.bigdata.BiDriverMeasureDayDto;
import com.zhuanche.dto.bigdata.DisinfectPenetranceDTO;import org.apache.ibatis.annotations.Param;

public interface BiDriverMeasureDayExtMapper {
  public IndexBiDriverMeasureDto findForStatistics(BiDriverMeasureDayDto param);

  DisinfectPenetranceDTO disinfectPenetrance(@Param("startDate")String startDate, @Param("endDate")String endDate, @Param("supplierId")Integer supplierId, @Param("suppliers")String suppliers);
}