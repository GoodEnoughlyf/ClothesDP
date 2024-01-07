package com.liyifu.clothesdp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录信息dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFormDTO implements Serializable {
    private String phone;

    private String code;
}
