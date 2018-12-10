package com.zhuanche.common.web.fastjson.filter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * @ClassName: BigDecimalFilter
 * @Description: BigDecimal转换器
 * @author: yanyunpeng
 * @date: 2018年12月5日 上午10:34:17
 * 
 */
public class BigDecimalFilter implements ValueFilter {

	@Override
	public Object process(Object object, String name, Object value) {
		if (value != null && value instanceof BigDecimal) {
			if (new BigDecimal(0).equals(value)) {
				return "0.00";
			}
			DecimalFormat format = new DecimalFormat("##########.00");
			return format.format(value);
		}
		return value;
	}

}
