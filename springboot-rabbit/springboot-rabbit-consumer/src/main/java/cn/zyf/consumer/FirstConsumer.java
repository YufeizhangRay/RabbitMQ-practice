package cn.zyf.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:zyfmq.properties")
@RabbitListener(queues = "${cn.zyf.firstqueue}")
public class FirstConsumer {

    @RabbitHandler
    public void process(String msg) {
        System.out.println("First Queue received msg : " + msg);
    }

}
