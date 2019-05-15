package com.zhuanche.common.exception;

/**
 * @Author: nysspring@163.com
 * @Description: 自定义异常错误码
 * @Date: 14:26 2019/5/14
 */
public enum ExceptionCode {

    GLOBAL_EXCEPTION(0000, "程序异常"),
    GLOBAL_PARAM_ERROR(0001, "参数错误"),
    DB_OPTION_ERROR(0002,"数据库操作异常"),
    VERSION_DETAIL_OPTION_ERROR(0003,"版本记录附件操作异常");



    private Integer index;
    private String errMsg;

    private ExceptionCode(){
        this.index = 0000;
        this.errMsg = null;
    }

    private ExceptionCode(final int code, final String errMsg){
        this.index = code;
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public Integer getIndex() {
        // TODO Auto-generated method stub
        return index;
    }





}
