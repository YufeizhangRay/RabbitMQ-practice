package cn.zyf.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:zyfmq.properties")
@RabbitListener(queues = "${cn.zyf.thirdqueue}")
public class ThirdConsumer {
    @RabbitHandler
    public void process(String msg) {
        System.out.println("Third Queue received msg : " + msg);
    }
}
