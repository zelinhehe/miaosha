package com.miaoshaproject.error;

public enum EmBusinessError implements CommonError {

    // 10000开头的为通用的错误定义.其中errMsg可被重置 by setErrMsg，例如重置为'邮箱格式错误'
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    // 未知错误
    UNKNOWN_ERROR(10002, "未知错误"),

    // 20000开头的为用户模块的错误定义
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_ERROR(20002, "请输入正确的用户名或密码");

    private int errCode;
    private String errMsg;

    EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
