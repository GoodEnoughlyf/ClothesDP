package com.liyifu.clothesdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesdp.model.entity.SeckillVoucher;
import com.liyifu.clothesdp.model.entity.Voucher;
import com.liyifu.clothesdp.mapper.VoucherMapper;
import com.liyifu.clothesdp.service.SeckillVoucherService;
import com.liyifu.clothesdp.service.VoucherService;
import com.liyifu.clothesdp.utils.RedisConstants;
import io.swagger.models.auth.In;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.liyifu.clothesdp.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
* @author liyifu
* @description 针对表【voucher】的数据库操作Service实现
* @createDate 2024-01-07 10:47:02
*/
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher>
    implements VoucherService {

    @Resource
    private SeckillVoucherService seckillVoucherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据商铺id查询优惠劵（包括秒杀劵）
     *
     * @param shopId
     * @return
     */
    @Override
    public List<Voucher> queryVoucherOfShop(Long shopId) {
        QueryWrapper<Voucher> voucherQueryWrapper=new QueryWrapper<>();
        voucherQueryWrapper.eq("shop_id",shopId);
        List<Voucher> voucherList = this.list(voucherQueryWrapper);
        return voucherList;
    }

    /**
     * 添加秒杀劵
     * @param voucher
     */
    @Override
    public Long addSeckillVoucher(Voucher voucher) {
        //秒杀劵也是优惠劵
        this.save(voucher);

        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);

        //因为是秒杀劵，很多用户会抢，因此是高并发场景，将其放入redis
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY+voucher.getId(),seckillVoucher.getStock().toString());

        return voucher.getId();
    }
}




