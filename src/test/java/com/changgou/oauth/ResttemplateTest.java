package com.changgou.oauth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ResttemplateTest {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test(){
        //参数1请求路径
        String url ="http://user_oauth/auth/token";

        //封装请求体
        LinkedMultiValueMap<String,String> body = new LinkedMultiValueMap();
        body.add("username","abc");
        body.add("password","12345");

        //封装请求头
        LinkedMultiValueMap<String,String> header = new LinkedMultiValueMap<>();
        header.add("Accept-Encoding","gzip");

        //封装
        HttpEntity requestEntity = new HttpEntity(body,header);

        //参数1是请求的url,参数2是使用哪种方式请求,参数3是封装的请求头和请求体,参数4是响应回来的要求封装成什么样的数据类型
        ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //输出返回回来的数据
        System.out.println(mapResponseEntity.toString());
    }
}
