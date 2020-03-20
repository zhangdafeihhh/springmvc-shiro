package mapper.driver;

import com.zhuanche.entity.driver.DisinfectRecord;

/**
 * @author admin
 */
public interface DisinfectRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DisinfectRecord record);

    int insertSelective(DisinfectRecord record);

    DisinfectRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisinfectRecord record);

    int updateByPrimaryKey(DisinfectRecord record);
}