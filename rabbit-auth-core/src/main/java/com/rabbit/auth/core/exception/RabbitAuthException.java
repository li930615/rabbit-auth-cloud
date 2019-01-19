package com.rabbit.auth.core.exception;

/**
 * @ClassName RabbitAuthException
 * @Description 自定义异常
 * @Author LZQ
 * @Date 2019/1/19 20:20
 **/
public class RabbitAuthException extends RuntimeException {

    private static final long serialVersionUID = 42L;

    public RabbitAuthException(String msg)
    {
        super(msg);
    }

    public RabbitAuthException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RabbitAuthException(Throwable cause) {
        super(cause);
    }
}
