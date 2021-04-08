package com.changgou.canal.mq.queue;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/5 0005 20:11
 */
@Configuration
public class TopicQueue {

    public static final String TOPIC_QUEUE_SPU = "topic.queue.spu";
    public static final String TOPIC_EXCHANGE_SPU = "topic.exchange.spu";

    /**
     * Topic模式 SPU变更队列
     * @return
     */
    @Bean
    public Queue topicQueueSpu() {
        return new Queue(TOPIC_QUEUE_SPU);
    }

    /***
     * SPU队列交换机
     * @return
     */
    @Bean
    public TopicExchange  topicSpuExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_SPU);
    }

    /***
     * 队列绑定交换机
     * @return
     */
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueueSpu()).to(topicSpuExchange()).with(TOPIC_QUEUE_SPU);
    }
}