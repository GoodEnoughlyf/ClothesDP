package com.liyifu.clothesdp.rabbitmq;

import com.liyifu.clothesdp.model.entity.VoucherOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息生产者
 */
@Component
public class RabbitMQProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param exchange 指定交换机
     * @param routingKey 路由键
     */
    public void send(String exchange, String routingKey, VoucherOrder order){
        rabbitTemplate.convertAndSend(exchange,routingKey,order);
    }
}
