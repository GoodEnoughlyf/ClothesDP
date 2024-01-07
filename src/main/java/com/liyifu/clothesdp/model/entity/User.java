package com.liyifu.clothesdp.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    private Long id;

    private String phone;

    private String password;

    private String nickName;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}