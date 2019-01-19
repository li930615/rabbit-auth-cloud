package com.rabbit.auth.core.entity;

import java.io.Serializable;

/**
 * @ClassName R
 * @Description 暂时没搞懂是做什么的
 * @Author LZQ
 * @Date 2019/1/19 16:55
 **/
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int NO_LOGIN = -1;
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;
    public static final int NO_PERMISSION = 2;
    private String msg = "success";
    private int code = 0;
    private T data;

    public R() {
    }

    public R(T data) {
        this.data = data;
    }

    public R(Throwable throwable) {
        this.msg = throwable.getMessage();
        this.code = 1;
    }

    public R(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public R(String msg, int code, T data) {
        this();
        this.data = data;
    }

    public boolean success(){
        return 0 == this.code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
