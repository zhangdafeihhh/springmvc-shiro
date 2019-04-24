package com.zhuanche.dto.financial;

import java.math.BigDecimal;

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
	private String goodsName;
	private Byte salesTarget;
	private Byte goodsType;
	private Integer supplierId;
	private Integer cityId;
	private String channel;	
	private String reason;
	private String explain;
	private String pictureUrl;
	private String keyword;
	private Integer vehicleAge;
	private Integer mileage;
	private Integer vehicleProperties;
	private String color;
	private Integer basicsVehiclesId;
	private Integer stock;
	
	private Integer sourceFundsId;
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
  
