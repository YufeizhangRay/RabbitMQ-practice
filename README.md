# RabbitMQ-practice  
  
## RabbitMQ学习实践 原理分析 
  
### 什么是RabbitMQ  
  
RabbitMQ是一个由erlang语言编写的AMQP（Advanced Message Queue Protocol）的开源实现，由RabbitMQ Technologies Ltd开发并且提供商业支持。    
  
### 典型应用场景  
  
>1.跨系统的异步通信  
2.应用内的同步变成异步  
3.基于Pub/Sub模型实现的事件驱动  
4.利用RabbitMQ实现事务的最终一致性  
  
### AMQP协议  
  
AMQP 即 Advanced Message Queuing Protocol，一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。基于此协议的客户端与消息中间件可传递消息，并不受不同客户端/中间件同产品、不同的开发语言等条件的限制。  

### RabbitMQ的特性  
  
RabbitMQ使用Erlang语言编写，使用Mnesia数据库存储消息。  
>(1)可靠性(Reliability)   
RabbitMQ 使用一些机制来保证可靠性，如持久化、传输确认、发布确认。  
(2)灵活的路由(Flexible Routing)  
在消息进入队列之前，通过 Exchange 来路由消息的。对于典型的路由功能，RabbitMQ 已经提供了一些内置的 Exchange 来实现。针对更复杂的路由功能，可以将多个 Exchange 绑定在一起，也通过插件机制实现自己的 Exchange。  
(3)消息集群(Clustering)   
多个 RabbitMQ 服务器可以组成一个集群，形成一个逻辑 Broker。  
(4)高可用(Highly Available Queues)   
队列可以在集群中的机器上进行镜像，使得在部分节点出问题的情况下队列仍然可用。  
(5)多种协议(Multi-protocol)   
RabbitMQ 支持多种消息队列协议，比如 AMQP、STOMP、MQTT 等等。  
(6)多语言客户端(Many Clients)   
RabbitMQ 几乎支持所有常用语言，比如 Java、.NET、Ruby、PHP、C#、 JavaScript 等等。  
(7)管理界面(Management UI)  
RabbitMQ 提供了一个易用的用户界面，使得用户可以监控和管理消息、集群中的节点。  
(8)插件机制(Plugin System)   
RabbitMQ提供了许多插件，以实现从多方面扩展，当然也可以编写自己的插件。  
  
#### 工作模型  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/%E6%9E%B6%E6%9E%84%E6%A8%A1%E5%9E%8B.jpeg)  
   
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/%E6%A6%82%E5%BF%B5.jpeg)  
  
三种主要的交换机  
  
Direct Exchange 直连交换机  
定义:直连类型的交换机与一个队列绑定时，需要指定一个明确的binding key。  
路由规则:发送消息到直连类型的交换机时，只有routing key跟binding key完全匹配时，绑定的队列才能收到消息。 例如:  
```
 // 只有队列1能收到消息
channel.basicPublish("MY_DIRECT_EXCHANGE", "key1", null, msg.getBytes());
```
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/direct.jpeg)  
  
Topic Exchange 主题交换机  
定义:主题类型的交换机与一个队列绑定时，可以指定按模式匹配的routing key。  
通配符有两个，*代表匹配一个单词。#代表匹配零个或者多个单词。单词与单词之间用 . 隔开。  
路由规则:发送消息到主题类型的交换机时，routing key符合binding key的模式时，绑定的队列才能收到消息。 例如:  
```
 // 只有队列1能收到消息
channel.basicPublish("MY_TOPIC_EXCHANGE", "sh.abc", null, msg.getBytes());
// 队列2和队列3能收到消息
channel.basicPublish("MY_TOPIC_EXCHANGE", "bj.book", null, msg.getBytes());
// 只有队列4能收到消息
channel.basicPublish("MY_TOPIC_EXCHANGE", "abc.def.food", null, msg.getBytes());
 ```
 
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/topic.jpeg)  
  
Fanout Exchange 广播交换机  
定义:广播类型的交换机与一个队列绑定时，不需要指定binding key。  
路由规则:当消息发送到广播类型的交换机时，不需要指定routing key，所有与之绑定的队列都能收到消息。 例如:  
```
 // 3个队列都会收到消息
channel.basicPublish("MY_FANOUT_EXCHANGE", "", null, msg.getBytes());
```
 
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/fanout.jpeg)  
  
### Java API 编程  
  
创建Maven工程，pom.xml引入依赖    
```
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>4.1.0</version>
</dependency>
```
生产者  
```
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
public class MyProducer {
    private final static String QUEUE_NAME = "ORIGIN_QUEUE";
    public static void main(String[] args) throws Exception { 
        ConnectionFactory factory = new ConnectionFactory(); 
        // 连接IP
        factory.setHost("127.0.0.1");
        // 连接端口 
        factory.setPort(5672);
        // 虚拟机 
        factory.setVirtualHost("/"); 
        // 用户 f
        actory.setUsername("guest"); 
        factory.setPassword("guest");
        // 建立连接
        Connection conn = factory.newConnection(); 
        // 创建消息通道
        Channel channel = conn.createChannel();
        String msg = "Hello world, Rabbit MQ";
        // 声明队列
        // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 发送消息(发送到默认交换机AMQP Default，Direct)
        // 如果有一个队列名称跟Routing Key相等，那么消息会路由到这个队列
        // String exchange, String routingKey, BasicProperties props, byte[] body 
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        channel.close();
        conn.close();
    }
}
```
消费者  
```
import com.rabbitmq.client.*;
import java.io.IOException;
public class MyConsumer {
    private final static String QUEUE_NAME = "ORIGIN_QUEUE";
    public static void main(String[] args) throws Exception { 
    ConnectionFactory factory = new ConnectionFactory(); 
    // 连接IP
    factory.setHost("127.0.0.1");
    // 默认监听端口 
    factory.setPort(5672);
    // 虚拟机 
    factory.setVirtualHost("/"); 
    // 设置访问的用户 
    factory.setUsername("guest"); 
    factory.setPassword("guest");
    // 建立连接
    Connection conn = factory.newConnection(); 
    // 创建消息通道
    Channel channel = conn.createChannel();
    // 声明队列
    // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println(" Waiting for message....");
    // 创建消费者
    Consumer consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope,
            AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("Received message : '" + msg + "'");
            }
    };
    // 开始获取消息
    // String queue, boolean autoAck, Consumer callback channel.basicConsume(QUEUE_NAME, true, consumer);
  } 
}
```
参数说明  
声明交换机的参数  
>String type:交换机的类型，direct, topic, fanout中的一种。  
boolean durable:是否持久化，代表交换机在服务器重启后是否还存在。  
  
声明队列的参数  
>boolean durable:是否持久化，代表队列在服务器重启后是否还存在。  
boolean exclusive:是否排他性队列。排他性队列只能在声明它的Connection中使用，连接断开时自动删除。  
boolean autoDelete:是否自动删除。如果为true，至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时，队列会自动删除。  
Map<String, Object> arguments:队列的其他属性，例如x-message-ttl、x-expires、x-max-length、x-max- length-bytes、x-dead-letter-exchange、x-dead-letter-routing-key、x-max-priority。  
  
消息属性BasicProperties  
消息的全部属性有14个，以下列举了一些主要的参数:  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/%E5%B1%9E%E6%80%A7%E5%8F%82%E6%95%B0.jpeg)  

### RabbitMQ进阶  
  
#### TTL(Time To Live)  
消息的过期时间  
有两种设置方式:   
通过队列属性设置消息过期时间:  
```
Map<String, Object> argss = new HashMap<String, Object>();
argss.put("x-message-ttl",6000);
channel.queueDeclare("TEST_TTL_QUEUE", false, false, false, argss);
```
设置单条消息的过期时间:  
```
AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder() 
        .deliveryMode(2) // 持久化消息
        .contentEncoding("UTF-8")
        .expiration("10000") // TTL
        .build();
channel.basicPublish("", "TEST_TTL_QUEUE", properties, msg.getBytes());
```
队列的过期时间  
```
Map<String, Object> argss = new HashMap<String, Object>();
argss.put("x-message-ttl",6000);
channel.queueDeclare("TEST_TTL_QUEUE", false, false, false, argss);
```
队列的过期时间决定了在没有任何消费者以后，队列可以存活多久。  
  
#### 死信队列  
有三种情况消息会进入DLX(Dead Letter Exchange)死信交换机。  
>1.(NACK || Reject ) && requeue == false  
2.消息过期  
3.队列达到最大长度(先入队的消息会被发送到DLX)  
  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/DLX.jpeg)  
  
可以设置一个死信队列(Dead Letter Queue)与DLX绑定，即可以存储Dead Letter，消费者可以监听这个队列取走消息。  
```
Map<String,Object> arguments = new HashMap<String,Object>(); 
arguments.put("x-dead-letter-exchange","DLX_EXCHANGE");
// 指定了这个队列的死信交换机
channel.queueDeclare("TEST_DLX_QUEUE", false, false, false, arguments);
// 声明死信交换机
channel.exchangeDeclare("DLX_EXCHANGE","topic", false, false, false, null);
// 声明死信队列
channel.queueDeclare("DLX_QUEUE", false, false, false, null);
// 绑定
channel.queueBind("DLX_QUEUE","DLX_EXCHANGE","#");
```
  
#### 优先级队列  
设置一个队列的最大优先级:  
```
Map<String, Object> argss = new HashMap<String, Object>(); 
argss.put("x-max-priority",10); // 队列最大优先级
channel.queueDeclare("ORIGIN_QUEUE", false, false, false, argss);
```
发送消息时指定消息当前的优先级:  
```
AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder() 
        .priority(5) // 消息优先级
        .build();
channel.basicPublish("", "ORIGIN_QUEUE", properties, msg.getBytes());
```
优先级高的消息可以优先被消费，但是只有消息堆积(消息的发送速度大于消费者的消费速度)的情况下优先级才有意义。  
  
#### 延迟队列  
RabbitMQ本身不支持延迟队列。可以使用TTL结合DLX的方式来实现消息的延迟投递，即把DLX跟某个队列绑定，到了指定时间，消息过期后，就会从DLX路由到这个队列，消费者可以从这个队列取走消息。  
另一种方式是使用rabbitmq-delayed-message-exchange插件。  
当然，将需要发送的信息保存在数据库，使用任务调度系统扫描然后发送也是可以实现的。  
  
#### RPC  
RabbitMQ实现RPC的原理:服务端处理消息后，把响应消息发送到一个响应队列，客户端再从响应队列取到结果。  
其中的问题：Client收到消息后，怎么知道应答消息是回复哪一条消息的？所以必须有一个唯一ID来关联，就是 correlationId。  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/RPC.jpeg)  
  
#### 服务端流控(Flow Control)  
RabbitMQ 会在启动时检测机器的物理内存数值。默认当 MQ 占用 40% 以上内存时，MQ 会主动抛出一个内存警告并阻塞所有连接(Connections)。可以通过修改 rabbitmq.config 文件来调整内存阈值，默认值是 0.4，如下所示: 
```
[{rabbit, [{vm_memory_high_watermark, 0.4}]}]
```
默认情况，如果剩余磁盘空间在 1GB 以下，RabbitMQ 主动阻塞所有的生产者。这个阈值也是可调的。注意队列长度只在消息堆积的情况下有意义，而且会删除先入队的消息，不能实现服务端限流。  
  
#### 消费端限流  
在AutoACK为false的情况下，如果一定数目的消息(通过基于consumer或者channel设置Qos的值)未被确认前，不进行消费新的消息。
```
channel.basicQos(2); // 如果超过2条消息没有发送ACK，当前消费者不再接受队列消息 
channel.basicConsume(QUEUE_NAME, false, consumer);
```

### UI管理界面的使用  
  
管理插件提供了更简单的管理方式。  
启用管理插件  
Windows启用管理插件  
```
cd C:\Program Files\RabbitMQ Server\rabbitmq_server-3.6.6\sbin 
rabbitmq-plugins.bat enable rabbitmq_management
```
Linux启用管理插件  
```    
cd /usr/lib/rabbitmq/bin 
./rabbitmq-plugins enable rabbitmq_management
```
管理界面访问端口  
默认端口是15672，默认用户guest，密码guest。guest用户默认只能在本机访问。 
  
Linux 创建RabbitMQ用户  
例如创建用户admin，密码admin，授权访问所有的Vhost  
```
firewall-cmd --permanent --add-port=15672/tcp
firewall-cmd --reload
rabbitmqctl add_user admin admin
rabbitmqctl set_user_tags admin administrator
rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
```
  
### Spring配置方式集成RabbitMQ  
  
参考源代码。  
步骤:  
>1.创建Maven工程，pom.xml引入依赖   
2.src/main/resouces目录，创建rabbitMQ.xml  
3.配置applicationContext.xml   
4.src/main/resouces目录，log4j.properties   
5.编写生产者  
6.编写4个消费者  
7.编写单元测试类  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/%E5%85%B3%E7%B3%BB%E6%A8%A1%E5%9E%8B.jpeg)  
  
### Spring Boot集成RabbitMQ  
  
参考源代码。  
   
### 可靠性投递与生产实践  
  
可靠性投递  
首先需要明确，效率与可靠性是无法兼得的，如果要保证每一个环节都成功，势必会对消息的收发效率造成影响。  
如果是一些业务实时一致性要求不是特别高的场合，可以牺牲一些可靠性来换取效率。  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/%E5%8F%AF%E9%9D%A0%E4%BC%A0%E8%BE%93.jpeg)  
  
>1.代表消息从生产者发送到Exchange;   
2.代表消息从Exchange路由到Queue;   
3.代表消息在Queue中存储;  
4.代表消费者订阅Queue并消费消息。  
  
确保消息发送到RabbitMQ服务器  
可能因为网络或者Broker的问题导致1失败，而生产者是无法知道消息是否正确发送到Broker的。  
有两种解决方案，第一种是Transaction(事务)模式，第二种Confirm(确认)模式。  
在通过channel.txSelect方法开启事务之后，我们便可以发布消息给RabbitMQ了，如果事务提交成功，则消息一定到达了RabbitMQ中，如果在事务提交执行之前由于RabbitMQ异常崩溃或者其他原因抛出异常，这个时候我们便可以将其捕获，进而通过执行channel.txRollback方法来实现事务回滚。使用事务机制的话会“吸干”RabbitMQ的性能，一般不建议使用。  
  
生产者通过调用channel.confirmSelect方法(即Confirm.Select命令)将信道设置为confirm模式。一旦消息被投递到所有匹配的队列之后，RabbitMQ就会发送一个确认(Basic.Ack)给生产者(包含消息的唯一ID)，这就使得生产者知晓消息已经正确到达了目的地了。  
  
确保消息路由到正确的队列  
可能因为路由关键字错误，或者队列不存在，或者队列名称错误导致2失败。  
使用mandatory参数和ReturnListener，可以实现消息无法路由的时候返回给生产者。  
另一种方式就是使用备份交换机(alternate-exchange)，无法路由的消息会发送到这个交换机上。  
```
 Map<String,Object> arguments = new HashMap<String,Object>(); 
 arguments.put("alternate-exchange","ALTERNATE_EXCHANGE"); // 指定交换机的备份交换机
 channel.exchangeDeclare("TEST_EXCHANGE","topic", false, false, false, arguments);
```
  
确保消息在队列正确地存储  
可能因为系统宕机、重启、关闭等等情况导致存储在队列的消息丢失，即3出现问题。  
解决方案:  
1、队列持久化  
```
// String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
channel.queueDeclare(QUEUE_NAME, true, false, false, null);
```
2、交换机持久化  
```
  // String exchange, boolean durable
channel.exchangeDeclare("MY_EXCHANGE","true");
```
3、消息持久化  
```
AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder() 
.deliveryMode(2) // 2代表持久化，其他代表瞬态
.build();
channel.basicPublish("", QUEUE_NAME, properties, msg.getBytes());
```
4、集群，镜像队列(单独叙述)
  
确保消息从队列正确地投递到消费者  
如果消费者收到消息后未来得及处理即发生异常，或者处理过程中发生异常，会导致4失败。  
为了保证消息从队列可靠地达到消费者，RabbitMQ提供了消息确认机制(message acknowledgement)。消费者在订阅队列时，可以指定autoAck参数，当autoAck等于false时，RabbitMQ会等待消费者显式地回复确认信号后才从队列中移去消息。  
  
如果消息消费失败，也可以调用Basic.Reject或者Basic.Nack来拒绝当前消息而不是确认。如果requeue参数设置为 true，可以把这条消息重新存入队列，以便发给下一个消费者(当然，只有一个消费者的时候，这种方式可能会出现无限循环重复消费的情况，可以投递到新的队列中，或者只打印异常日志)。  

消费者回调  
消费者处理消息以后，可以再发送一条消息给生产者，或者调用生产者的API，告知消息处理完毕。  
  
补偿机制  
对于一定时间没有得到响应的消息，可以设置一个定时重发的机制，但要控制次数，比如最多重发3次，否则会造成消息堆积。  
  
消息幂等性  
服务端是没有这种控制的，只能在消费端控制。  
如何避免消息的重复消费？  
消息重复可能会有两个原因:   
>1.生产者的问题，环节1重复发送消息，比如在开启了Confirm模式但未收到确认。   
>2.环节4出了问题，由于消费者未发送ACK或者其他原因，消息重复投递。 
  
对于重复发送的消息，可以对每一条消息生成一个唯一的业务ID，通过日志或者建表来做重复控制。  
  
消息的顺序性  
消息的顺序性指的是消费者消费的顺序跟生产者产生消息的顺序是一致的。  
在RabbitMQ中，一个队列有多个消费者时，由于不同的消费者消费消息的速度是不一样的，顺序无法保证。  

### 高可用架构  
  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/%E9%AB%98%E5%8F%AF%E7%94%A8%E6%9E%B6%E6%9E%84.jpeg)  
  
RabbitMQ集群  

集群主要用于实现高可用与负载均衡。  
RabbitMQ通过/var/lib/rabbitmq/.erlang.cookie来验证身份，需要在所有节点上保持一致。  
集群有两种节点类型，一种是磁盘节点，一种是内存节点。集群中至少需要一个磁盘节点以实现元数据的持久化，未指定类型的情况下，默认为磁盘节点。  
集群通过25672端口两两通信，需要开放防火墙的端口。  
需要注意的是，RabbitMQ集群无法搭建在广域网上，除非使用federation或者shovel等插件。  
集群的配置步骤:  
>1.配置hosts  
2.同步erlang.cookie  
3.加入集群  
  
RabbitMQ镜像队列  
  
集群方式下，队列和消息是无法在节点之间同步的，因此需要使用RabbitMQ的镜像队列机制进行同步。  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/%E9%AB%98%E5%8F%AF%E7%94%A8%E9%85%8D%E7%BD%AE.jpeg)  
  
如图:  
![](https://github.com/YufeizhangRay/image/blob/master/RabbitMQ/%E9%98%9F%E5%88%97%E9%95%9C%E5%83%8F.jpeg)  
  
HAproxy负载+Keepalived高可用  
  
在两个内存节点上安装HAProxy  
```
yum install haproxy
```
编辑配置文件
```
vim /etc/haproxy/haproxy.cfg
```
内容修改为:
```
global 
    log                     127.0.0.1 local2
    chroot                  /var/lib/haproxy
    pidfile                 /var/run/haproxy.pid
    maxconn                 4000
    user                    haproxy
    group                   haproxy
    daemon
    stats socket /var/lib/haproxy/stats
    
defaults
    log                     global
    option                  dontlognull
    option                  redispatch
    retries                 3
    timeout connect         10s
    timeout client          1m
    timeout server          1m
    maxconn                 3000
       
listen http_front
        mode http
        bind 0.0.0.0:1080             #监听端口 
        stats refresh 30s             #统计页面自动刷新时间
        stats uri /haproxy?stats      #统计页面url
        stats realm Haproxy Manager   #统计页面密码框上提示文本
        stats auth admin:123456       #统计页面用户名和密码设置
        
listen rabbitmq_admin 
    bind 0.0.0.0:15673
    server node1 192.168.8.40:15672 
    server node2 192.168.8.45:15672
    
listen rabbitmq_cluster 0.0.0.0:5673 
    mode tcp
    balance roundrobin
    timeout client 3h
    timeout server 3h
    timeout connect 3h
    server node1 192.168.8.40:5672 check inter 5s rise 2 fall 3 
    server node2 192.168.8.45:5672 check inter 5s rise 2 fall 3
```
启动HAProxy  
```
haproxy -f /etc/haproxy/haproxy.cfg
```
安装Keepalived  
```
yum -y install keepalived 
```
修改配置文件  
```
vim /etc/keepalived/keepalived.conf
```
内容改成(物理网卡和当前主机IP要修改):  
```
global_defs {
    notification_email {
      acassen@firewall.loc 
      failover@firewall.loc 
      sysadmin@firewall.loc
    }
    notification_email_from Alexandre.Cassen@firewall.loc 
    smtp_server 192.168.200.1
    smtp_connect_timeout 30
    router_id LVS_DEVEL
    vrrp_skip_check_adv_addr
    # vrrp_strict # 注释掉，不然访问不到VIP vrrp_garp_interval 0
    vrrp_gna_interval 0
}
global_defs {
    notification_email { 
      acassen@firewall.loc 
      failover@firewall.loc 
      sysadmin@firewall.loc
    }
    notification_email_from Alexandre.Cassen@firewall.loc 
    smtp_server 192.168.200.1
    smtp_connect_timeout 30
    router_id LVS_DEVEL
    vrrp_skip_check_adv_addr
    # vrrp_strict # 注释掉，不然访问不到VIP 
    vrrp_garp_interval 0
    vrrp_gna_interval 0
}
# 检测任务
vrrp_script check_haproxy {
    # 检测HAProxy监本
    script "/etc/keepalived/script/check_haproxy.sh" 
    # 每隔两秒检测
    interval 2
    # 权重
    weight 2
}
# 虚拟组
vrrp_instance haproxy {
    state MASTER # 此处为`主`，备机是 `BACKUP`
    interface ens33 # 物理网卡，根据情况而定 
    mcast_src_ip 192.168.8.40 # 当前主机ip 
    virtual_router_id 51 # 虚拟路由id，同一个组内需要相同 
    priority 100 # 主机的优先权要比备机高
    advert_int 1 # 心跳检查频率，单位:秒 
    authentication { # 认证，组内的要相同
        auth_type PASS
        auth_pass 1111
    }
    # 调用脚本 track_script {
        check_haproxy
    }
    # 虚拟ip，多个换行 virtual_ipaddress {
        192.168.188.201
    }
}
 ```
启动keepalived 
```
keepalived -D
```
