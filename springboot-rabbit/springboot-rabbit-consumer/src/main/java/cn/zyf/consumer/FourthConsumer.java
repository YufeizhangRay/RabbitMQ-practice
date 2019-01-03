package cn.zyf.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:zyfmq.properties")
@RabbitListener(queues = "${cn.zyf.fourthqueue}")
public class FourthConsumer {
    @RabbitHandler
    public void process(String msg){
        System.out.println("Fourth Queue received msg : " + msg);
    }
}
