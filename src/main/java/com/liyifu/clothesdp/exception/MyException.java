package com.liyifu.clothesdp.exception;

import lombok.Data;

/**
 * 自定义异常封装类
 */
@Data
public class MyException extends RuntimeException{
    /**
     * 错误码
     */
    private final int code;

    public MyException(int code,String message){
        super(message);
        this.code=code;
    }
}
