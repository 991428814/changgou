package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.UsreLoginService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/11 0011 19:39
 */
@Service
public class UserLoginServiceImpl implements UsreLoginService {

    //实现请求发送
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;


    /***
     * 登录实现
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param grant_type
     * @return
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret, String grant_type) throws Exception{

        //获取指定服务的注册数据
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");

        //调用的请求地址 http://localhost:9001/oauth/token

        String url = serviceInstance.getUri()+"/oauth/token";

        //请求提交的对象
        MultiValueMap<String,String> parameterMap = new LinkedMultiValueMap<String,String>();

        //请求提交的数据封装
        parameterMap.add("username",username);
        parameterMap.add("password",password);
        parameterMap.add("grant_type",grant_type);

        //请求体封装
        String Authorization = "Basic "+ new String(Base64.getEncoder().encode((clientId+":"+clientSecret).getBytes()),"UTF-8");
        MultiValueMap headerMap = new LinkedMultiValueMap();
        headerMap.add("Authorization",Authorization);

        //HttpEntity->创建该对象 封装了请求头和请求体
        HttpEntity httpEntity = new HttpEntity(parameterMap,headerMap);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);

        //用户登录后的令牌信息
        Map<String,String> map = response.getBody();

        //将令牌信息转成authToken
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(map.get("access_token"));
        authToken.setRefreshToken(map.get("refresh_token"));
        authToken.setJti(map.get("jti"));

        return authToken;

    }
}