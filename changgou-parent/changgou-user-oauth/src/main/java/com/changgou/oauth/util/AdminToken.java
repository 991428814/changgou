package com.changgou.oauth.util;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/19 0019 10:38
 */
public class AdminToken {

    public static String adminToken(){
        //加载证书  ClassPathResource读取类路径中的文件
        ClassPathResource resource = new ClassPathResource("changgou68.jks");
        //读取证书数据,加载读取证书数据
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,"changgou68".toCharArray());
        //获取证书中的一对秘钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("changgou68","changgou68".toCharArray());
        //获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //创建令牌,需要私钥加盐[RSA算法]
        Map<String,Object> payload = new HashMap<String,Object>();
        payload.put("nikename","tomcat");
        payload.put("address","sz");
        payload.put("authorities", new String[]{"admin","oauth"});

        Jwt jwt = JwtHelper.encode(JSON.toJSONString(payload), new RsaSigner(privateKey));

        //获取令牌数据
        String token = jwt.getEncoded();
        return token;
    }
}