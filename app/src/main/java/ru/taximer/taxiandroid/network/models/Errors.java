package ru.taximer.taxiandroid.network.models;

public class Errors {
    int errCode;
    String errMessage;
    String errData;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrData() {
        return errData;
    }

    public void setErrData(String errData) {
        this.errData = errData;
    }

}
