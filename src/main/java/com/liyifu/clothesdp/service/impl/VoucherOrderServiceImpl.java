package com.liyifu.clothesdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesdp.exception.MyException;
import com.liyifu.clothesdp.model.entity.SeckillVoucher;
import com.liyifu.clothesdp.model.entity.VoucherOrder;
import com.liyifu.clothesdp.mapper.VoucherOrderMapper;
import com.liyifu.clothesdp.model.vo.UserVO;
import com.liyifu.clothesdp.service.SeckillVoucherService;
import com.liyifu.clothesdp.service.VoucherOrderService;
import com.liyifu.clothesdp.utils.RedisConstants;
import com.liyifu.clothesdp.utils.UserThreadLocal;
import io.swagger.models.auth.In;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.liyifu.clothesdp.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
 * @author liyifu
 * @description 针对表【voucher_order】的数据库操作Service实现
 * @createDate 2024-01-07 10:47:02
 */

@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder>
        implements VoucherOrderService {
    @Resource
    private SeckillVoucherService seckillVoucherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 购买秒杀劵
     *
     * @param voucherId
     * @return
     */
    @Override
    public Long secSkillVoucherOrder(Long voucherId) {
        //1、查询优惠劵信息
        QueryWrapper<SeckillVoucher> seckillVoucherQueryWrapper = new QueryWrapper<>();
        seckillVoucherQueryWrapper.eq("voucher_id", voucherId);
        SeckillVoucher seckillVoucher = seckillVoucherService.getOne(seckillVoucherQueryWrapper);

        //2、活动未开始，返回异常
        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
            throw new MyException(401, "活动未开始！");
        }

        //3、活动已结束，返回异常
        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
            throw new MyException(401, "活动已结束!");
        }

        //4、判断库存是否充足
        String jsonValue = stringRedisTemplate.opsForValue().get(SECKILL_STOCK_KEY + voucherId);
        Integer stock = Integer.valueOf(jsonValue);
        // 不存足，返回异常
        if (stock < 1) {
            throw new MyException(401, "库存不足！");
        }

        //封装的创建订单方法
        UserVO userVO = UserThreadLocal.getUser();
        Long orderId = null;
        //创建Redisson分布式锁
        RLock lock = redissonClient.getLock("lock:order:" + userVO.getId());
        //获取锁
        boolean isLock = lock.tryLock();
        //获取锁失败
        if(!isLock){
            throw new MyException(401,"一人只能抢购一张秒杀劵！");
        }

        try{
            Object o = AopContext.currentProxy();
            VoucherOrderService proxy = (VoucherOrderService) o;
            orderId = proxy.createOrder(voucherId, seckillVoucher, stock);
            return orderId;
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            //释放锁
            lock.unlock();
        }
    }

    /**
     * 对判断用户是否购买、扣减库存、创建订单进行封装
     * 因为这三个动作的目的都是新增订单，且操作一致
     */
    @Transactional
    public Long createOrder(Long voucherId, SeckillVoucher seckillVoucher, Integer stock) {
        //5、库存充足，使用乐观锁的CAS法更新库存
        //实现一人一单，判断订单是否存在
        UserVO userVO = UserThreadLocal.getUser();
        QueryWrapper<VoucherOrder> voucherOrderQueryWrapper = new QueryWrapper<>();
        voucherOrderQueryWrapper.eq("voucher_id", voucherId).eq("user_id", userVO.getId());
        VoucherOrder order = this.getOne(voucherOrderQueryWrapper);
        if (order != null) {
            throw new MyException(401, "一人只能购买一张秒杀劵！");
        }

        //5、1 扣件库存
        SeckillVoucher newSeckillVoucher = new SeckillVoucher();
        BeanUtil.copyProperties(seckillVoucher, newSeckillVoucher);
        Integer newStock = stock - 1;
        newSeckillVoucher.setStock(newStock);
        //5、2 更新库存前，需要判断查询到的数据是否被修改
        QueryWrapper<SeckillVoucher> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("stock", 0);
        boolean success = seckillVoucherService.update(newSeckillVoucher, queryWrapper);
        //5、3 如果不满足乐观锁的要求，则返回异常
        if (!success) {
            throw new MyException(401, "库存不足！");
        }
        //5、4 更新缓存
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucherId, newStock.toString());

        //6、创建新线程创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setUserId(userVO.getId());
        this.save(voucherOrder);

        return voucherOrder.getId();
    }
}




