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

    CarBizCarInfoAudit selectAuditStatusByCarTempId(Integer carId);
}
