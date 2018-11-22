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

	public BusBizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BusBizException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusBizException(String message) {
		super(message);
	}

	public BusBizException(Throwable cause) {
		super(cause);
	}

}
