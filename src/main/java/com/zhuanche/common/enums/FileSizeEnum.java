package com.zhuanche.common.enums;

public enum FileSizeEnum {

    /**
     * 1M = 1024k= 1048576 byte
     */
    MByte(1048576,"1M")
    ;

    private long size;

    private String msg;

    FileSizeEnum(long size, String msg) {
        this.size = size;
        this.msg = msg;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
