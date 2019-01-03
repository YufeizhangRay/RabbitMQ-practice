package cn.zyf.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:zyfmq.properties")
public class RabbitConfig {
    @Value("${cn.zyf.firstqueue}")
    private String firstQueue;

    @Value("${cn.zyf.secondqueue}")
    private String secondQueue;

    @Value("${cn.zyf.thirdqueue}")
    private String thirdQueue;

    @Value("${cn.zyf.fourthqueue}")
    private String fourthQueue;

    @Value("${cn.zyf.directexchange}")
    private String directExchange;

    @Value("${cn.zyf.topicexchange}")
    private String topicExchange;

    @Value("${cn.zyf.fanoutexchange}")
    private String fanoutExchange;

    // 创建四个队列
    @Bean("zyfFirstQueue")
    public Queue getFirstQueue(){
        return new Queue(firstQueue);
    }

    @Bean("zyfSecondQueue")
    public Queue getSecondQueue(){
        return new Queue(secondQueue);
    }

    @Bean("zyfThirdQueue")
    public  Queue getThirdQueue(){
        return  new Queue(thirdQueue);
    }

    @Bean("zyfFourthQueue")
    public  Queue getFourthQueue(){
        return  new Queue(fourthQueue);
    }

    // 创建三个交换机
    @Bean("zyfDirectExchange")
    public DirectExchange getDirectExchange(){
        return new DirectExchange(directExchange);
    }

    @Bean("zyfTopicExchange")
    public TopicExchange getTopicExchange(){
        return new TopicExchange(topicExchange);
    }

    @Bean("zyfFanoutExchange")
    public FanoutExchange getFanoutExchange(){
        return new FanoutExchange(fanoutExchange);
    }

    // 定义四个绑定关系
    @Bean
    public Binding bindFirst(@Qualifier("zyfFirstQueue") Queue queue, @Qualifier("zyfDirectExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("zyf.test");
    }

    @Bean
    public Binding bindSecond(@Qualifier("zyfSecondQueue") Queue queue, @Qualifier("zyfTopicExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("*.zyf.*");
    }

    @Bean
    public Binding bindThird(@Qualifier("zyfThirdQueue") Queue queue, @Qualifier("zyfFanoutExchange") FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Binding bindFourth(@Qualifier("zyfFourthQueue") Queue queue, @Qualifier("zyfFanoutExchange") FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }

}
