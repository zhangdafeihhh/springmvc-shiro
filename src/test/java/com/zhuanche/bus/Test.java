
  
package com.zhuanche.bus;

import com.zhuanche.constants.financial.AdditionalServicesEnum;

/**  
 * ClassName:Test <br/>  
 * Date:     2019年4月23日 上午10:44:42 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class Test {
	public static void main(String[] args) {
		for (AdditionalServicesEnum additionalServicesEnum : AdditionalServicesEnum.values()) {
			System.out.println(additionalServicesEnum.getIndex());
			System.out.println(additionalServicesEnum.getServiceName());
		}
	}
}
  
