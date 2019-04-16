package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;
import lombok.Data;

/**
 * @ClassName: BusDriverViolatorsQueryDTO
 * @Description: 违规司机处理查询DTO
 * @author: tianye
 * @date: 2019年03月25日 下午17:10:15
 *
 */
@Data
public class BusDriverViolatorsQueryDTO extends BaseDTO {

    private String busDriverName;//巴士司机姓名

    private String busDriverPhone;//巴士司机手机号

    private Integer cityId;//巴士司机所属城市id

    private Integer supplierId;//巴士司机所属供应商id

    private Short punishType;//处罚类型 1停运 2冻结

    private Short punishStatus;//处罚状态 0未处罚 1处罚生效
}
