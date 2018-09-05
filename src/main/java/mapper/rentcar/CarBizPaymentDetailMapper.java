package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizPaymentDetail;

public interface CarBizPaymentDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarBizPaymentDetail record);

    int insertSelective(CarBizPaymentDetail record);

    CarBizPaymentDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarBizPaymentDetail record);

    int updateByPrimaryKey(CarBizPaymentDetail record);
}