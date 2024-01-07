package com.liyifu.clothesdp.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName shop_type
 */
@TableName(value ="shop_type")
@Data
public class ShopType implements Serializable {
    private Long id;

    private String name;

    private Integer sort;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}