package com.zhuanche.dto.busManage;

import com.zhuanche.common.web.datavalidate.custom.InArray;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * bus_biz_driver_violators
 */
@Data
public class BusDriverViolatorsSaveDTO extends BusBaseStatisDTO {


	/** 手机号 **/
	@NotBlank(message = "司机手机号不能为空")
	@Length(min = 11, max = 11, message = "司机手机号必须为11位")
	private String busDriverPhone;

	/** 司机姓名 **/
	@Length(max = 20, message = "司机姓名长度不能超过20")
	private String busDriverName;

	/** 停运说明 **/
	@NotBlank(message = "停运说明不能为空")
	@Length(max = 120, message = "停运说明长度不能超过20")
	private String punishReason;

	/** 停运开始时间 **/
	@NotNull(message = "停运开始时间不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date punishStartTime;

	/** 停运时长 **/
	@NotNull(message = "停运时长不能为空")
	@Min(value=1,message = "停运时长不能小于1")
	private Integer punishDuration;

	/** 处罚状态非必填[1.停运 2.冻结] **/
	@InArray(values = { "1" }, message = "处罚状态不在有效范围内[1.停运]")
	private short punishType;
}