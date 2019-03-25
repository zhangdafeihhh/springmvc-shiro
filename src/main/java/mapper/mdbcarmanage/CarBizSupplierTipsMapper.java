package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarBizSupplierTips;

public interface CarBizSupplierTipsMapper {
    int insert(CarBizSupplierTips record);

    int insertSelective(CarBizSupplierTips record);

    CarBizSupplierTips selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizSupplierTips record);

    int updateByPrimaryKey(CarBizSupplierTips record);
}