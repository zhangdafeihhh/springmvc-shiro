package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.BusBizChangeLog;

public interface BusBizChangeLogMapper {
    int insert(BusBizChangeLog record);

    int insertSelective(BusBizChangeLog record);

    BusBizChangeLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BusBizChangeLog record);

    int updateByPrimaryKey(BusBizChangeLog record);
}