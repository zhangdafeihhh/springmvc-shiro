package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizTipsDoc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarBizTipsDocExMapper {
    int insertTipsDoc(CarBizTipsDoc record);

    int deleteByDocId(@Param("id")Integer id);

    List<CarBizTipsDoc> listTipsDoc(@Param("tipsId")Long tipsId);


    int deleteAllDocByTipsId(@Param("tipsId")Integer tipsId);

    /**
     * 删除tips
     * @param id
     * @return
     */
    int updateStatus(@Param("id")Integer id);



    CarBizTipsDoc selectByPrimaryKey(Integer id);

    /*int insertSelective(CarBizTipsDoc record);

    CarBizTipsDoc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizTipsDoc record);

    int updateByPrimaryKey(CarBizTipsDoc record);*/
}