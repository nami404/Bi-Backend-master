package com.nami.springbootinit.bizmq;

public interface BiMqConstant {

    String BI_EXCHANGE_NAME = "bi_exchange";

    String BI_QUEUE_NAME = "bi_queue";

    String BI_ROUTING_KEY = "bi_routingKey";

    Integer BI_QUEUE_MAX_LENGTH = 3;

    Long BI_QUEUE_TTL = new Long(3000);
}
