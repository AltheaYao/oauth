package com.changgou.oauth.controller_20190929_105615;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//全局异常处理器
@ControllerAdvice //增强类,声明这是个全局异常处理类
public class HandlerException {

    @ExceptionHandler(Exception.class)//捕获所有异常
    public Result handlerException(Exception e){

        return new Result(false, StatusCode.ERROR,e.getMessage());
    }
}
