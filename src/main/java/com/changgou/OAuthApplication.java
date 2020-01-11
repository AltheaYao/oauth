package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.changgou.auth.dao")
public class OAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthApplication.class,args);
    }

    @Bean
    @LoadBalanced //开启负载均衡,
    // 作用:加了这个注解生成的restTemplate会生成一个拦截器,
    // 利用这个拦截器spring可以对restTemplate的Bean进行定制进行ip:port的替换,就是将请求地址的服务名转换成具体的地址
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}