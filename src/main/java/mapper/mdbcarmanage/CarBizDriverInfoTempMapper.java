package mapper.mdbcarmanage;


import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;

public interface CarBizDriverInfoTempMapper {

    /**
     * 删除
     * @param driverId
     * @return
     */
    int deleteByPrimaryKey(Integer driverId);

    /**
     * 新增
     * @param record
     * @return
     */
    int insertSelective(CarBizDriverInfoTemp record);

    /**
     * 查询
     * @param driverId
     * @return
     */
    CarBizDriverInfoTemp selectByPrimaryKey(Integer driverId);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(CarBizDriverInfoTemp record);

}