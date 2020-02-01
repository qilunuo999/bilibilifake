package com.bilibili_fake.demo.controller;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseControlller {
    /**
     * 登录认证异常
     *
     * @param request
     * @param response
     * @return
     */
    @ExceptionHandler({ UnauthenticatedException.class, AuthenticationException.class })
    public String authenticationException(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        JSONObject json = new JSONObject();
//        json.put("code",400);
//        json.put("message","您已经登陆了，不能进行该操作！o(≧口≦)o");
        return json.toString();
    }
}
