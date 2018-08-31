package com.zhuanche.entity.rentcar;

public class DriverOutageVo extends DriverOutage{

    /**
     * serialVersionUID
     * @since JDK 1.6
     */
    private static final long serialVersionUID = 1L;

    private String fileName;//批量导入上传文件名称

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }



}
