package com.nami.springbootinit.bizmq;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
class MyMessageProducerTest {

    @Resource
    private DlMessageProducer dlMessageProducer;

    @Resource
    private BiMessageProducer biMessageProducer;

    @Test
    void sendDlMessage() {
        dlMessageProducer.sendMessage("测试死信队列正常消费消息");
    }

    @Test
    void sendBiMessage() {
        biMessageProducer.sendMessage("测试Bi队列消息消费正常");
    }

    @Test
    void sendBiMessage3Times() {
        int i = 0;
        while (i <= 3) {
            try {
                biMessageProducer.sendMessage("测试Bi队列消息最大长度为3，此时i=" + i);
                log.info("消息发送成功！此时i = " + i);
                i++;
            } catch (Exception e) {
                log.error("消息发送失败！此时 i = " + i);
            }
        }
    }
}