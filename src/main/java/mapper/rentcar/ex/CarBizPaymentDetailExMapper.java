package mapper.rentcar.ex;

import com.zhuanche.dto.rentcar.CarBizPaymentDetailDTO;

public interface CarBizPaymentDetailExMapper {

    int insert(CarBizPaymentDetailDTO record);

    int updateByExternalRefNumber(CarBizPaymentDetailDTO record);
}