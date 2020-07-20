package mapper.bigdata;

import com.zhuanche.entity.bigdata.BiNewcityDriverReportDay;

public interface BiNewcityDriverReportDayMapper {
    int insert(BiNewcityDriverReportDay record);

    int insertSelective(BiNewcityDriverReportDay record);

    BiNewcityDriverReportDay selectByPrimaryKey(Long tableauId);

    int updateByPrimaryKeySelective(BiNewcityDriverReportDay record);

    int updateByPrimaryKey(BiNewcityDriverReportDay record);
}