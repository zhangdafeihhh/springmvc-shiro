package mapper.bigdata;

import com.zhuanche.entity.bigdata.BiDriverDisinfectMeasureDay;

public interface BiDriverDisinfectMeasureDayMapper {
    int insert(BiDriverDisinfectMeasureDay record);

    int insertSelective(BiDriverDisinfectMeasureDay record);

    BiDriverDisinfectMeasureDay selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BiDriverDisinfectMeasureDay record);

    int updateByPrimaryKey(BiDriverDisinfectMeasureDay record);
}