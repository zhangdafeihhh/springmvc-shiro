package mapper.bigdata;


import com.zhuanche.entity.bigdata.BiDriverMeasureDay;

import java.util.List;

public interface BiDriverMeasureDayMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BiDriverMeasureDay record);

    int insertSelective(BiDriverMeasureDay record);

    BiDriverMeasureDay selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BiDriverMeasureDay record);

    int updateByPrimaryKey(BiDriverMeasureDay record);

    List<BiDriverMeasureDay> getRecordList(BiDriverMeasureDay params);
}