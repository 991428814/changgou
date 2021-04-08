package com.changgou.oauth.controller;

import com.changgou.oauth.service.UsreLoginService;
import com.changgou.oauth.util.AuthToken;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/11 0011 10:51
 */
@RestController
@RequestMapping(value = "/user")
public class UserLoginController {

    //客户端Id
    @Value("${auth.clientId}")
    private String clientId;

    //客户端秘钥
    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Autowired
    private UsreLoginService usreLoginService;

    /***
     * 登录方法
     */
    @RequestMapping(value = "/login")
    public Result login(String username,String password)throws Exception{
        String grant_type="password";
        //调用userLoginService实现登录
        AuthToken authToken = usreLoginService.login(username, password, clientId, clientSecret, grant_type);
        if(authToken!=null){
            return new Result(true, StatusCode.OK,"登录成功!",authToken);

        }
        return new Result(false, StatusCode.LOGINERROR,"登录失败!");
    }
}