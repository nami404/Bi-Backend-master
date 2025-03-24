package com.nami.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


/**
 * @author nami404
 * * @date 2025/3/3 13:38
 */
@Component
@Slf4j
public class DlMessageConsumer {

    @SneakyThrows
    @RabbitListener(queues = {DlMqConstant.DL_QUEUE}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.error("Received a dead letter message: {}", message);

        // 日志记录，消息无用丢弃处理，手动确认消息
        channel.basicAck(deliveryTag, false);
    }
}
