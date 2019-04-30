package com.zhuanche.common.rpc;

import com.alibaba.fastjson.JSON;

import java.util.Date;

public final class DriverIncomeScoreResponse {
    private int code;//错误码  0为成功
    private int status;//错误码  0为成功
    private String msg;//错误信息
    private Date time; //响应时间
    private Object data;//具体的业务数据
    private Object page;

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

    public Object getPage() {
        return page;
    }

    public void setPage(Object page) {
        this.page = page;
    }
    //----------------------------------------------------------------------方便的转换方法
    /**当返回的属性比较少时，用这个比较方便（data是JSONObject）**/
    public static DriverIncomeScoreResponse parse(String jsonString ) {
        DriverIncomeScoreResponse resp = JSON.parseObject(jsonString, DriverIncomeScoreResponse.class );
        return resp;
    }
    /**当返回的属性非常多时，建议将属性封装成普通JAVA对象（data是自定义的dataClz类的实例），此时用这个比较方便**/
    public static <T> DriverIncomeScoreResponse parseObject(String jsonString , Class<T> dataClz ) {
        DriverIncomeScoreResponse resp = parse(jsonString);
        if(resp!=null && resp.getData()!=null) {
            T  data	 = 	JSON.parseObject( JSON.toJSONString(resp.getData()),  dataClz  );
            resp.setData( data );
        }
        return resp;
    }

}
