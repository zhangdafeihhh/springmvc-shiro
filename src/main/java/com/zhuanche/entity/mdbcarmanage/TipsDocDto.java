package com.zhuanche.entity.mdbcarmanage;

/**
 * @Author fanht
 * @Description
 * @Date 2019/3/24 下午5:43
 * @Version 1.0
 */
public class TipsDocDto {

    private Integer docId;

    private String docName;

    private String docUrl;

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

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public TipsDocDto(Integer docId, String docName, String docUrl) {
        this.docId = docId;
        this.docName = docName;
        this.docUrl = docUrl;
    }

    @Override
    public String toString() {
        return "CarBizDocDto{" +
                "docName='" + docName + '\'' +
                ", docUrl='" + docUrl + '\'' +
                '}';
    }
}
