package com.changgou.oauth.controller_20190929_105615;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import com.netflix.client.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${auth.clientId}")
    private String clientId;//客户端id

    @Value("${auth.clientSecret}")
    private String clientSecret;//客户端密码

    //可以访问该Cookie的域名。例如，如果设置为.zhihu.com，则所有以zhihu.com，结尾的域名都可以访问该Cookie。
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //COOKIE的最大生命周期，-1为无生命周期，即只在当前打开的窗口有效，关闭或重新打开其它窗口，仍会要求验证
    @Value("${auth.cookieMaxAge}")
    private String cookieMaxAge;

    //如果设置为/，则本域名下的所有页面都可以访问该Cookie。
    @Value("${auth.cookiePath}")
    private String cookiePath;


    @PostMapping("/login")
    public Result login(String username, String password, HttpServletResponse response){
        //封装
        Map<String,String> map = new HashMap<>();
        map.put("clientId",clientId);
        map.put("clientSecret",clientSecret);
        map.put("username",username);
        map.put("password",password);
        //调用业务层获取jwt令牌
        AuthToken jwt = authService.getJwt(map);
        //向responsse中添加Cookie,参数1请求对象,参数2域名,参数3cookie的名称
        // 参数4cookie的值,参数5cookie的声明周期,参数6如果为true就不可以通过getCookie来获取cookie
        CookieUtil.addCookie(response,cookieDomain,cookiePath,"jti",jwt.getJti(),Integer.parseInt(cookieMaxAge),false);

        return new Result(true, StatusCode.OK,"登录成功");
    }
}
