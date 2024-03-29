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
import com.liyifu.clothesdp.rabbitmq.RabbitMQProducer;
import com.liyifu.clothesdp.service.SeckillVoucherService;
import com.liyifu.clothesdp.service.VoucherOrderService;
import com.liyifu.clothesdp.utils.RedisConstants;
import com.liyifu.clothesdp.utils.UserThreadLocal;
import io.swagger.models.auth.In;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;

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

    @Resource
    private RabbitMQProducer rabbitMQProducer;

    //防止每一次释放锁都需要利用io流读取lua脚本，于是将lua脚本放入静态代码块中初始化
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

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
        if (!isLock) {
            throw new MyException(401, "一人只能抢购一张秒杀劵！");
        }

        try {
            Object o = AopContext.currentProxy();
            VoucherOrderService proxy = (VoucherOrderService) o;
            orderId = proxy.createOrder(voucherId, seckillVoucher, stock);
            return orderId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
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

    /**
     * 利用rabbitMq异步购买秒杀劵
     *
     * @param voucherId
     * @return
     */
    @Override
    public Long secSkillVoucherRabbitMQOrder(Long voucherId) {
        //获取用户
        Long userId = UserThreadLocal.getUser().getId();
        //1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(), //这里是key数组，没有key，就传的一个空集合
                voucherId.toString(), userId.toString()
        );
        //2.判断结果是0
        int r = result.intValue();//Long型转为int型，便于下面比较
        if (r != 0) {
            //2.1 不为0，代表没有购买资格
            throw new MyException(401,r==1?"优惠劵售卖完":"不能重复购买");
        }
        //2.2 为0，有购买资格，把下单信息保存到阻塞队列中
        //7.创建订单   向订单表新增一条数据，除默认字段，其他字段的值需要set
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(voucherOrder.getId());
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        //放入消息队列中
        rabbitMQProducer.send("myExchange","myRoutingKey",voucherOrder);

        //3.返回订单id
        return voucherOrder.getId();
    }



}




