package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {
    @Autowired
    private Producer producer;

    @Test
    public void test() {
        producer.sendMsg("test", "Hi, i'm Wendell Zhang.");
        producer.sendMsg("test", "I'm from ByteDance.");
        try {
            Thread.sleep(1000 * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Component
class Producer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMsg(String topic, String data) {
        kafkaTemplate.send(topic, data);
    }
}

@Component
class Consumer {
    @KafkaListener(topics = "test")
    public void consumer(ConsumerRecord consumerRecord) {
        System.out.println(consumerRecord.topic());
        System.out.println(consumerRecord.key());
        System.out.println(new Date(consumerRecord.timestamp()));
        System.out.println(consumerRecord.value());
    }
}