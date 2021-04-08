package com.changgou.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import entity.SeckillStatus;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2021/1/5 0005 19:11
 */
@Component
@RabbitListener(queues = "seckillQueue")
public class DelaySecKillMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * 消息监听
     * @param message
     */
    @RabbitHandler
    public void getMessage(String message){
        try {
            //获取用户的排队信息
            SeckillStatus seckillStatus = JSON.parseObject(message,SeckillStatus.class);

            //如果此时Redis中没有用户的排队信息，则表明该订单已经处理，如果有排队信息，则表明用户尚未完成支付，关闭订单(关闭微信支付)
            Object userQueueStatus = redisTemplate.boundHashOps("UserQueueStatus").get(seckillStatus.getUsername());
            if(userQueueStatus!=null){
                //关闭微信支付

                //删除订单
                seckillOrderService.deleteOrder(seckillStatus.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}