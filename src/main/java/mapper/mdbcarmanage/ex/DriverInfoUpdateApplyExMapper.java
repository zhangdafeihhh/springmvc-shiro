package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.DriverInfoUpdateApplyDTO;

import java.util.List;

public interface DriverInfoUpdateApplyExMapper {

    /**
     * 司机\车辆修改申请信息列表
     * @param driverInfoUpdateApplyDTO
     * @return
     */
    List<DriverInfoUpdateApplyDTO> queryDriverInfoUpdateList(DriverInfoUpdateApplyDTO driverInfoUpdateApplyDTO);

}