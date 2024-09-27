package com.atguigu.interceptors;


import com.atguigu.utils.JwtHelper;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.Jar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.runtime.ObjectMethods;

/*
* 登录包含拦截器，检查请求头是否有token。有且再有效期内，返回有效
* */
@Component
public class LoginProtectedInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtHelper jwtHelper;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        boolean expiration = jwtHelper.isExpiration(token);
        if(!expiration){
            return true; // 放行
        }
        Result result = Result.build(null, ResultCodeEnum.NOTLOGIN);
        //拦截器自动返回Json字符串
        ObjectMapper objectMapper = new JsonMapper();
        String json = objectMapper.writeValueAsString(result); // 把对象转换为json字符串
        response.getWriter().print(json); // 把字符串写入到响应体中
        return false;
    }
}
