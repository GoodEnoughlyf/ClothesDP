package com.liyifu.clothesdp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用返回类
 */
@Data
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {
    private int code;

    private T data;

    private String message;

}
