package com.bootdo.meituan.banma.response;

/**
 * 抽象响应父类
 */
public abstract class AbstractResponse {
    protected String code;
    protected String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
