package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizSupplierTips;
import com.zhuanche.entity.mdbcarmanage.CarBizSupplierTipsDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarBizSupplierTipsDtoExMapper {


    /**
     * 列表
     * @return
     */
    List<CarBizSupplierTipsDto> listTips();


    /**
     * 查询所有符合条件的数据
     * @param keyword
     * @param startDate
     * @param endDate
     * @param list
     * @return
     */
    List<CarBizSupplierTipsDto> searchTipsAndDoc(@Param("keyword") String keyword,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate,
                                             @Param("idList") List<Integer> idList);


    List<CarBizSupplierTipsDto> searchTips(@Param("keyword") String keyword,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate,
                                             @Param("idList") List<Integer> idList);


    List<CarBizSupplierTipsDto> searchDoc(@Param("keyword") String keyword,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate,
                                             @Param("idList") List<Integer> idList);


}