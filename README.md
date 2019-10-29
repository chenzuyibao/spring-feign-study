
# Feign 学习笔记

### web Service



### Feign 入门

1.引入相关依赖
```xml
<dependency>

        <groupId>org.springframework.boot</groupId>

        <artifactId>spring-boot-starter-web</artifactId>

    </dependency>

    <dependency>

        <groupId>org.springframework.cloud</groupId>

        <artifactId>spring-cloud-starter-<u>openfeign</u></artifactId>

    </dependency>


```
2.编写启动类，加上@EnableFeignClients 注解
3.编写Feign 服务类
```java
@FeignClient(name="github-client", url ="https://api.github.com")

public interface HelloFeignService {
@RequestMapping(value="/search/repositories", method = RequestMethod.GET)

    String searchRepo(@RequestParam("q") String queryStr);

}
```
4.编写controller ，注入Feign 服务类使用

### Feign 工作原理
@EnableFeignClients 注解开启了对FeignClient 扫描加载处理，程序启动时会扫描所有@FeignClients 注解的类，注入到IOC容器中，当Feign接口中的方法中被调用时，通过JDK代理，生成具体的RequestTemplate,当生成代理时，Feign会为每一个接口方法创建一个RequestTemplate,然后由RequestTemplate生成Request, 然后把Request交给Client(URLConnection, Apache 的Http Client 或者OKhttp)处理，最后Client被封装到LoadBalanceClient 这个类结合Ribbon负载均衡发起服务之间的调用


### Feign注解详解
@FeignClient 注解作用于接口上
FeignClient 注解对应的属性

* name: 只当FeignClient的名称，如果项目使用Ribbon,name 属性会作为为服务的名称，用于服务发现
* url: 一般用于调试，可以手动指定@FeignClient 调用的地址
* decode404: 当发生 404 错误时，如果该字段为true,会调用decoder 进行解码，否则抛出FeignException
* configuration: Feign配置类，可以自定义Feign 的Encoder,Decoder,LogLevel 等
* fallback: 定义容错的处理类，当调用远程接口失败或者超时，会调用对应接口的容错逻辑，fallback 指定的类必选实现@FeignClient 标记的接口
* fallbackFactory: 工厂类，用于生成fallback 类示例，通过这个属性我们可以实现每个接口通用的容错逻辑，减少重复代码

### Feign 开启GZIP压缩

支持对请求和响应进行GZIP压缩，可以提高通信效率。压缩后Feign之间的调用通过二进制协议进行传输，返回值需要改为ResponseEntity<byte[]>，否则是乱码。
配置方式：
```yml
feign:
  compression:
    request:
      enabled: true
      mime-types: text/xml, application/xml, application/json
      min-request-size: 2048
    response:
      enabled: true
```
### Feign 属性文件配置
1.对指定名称的Feign进行配置
```yml
feign:
  client:
    config:
      feignName: HelloFeignService # 需要配置的Feign名称
        connectTimeout: 5000 # 连接超时时间
        readTimeout: 5000 # 度超时时间设置
        loggerLevel: full #配置Feign的日志级别
        errorDecoder: com.example.SimpleErrorDecoder #Feign 的错误解码器
        retryer: com.example.SimpleRetryer #配置重试
        requestinterceptors:
          - com.example.FooRequestInterceptor
          - com.example.BarRequestInterceptor
        decode404: false
        encoder: com.example.SimpleEncoder #Feign 的编码器
        decoder: com.example.SimpleDecoder #Feign 的解码器
        contract: com.example.SimpleContract #Feign 的Contract 配置
 
```
2. 作用于所有的Feign
 * 将默认配置写成要个java类，@EnableFeignClients 注解上有个defaultConfiguration属性，配置成默认配置java类，就行
 * 是用配置文件，将feignName 改为default
 
 ### Feign Client 开启日志
 1.配置日志输出级别
 ```yml
logging:
  level:
    cn.springcloud.book.feign.service.HelloFeignService: debug
```     

2.配置日志Bean
```java
@Bean
Logger.Level feignLoggerLevel(){
    return Logger.Level.Full;
}
```

### Feign 的超时设置
Feign 的调用分两层，即Ribbon 的调用和Hystrix 的调用，高版本的Hystrix 默认是关闭的，
```yml
ribbon:
  ReadTimeout:120000
  ConnectTimeout: 30000
```
如果开启Hystrix 报错超时
```yml
feign:
  hystrix:
    enabled: true
hystrix:
  shareSecurityContext: true
  command:
    default:
      circuitBreaker:
        sleepWindowInMilliseconds: 100000
        forceClosed: true
      execution:
        isolation:
          thread:
            timeoutInMillisseconds: 600000
    
```

## Feign 实战
Feign 默认使用的是JDK原生的URLConnection 发送HTTP请求
使用HTTP Client 替换Feign 的默认Client 
1.引入maven依赖
```xml
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId><u>httpclient</u></artifactId>
    </dependency>
    <dependency>
        <groupId>com.netflix.feign</groupId>
        <artifactId>feign-<u>httpclient</u></artifactId>
        <version>8.17.0</version>
    </dependency>

```
2. 修改配置文件
```yml
feign:
  httpclient:
    enabled: true  
```

使用okhttp 替换Feign 默认的Client
1.引入maven 依赖
```xml
    <dependency>
        <groupId>io.github.openfeign</groupId>
        <artifactId>feign-<u>okhttp</u></artifactId>
    </dependency>
```
2.修改配置文件
```yml
feign:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
```

### Feign 整合eureka
上面的Feign 是通过url 指定调用地址，下面介绍Feign 调用eureka上注册的服务
@FeignClient(name="github-client", url ="https://api.github.com")

1. 引入maven依赖
```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-<u>netflix</u>-<u>eureka</u>-client</artifactId>
    </dependency>
```
因为Feign 已经和eureka整合到一起了，所以启动类上就不需要，@EnableEurekaClient
注解了
2. 远程调用的服务，
```java
@FeignClient(name = "SPRING-EUREKA-PRODUCER")  //指定Eureka服务的名称
public interface LocalFeignService {
    @GetMapping("/produce/hello")
    public String custome();
}
```
Eureka 服务，就是IP加上端口
上面的生成具体的RequestTemplate访问地址就是：
http://服务提供方IP:服务提供方端口/produce/hello   

### Feign 整合传递多个参数


使用@RequestBody 传递对象


### 解决Feign 首次请求失败问题
当Feign 和Ribbon 整合了Hystrix 之后，可能出现首次调用失败的问题，因为Hystrix 默认超时时间是1秒，由于Bean 的装配以及懒加载机制，Feign 首次请求会比较慢。

1.将Hystrix 的超时时间，改成5秒
```
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 5000
```
2. 禁用Hystrix 的超时时间
```yml
hystrix:
  command:
    default:
      execution:
        timeout:
          enabled: false
```

### Feign 调用传递Token 
使用拦截器，先获取token, 然后再加入到Feign 的请求中去

