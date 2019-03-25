package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizSupplierTips;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarBizSupplierTipsExMapper {

    int createTips(CarBizSupplierTips record);


    int updateTips(CarBizSupplierTips record);


    int deleteTipsById(@Param("id")Integer id);


    List<CarBizSupplierTips> tipsList();


    CarBizSupplierTips selectByPrimaryKey(Integer id);


    int addReadCount(Integer id);



}