//package com.liyifu.clothesdp.controller;
//
//import com.liyifu.clothesdp.rabbitmq.RabbitMQProducer;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//
///**
// * 测试 RabbitMQ
// */
//@RestController
//@RequestMapping("/test")
//public class TestRabbitMQ {
//    @Resource
//    private RabbitMQProducer rabbitMQProducer;
//
//    @PostMapping("/send")
//    public String sendMessage(@RequestParam("message") String message){
//        rabbitMQProducer.send("myExchange","myRoutingKey",message);
//        return "消息发送成功！";
//    }
//}
