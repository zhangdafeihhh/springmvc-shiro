package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.BusOrderMessageTask;

public interface BusOrderMessageTaskMapper {
    int insert(BusOrderMessageTask record);

    int insertSelective(BusOrderMessageTask record);

    BusOrderMessageTask selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BusOrderMessageTask record);

    int updateByPrimaryKey(BusOrderMessageTask record);
}