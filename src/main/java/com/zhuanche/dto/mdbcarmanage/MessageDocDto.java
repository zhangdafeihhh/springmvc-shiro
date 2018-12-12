package com.zhuanche.dto.mdbcarmanage;

/**
 * @Author fanht
 * @Description
 * @Date 2018/11/26 下午3:03
 * @Version 1.0
 */
public class MessageDocDto {

    private String docName;

    private String docUrl;

    public MessageDocDto(String docName, String docUrl) {
        this.docName = docName;
        this.docUrl = docUrl;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    @Override
    public String toString() {
        return "MessageDocDto{" +
                "docName='" + docName + '\'' +
                ", docUrl='" + docUrl + '\'' +
                '}';
    }
}
