package com.zhuanche.dto.financial;

import com.zhuanche.constants.financial.FinancialConst.ClueStatus;
import com.zhuanche.entity.driver.FinancialClue;

/**  
 * ClassName:FinancialClueDTO <br/>  
 * Date:     2019年4月24日 上午11:57:49 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class FinancialClueDTO extends FinancialClue{
	private String statusName;
	private String whetherName;
	public String getStatusName() {
		if (getStatus()==ClueStatus.PROCESSED) {
			statusName="已分发";
		}
		if (getStatus()==ClueStatus.UNTREATED) {
			statusName="待分发";
		}
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getWhetherName() {
		if (getWhether()==0) {
			whetherName="否";
		}
		if (getWhether()==1) {
			whetherName="是";
		}
		return whetherName;
	}
	public void setWhetherName(String whetherName) {
		this.whetherName = whetherName;
	}
	
	
}
  
