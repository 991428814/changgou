package com.changgou.seckill.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * 1.延时超时队列->负责数据暂时存储   Queue1
 * 2.真正监听的消息队列             Queue2
 * 3.创建交换机
 * @date 2021/1/7 0007 19:41
 */
@Configuration
public class QueueConfig {

    /***
     * 延时超时队列->负责数据暂时存储   Queue1
     */
    @Bean
    public Queue delaySeckillQueue(){
        return QueueBuilder.durable("delaySeckillQueue")
                .withArgument("x-dead-letter-exchange","seckillExchange")     //当前队列的消息一但过期则进入到死信交换机
                .withArgument("x-dead-letter-routing-key","seckillQueue")     //将死信队列的数据路由到指定的队列中
                .build();
    }


    /***
     * 真正监听的消息队列             Queue2
     */
    @Bean
    public Queue seckillQueue(){
        return new Queue("seckillQueue");
    }

    /***
     * 创建秒杀交换机
     */
    @Bean
    public Exchange seckillExchange(){
        return new DirectExchange("seckillExchange");
    }

    /***
     * 队列绑定交换机
     */
    @Bean
    public Binding seckillQueueBindingExchange(Queue seckillQueue,Exchange seckillExchange){
        return BindingBuilder.bind(seckillQueue).to(seckillExchange).with("seckillQueue").noargs();
    }
}