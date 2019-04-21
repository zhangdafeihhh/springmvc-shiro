package mapper.bigdata;

import com.zhuanche.entity.bigdata.BiDriverBusinessInfoDayReport;

public interface BiDriverBusinessInfoDayReportMapper {
    int insert(BiDriverBusinessInfoDayReport record);

    int insertSelective(BiDriverBusinessInfoDayReport record);

    BiDriverBusinessInfoDayReport selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BiDriverBusinessInfoDayReport record);

    int updateByPrimaryKey(BiDriverBusinessInfoDayReport record);
}