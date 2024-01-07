package com.liyifu.clothesdp.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName blog
 */
@TableName(value ="blog")
@Data
public class Blog implements Serializable {
    private Long id;

    private Long shopId;

    private Long userId;

    private String title;

    private String content;

    private Integer liked;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}