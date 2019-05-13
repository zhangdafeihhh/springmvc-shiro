package mapper.mdbcarmanage.ex;


import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersionDetail;
/**
 * @Author: nysspring@163.com
 * @Description:
 * @Date: 18:44 2019/5/13
 */
public interface CarBizSaasVersionDetailExMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(CarBizSaasVersionDetail record);

    int insertSelective(CarBizSaasVersionDetail record);

    CarBizSaasVersionDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizSaasVersionDetail record);

    int updateByPrimaryKey(CarBizSaasVersionDetail record);

}
