package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.BusBizDriverViolators;

public interface BusBizDriverViolatorsMapper {
    int insert(BusBizDriverViolators record);

    int insertSelective(BusBizDriverViolators record);

    BusBizDriverViolators selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BusBizDriverViolators record);

    int updateByPrimaryKey(BusBizDriverViolators record);
}