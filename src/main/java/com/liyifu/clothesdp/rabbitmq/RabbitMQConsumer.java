package com.liyifu.clothesdp.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liyifu.clothesdp.model.entity.SeckillVoucher;
import com.liyifu.clothesdp.model.entity.Voucher;
import com.liyifu.clothesdp.model.entity.VoucherOrder;
import com.liyifu.clothesdp.service.SeckillVoucherService;
import com.liyifu.clothesdp.service.VoucherOrderService;
import com.liyifu.clothesdp.service.VoucherService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息消费者
 */
@Component
public class RabbitMQConsumer {
    @Resource
    private VoucherOrderService voucherOrderService;
    
    @Resource
    private SeckillVoucherService seckillVoucherService;
    /**
     * @RabbitListener 注解，并指定了监听的队列名。当接收到消息时，handleMessage 方法将被自动调用。
     * @param order
     */
    @RabbitListener(queues = "myQueue")
    public void handleMessage(VoucherOrder order){
        System.out.println("开始创建订单！!");
        Long voucherId = order.getVoucherId();
        //通过订单拿到秒杀劵
        QueryWrapper<SeckillVoucher> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("voucher_id",voucherId);
        SeckillVoucher seckillVoucher = seckillVoucherService.getOne(queryWrapper);
        seckillVoucher.setStock(seckillVoucher.getStock()-1);
        seckillVoucherService.updateById(seckillVoucher);
        voucherOrderService.save(order);
    }
}
