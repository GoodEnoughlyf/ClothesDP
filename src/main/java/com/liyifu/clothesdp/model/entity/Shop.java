package com.liyifu.clothesdp.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName shop
 */
@TableName(value ="shop")
@Data
public class Shop implements Serializable {
    private Long id;

    private String name;

    private Long typeId;

    private String address;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}