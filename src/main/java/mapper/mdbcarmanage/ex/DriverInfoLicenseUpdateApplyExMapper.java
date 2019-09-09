package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.mdbcarmanage.DriverInfoLicenseUpdateApplyDTO;

import java.util.List;

public interface DriverInfoLicenseUpdateApplyExMapper {

    /**
     * 司机换车换牌列表查询
     * @param licenseUpdateApplyDTO
     * @return
     */
    List<DriverInfoLicenseUpdateApplyDTO> queryDriverInfoLicenseUpdateList(DriverInfoLicenseUpdateApplyDTO licenseUpdateApplyDTO);

}