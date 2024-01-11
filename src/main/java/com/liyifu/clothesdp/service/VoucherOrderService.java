package com.liyifu.clothesdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liyifu.clothesdp.model.entity.SeckillVoucher;
import com.liyifu.clothesdp.model.entity.VoucherOrder;

/**
* @author liyifu
* @description 针对表【voucher_order】的数据库操作Service
* @createDate 2024-01-07 10:47:02
*/
public interface VoucherOrderService extends IService<VoucherOrder> {

    /**
     * 秒杀劵下单
     * @param voucherId
     */
    Long secSkillVoucherOrder(Long voucherId);

    /**
     * 创建订单
     * @param voucherId
     * @param seckillVoucher
     * @param stock
     * @return
     */
    Long createOrder(Long voucherId, SeckillVoucher seckillVoucher, Integer stock)
}
