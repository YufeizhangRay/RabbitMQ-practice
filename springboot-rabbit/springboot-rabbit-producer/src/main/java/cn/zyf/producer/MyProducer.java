package cn.zyf.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:zyfmq.properties")
public class MyProducer {

    @Value("${cn.zyf.directexchange}")
    private String directExchange;

    @Value("${cn.zyf.topicexchange}")
    private String topicExchange;

    @Value("${cn.zyf.fanoutexchange}")
    private String fanoutExchange;

    @Value("${cn.zyf.directroutingkey}")
    private String directRoutingKey;

    @Value("${cn.zyf.topicroutingkey1}")
    private String topicRoutingKey1;

    @Value("${cn.zyf.topicroutingkey2}")
    private String topicRoutingKey2;


    // 自定义的模板，所有的消息都会转换成JSON发送
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void send() throws JsonProcessingException {
        rabbitTemplate.convertAndSend(directExchange,directRoutingKey,"a direct msg");

        rabbitTemplate.convertAndSend(topicExchange,topicRoutingKey1,"a topic msg : hangzhou.zyf.teacher");
        rabbitTemplate.convertAndSend(topicExchange,topicRoutingKey2,"a topic msg : beijing.zyf.student");

        rabbitTemplate.convertAndSend(fanoutExchange,"","a fanout msg");

    }


}
