package mapper.bigdata;

import com.zhuanche.entity.bigdata.DisinfectRecord;

public interface DisinfectRecordMapper {
    int insert(DisinfectRecord record);

    int insertSelective(DisinfectRecord record);

    DisinfectRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisinfectRecord record);

    int updateByPrimaryKey(DisinfectRecord record);
}