package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarBizTipsDoc;

public interface CarBizTipsDocMapper {
    int insert(CarBizTipsDoc record);

    int insertSelective(CarBizTipsDoc record);

    CarBizTipsDoc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizTipsDoc record);

    int updateByPrimaryKey(CarBizTipsDoc record);
}