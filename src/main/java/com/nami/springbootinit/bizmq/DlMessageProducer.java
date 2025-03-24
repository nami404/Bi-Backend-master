package com.nami.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nami404
 * * @date 2025/3/3 13:35
 */
@Component
public class DlMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(DlMqConstant.DL_EXCHANGE_NAME, DlMqConstant.DL_ROUTING_KEY, message);
    }
}
