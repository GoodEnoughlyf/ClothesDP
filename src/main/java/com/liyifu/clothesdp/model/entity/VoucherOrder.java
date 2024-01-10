package com.liyifu.clothesdp.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @TableName voucher_order
 */
@TableName(value ="voucher_order")
@Data
public class VoucherOrder implements Serializable {
    private Long id;

    private Long userId;

    private Long voucherId;

    private Integer payType;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}