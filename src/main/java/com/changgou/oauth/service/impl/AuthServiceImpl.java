package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@Service
public class AuthServiceImpl implements AuthService {

    //发送获取jwt令牌请求的对象
    @Autowired
    private  RestTemplate restTemplate;

    //操作rdis数据库的对象
    @Autowired
    private RedisTemplate redisTemplate;

    //获取配置文件中设置的jwt在redis的过期时间
    @Value("${auth.ttl}")
    private long ttl;


    //获取jwt令牌
    @Override
    public AuthToken getJwt(Map<String, String> map) {
        if (map==null){
            throw  new RuntimeException("参数为空");
        }
        String clientId = map.get("clientId");//客户端id
        String clientSecret = map.get("clientSecret");//客户端密码
        if (StringUtils.isEmpty(clientId)||StringUtils.isEmpty(clientSecret)){
            throw new RuntimeException("客户端id或客户端密码有误");
        }
        //封装请求头
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        //请求头的键是Authorization  值是Basic clientId:clientSecret的Base64编码
        StringBuilder value = new StringBuilder(clientId+':'+clientSecret);
        headers.add("Authorization","Basic "+ Base64Utils.encodeToString(value.toString().getBytes()));

        //创建url,http://服务名,restTemplate会根据这个名称去eureka中去找路径
        //在配置restTemplate是就加了开启负载均衡的注解,因为可能认证服务是以集群的方式部署的
        String url ="http://user-auth/oauth/token";
        //封装请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (!"clientId".equals(entry.getKey())&&!"clientSecret".equals(entry.getKey())){
                body.add(entry.getKey(),entry.getValue());
            }
        }

        //封装请求携带的键值对参数
        HttpEntity requestEntity = new HttpEntity(body,headers);
        //发送请求,参数1url,参数2请求方式,参数3 封装了请求头和请求体的内容,参数4要求响应回来的数据类型
        ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //获取jwt令牌
        Map jwtMap = mapResponseEntity.getBody();
       if (jwtMap==null){
           throw new  RuntimeException("令牌获取失败");
       }
        String access_token = (String) jwtMap.get("access_token");//获取jwt令牌
        String jti = (String) jwtMap.get("jti");//获取jti短令牌
        String refresh_token = (String) jwtMap.get("refresh_token");//获取刷新令牌
        //如果获取的都是空的代表令牌获取失败
        if (StringUtils.isEmpty(access_token)||StringUtils.isEmpty(jti)||StringUtils.isEmpty(refresh_token)){
            throw new RuntimeException("令牌获取失败");
        }
        //将jwt存入redis,jti是键,jwt令牌是值,ttl是过期时间,timeunit.seconds是时间单位秒
        redisTemplate.boundValueOps(jti).set(access_token,ttl, TimeUnit.SECONDS);

        //封装,然后返回
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(access_token);
        authToken.setJti(jti);
        authToken.setRefreshToken(refresh_token);
        return authToken;
    }
}
