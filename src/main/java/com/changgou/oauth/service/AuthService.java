package com.changgou.oauth.service;

import com.changgou.oauth.util.AuthToken;

import java.util.Map;

public interface AuthService {
    //认证生成jwt令牌
    AuthToken getJwt(Map<String,String> map);
}
