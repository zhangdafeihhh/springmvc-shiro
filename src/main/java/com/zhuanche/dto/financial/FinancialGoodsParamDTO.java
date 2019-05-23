package com.zhuanche.dto.financial;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.zhuanche.common.web.datavalidate.sequence.*;

import lombok.Data;

/**  
 * ClassName:FinancialGoodsParamDTO <br/>  
 * Date:     2019年4月24日 上午10:43:15 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Data
public class FinancialGoodsParamDTO {
	private Integer goodsId;
	@NotBlank(groups=Seq0.class,message = "商品名称不能为空")
	@Length(groups=Seq1.class,max = 20, message = "商品名称不能最长不能超过20")
	private String goodsName;
	
	@NotNull(groups=Seq2.class,message = "销售对象不能为空")
	private Byte salesTarget;
	
	@NotNull(groups=Seq3.class,message = "商品类型不能为空")
	private Byte goodsType;
	@NotNull(groups=Seq4.class,message = "供应商不能为空")
	private Integer supplierId;
	@NotNull(groups=Seq5.class,message = "城市不能为空")
	private Integer cityId;
	@NotNull(groups=Seq6.class,message = "渠道不能为空")
	private Integer channelId;	
	private String reason;
	private String expInfo;
	@NotBlank(groups=Seq7.class,message = "图片地址不能为空")
	@Length(groups=Seq8.class,max = 512, message = "图片最多不能超过5张")
	private String pictureUrl;
	@Length(groups=Seq9.class,max = 256, message = "关键字最多不能超过256个字符")
	private String keyword;
	@NotNull(groups=Seq10.class,message = "车龄不能为空")
	private Integer vehicleAge;
	@NotNull(groups=Seq11.class,message = "里程不能为空")
	private Integer mileage;
	@NotNull(groups=Seq12.class,message = "车辆性质不能为空")
	private Integer vehicleProperties;
	
	private String color;
	@NotNull(groups=Seq13.class,message = "车型车款不能为空")
	private Integer basicsVehiclesId;
	@NotNull(groups=Seq14.class,message = "销售库存不能为空")
	private Integer stock;
	
	private Integer sourceFundsId;
	@Min(value = 1, message = "贷款期限不能小于1")
	private Integer leaseTerm;
	
	private BigDecimal rentEveryTerm;
	private BigDecimal firstRent;
	private BigDecimal securityDeposit;
	private BigDecimal totalPrice;
	private BigDecimal frontMoney;
	
	
	private String additionalServicesId;
	private String additionalServicesInfo;
	
	
	private String additionalClause;
}
  
