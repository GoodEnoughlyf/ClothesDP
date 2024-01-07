package com.liyifu.clothesdp.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName seckill_voucher
 */
@TableName(value ="seckill_voucher")
@Data
public class SeckillVoucher implements Serializable {
    private Long voucherId;

    private Integer stock;

    private Date createTime;

    private Date beginTime;

    private Date endTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}