package com.zhuanche.util.dateUtil;

/**
 * <p>业务异常基类</p>
 * 
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * <PRE>
 * 
 * @author lunan
 * @since 1.0
 * @version 1.0
 */
public class BusinessException extends BaseException {

	private static final long serialVersionUID = 3003915267325733689L;
	
	/**
	 * 构造器
	 */
	public BusinessException() {
		super();
	}

	/**
	 * 构造器
	 * @param message	异常详细信息
	 * @param cause	异常原因
	 */
	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 构造器
	 * @param message	异常详细信息
	 */
	public BusinessException(String message) {
		super(message);
	}

	/**
	 * 构造器
	 * @param cause	异常原因
	 */
	public BusinessException(Throwable cause) {
		super(cause);
	}

}
