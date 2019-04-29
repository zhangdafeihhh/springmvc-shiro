package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCooperationType;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CarBizCooperationTypeExMapper {
    CarBizCooperationType queryForObject(CarBizCooperationType carBizCarGroup);

    /**
     * 查询所有加盟类型
     * @return
     */
    List<CarBizCooperationType> queryCarBizCooperationTypeList();

	/**
	 * @Title: queryNameById
	 * @Description: 查询加盟类型名称
	 * @param id
	 * @return String
	 * @throws
	 */
	String queryNameById(@Param("id") Integer id);
}