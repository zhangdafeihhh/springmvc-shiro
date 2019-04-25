package mapper.bigdata;

import com.zhuanche.entity.bigdata.BiDriverBusinessInfoMonthReport;

public interface BiDriverBusinessInfoMonthReportMapper {
    int insert(BiDriverBusinessInfoMonthReport record);

    int insertSelective(BiDriverBusinessInfoMonthReport record);

    BiDriverBusinessInfoMonthReport selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BiDriverBusinessInfoMonthReport record);

    int updateByPrimaryKey(BiDriverBusinessInfoMonthReport record);
}