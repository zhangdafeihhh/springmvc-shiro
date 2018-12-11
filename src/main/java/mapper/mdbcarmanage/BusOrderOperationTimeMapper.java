package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.BusOrderOperationTime;

public interface BusOrderOperationTimeMapper {
    int insert(BusOrderOperationTime record);

    int insertSelective(BusOrderOperationTime record);

    BusOrderOperationTime selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BusOrderOperationTime record);

    int updateByPrimaryKey(BusOrderOperationTime record);
}