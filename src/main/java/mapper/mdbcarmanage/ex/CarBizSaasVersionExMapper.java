package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: nysspring@163.com
 * @Description:
 * @Date: 18:44 2019/5/13
 */
public interface CarBizSaasVersionExMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(CarBizSaasVersion record);

    int insertSelective(CarBizSaasVersion record);

    CarBizSaasVersion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizSaasVersion record);

    List<CarBizSaasVersion> listVersion();

    List<Map<String,String>> selectContactsSendMsg(@Param("cityId") Integer cityId);

}
