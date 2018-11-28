package com.zhuanche.vo.rentcar;

import java.io.Serializable;
import java.util.List;

/**
 * @program: mp-manage
 * @description: 存入excel导入信息的错误
 * @author: niuzilian
 * @create: 2018-11-28 15:28
 **/
public class ImportErrorVO implements Serializable{
    private int total;
    private int successNum;
    private int errorNum;
    private List<ErrorReason> reasons;

    public ImportErrorVO() {
    }

    public ImportErrorVO(int total, int successNum, int errorNum, List<ErrorReason> reasons) {
        this.total = total;
        this.successNum = successNum;
        this.errorNum = errorNum;
        this.reasons = reasons;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public int getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(int errorNum) {
        this.errorNum = errorNum;
    }

    public List<ErrorReason> getReasons() {
        return reasons;
    }

    public void setReasons(List<ErrorReason> reasons) {
        this.reasons = reasons;
    }
}
