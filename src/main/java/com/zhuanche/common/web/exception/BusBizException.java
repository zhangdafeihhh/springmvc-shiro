package com.zhuanche.common.web.exception;

/**
 * @ClassName: BusBizException
 * @Description: 巴士异常类
 * @author: yanyunpeng
 * @date: 2018年11月22日 下午3:29:16
 * 
 */
public class BusBizException extends Exception {

	private static final long serialVersionUID = 7110449348647018113L;

	public BusBizException() {
		super();
	}

	public BusBizException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public BusBizException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BusBizException(String arg0) {
		super(arg0);
	}

	public BusBizException(Throwable arg0) {
		super(arg0);
	}

}
