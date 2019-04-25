package mapper.bigdata;

import com.zhuanche.entity.bigdata.BiDriverBusinessInfoSummaryReport;

public interface BiDriverBusinessInfoSummaryReportMapper {
    int insert(BiDriverBusinessInfoSummaryReport record);

    int insertSelective(BiDriverBusinessInfoSummaryReport record);

    BiDriverBusinessInfoSummaryReport selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BiDriverBusinessInfoSummaryReport record);

    int updateByPrimaryKey(BiDriverBusinessInfoSummaryReport record);
}