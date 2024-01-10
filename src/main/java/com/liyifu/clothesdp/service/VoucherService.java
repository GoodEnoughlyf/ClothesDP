package com.liyifu.clothesdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liyifu.clothesdp.model.entity.Voucher;

import java.util.List;

/**
* @author liyifu
* @description 针对表【voucher】的数据库操作Service
* @createDate 2024-01-07 10:47:02
*/
public interface VoucherService extends IService<Voucher> {

    /**
     * 根据商铺id查询优惠劵（包括秒杀劵）
     * @param shopId
     * @return
     */
    List<Voucher> queryVoucherOfShop(Long shopId);

    /**
     * 添加秒杀劵
     * @param voucher
     */
    Long addSeckillVoucher(Voucher voucher);

}
