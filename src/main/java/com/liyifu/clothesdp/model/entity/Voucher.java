package com.liyifu.clothesdp.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName voucher
 */
@TableName(value ="voucher")
@Data
public class Voucher implements Serializable {
    private Long id;

    private Long shopId;

    private String title;

    private Long payValue;

    private Long actualValue;

    private Integer type;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}