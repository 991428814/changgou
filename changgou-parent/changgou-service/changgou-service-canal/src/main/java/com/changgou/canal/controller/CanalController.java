package com.changgou.canal.controller;

import com.changgou.canal.mq.send.TopicMessageSender;
import entity.Message;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/6 0006 21:07
 */
@RestController
@RequestMapping("/canal")
@CrossOrigin
public class CanalController {

    TopicMessageSender topicMessageSender = new TopicMessageSender();

    @RequestMapping("/sendTopic")
    public String sendTopic(Message message) {
        topicMessageSender.sendMessage(message);
        return "发送成功...";
    }
}