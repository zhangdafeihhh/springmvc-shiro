package mapper.driver;

import com.zhuanche.entity.driver.TwoLevelCooperationDto;

public interface TwoLevelCooperationDtoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TwoLevelCooperationDto record);

    int insertSelective(TwoLevelCooperationDto record);

    TwoLevelCooperationDto selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TwoLevelCooperationDto record);

    int updateByPrimaryKey(TwoLevelCooperationDto record);
}