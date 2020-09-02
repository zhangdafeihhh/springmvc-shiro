package mapper.mdbcarmanage;


import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoAudit;

import java.util.List;

/**
 * 车辆审核信息查询
 * <p>
 * Created by jiamingku on 2020/7/30.
 */
public interface CarBizCarInfoAuditMapper {

    int insert(CarBizCarInfoAudit carBizCarInfoAudit);

    int insertBatch(List<CarBizCarInfoAudit> list);

    /**
     * 返回当前车辆审核记录信息,时间最新的就是当前车辆的审核状态
     *
     * @param carId 车辆临时表主键(carId)
     * @return 车辆审核信息
     */
    CarBizCarInfoAudit selectAuditStatusByCarTempId(Integer carId);

    List<CarBizCarInfoAudit> selectAuditStatusListByCarTempId(Integer carId);
}
