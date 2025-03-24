package com.nami.springbootinit.bizmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nami404
 * * @date 2025/3/3 14:42
 */
@Configuration
public class RabbitmqConfig {

    // 死信队列
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DlMqConstant.DL_EXCHANGE_NAME);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(DlMqConstant.DL_QUEUE)
                .build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DlMqConstant.DL_ROUTING_KEY);
    }


    // 业务队列
    @Bean
    public DirectExchange biDirectExchange() {
        return new DirectExchange(BiMqConstant.BI_EXCHANGE_NAME);
    }

    @Bean
    public Queue biDirectQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DlMqConstant.DL_EXCHANGE_NAME);
        arguments.put("x-dead-letter-routing-key", DlMqConstant.DL_ROUTING_KEY);
        arguments.put("x-max-length", BiMqConstant.BI_QUEUE_MAX_LENGTH);
        arguments.put("x-message-ttl", BiMqConstant.BI_QUEUE_TTL);

        return QueueBuilder
                .durable(BiMqConstant.BI_QUEUE_NAME)
                .withArguments(arguments)
                .build();
    }

    @Bean
    public Binding BiDirectBinding(DirectExchange biDirectExchange, Queue biDirectQueue) {
        return BindingBuilder
                .bind(biDirectQueue)
                .to(biDirectExchange)
                .with(BiMqConstant.BI_ROUTING_KEY);
    }
}
