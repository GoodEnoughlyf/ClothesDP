package com.liyifu.clothesdp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置类
 */
@Configuration
public class RabbitMQConfig {
    /**
     * 定义了一个名为 myQueue 的队列
     * @return
     */
    @Bean
    public Queue myQueue(){
        return new Queue("myQueue");
    }

    /**
     * 定义一个名为 myExchange 的直接交换器
     * @return
     */
    @Bean
    public DirectExchange myExchange(){
        return new DirectExchange("myExchange");
    }

    /**
     * 并绑定它们，使得交换器接收到路由键为 myRoutingKey 的消息时，会将消息路由到 myQueue
     * @param myQueue
     * @param myExchange
     * @return
     */
    @Bean
    public Binding binding(Queue myQueue,DirectExchange myExchange){
        return BindingBuilder.bind(myQueue).to(myExchange).with("myRoutingKey");
    }
}
