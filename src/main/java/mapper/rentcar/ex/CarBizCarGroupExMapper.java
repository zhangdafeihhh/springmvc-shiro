package mapper.rentcar.ex;


import com.zhuanche.entity.rentcar.CarBizCarGroup;

/**
 * @author wzq
 */
public interface CarBizCarGroupExMapper {
    /**
     * 根据groupId查询
     * @param carBizCarGroup
     * @return
     */
    CarBizCarGroup queryForObject(CarBizCarGroup carBizCarGroup);

    CarBizCarGroup queryForObjectByGroupName(CarBizCarGroup carBizCarGroup);
}