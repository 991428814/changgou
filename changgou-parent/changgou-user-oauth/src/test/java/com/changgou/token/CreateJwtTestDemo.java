package com.changgou.token;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: 令牌的创建和解析
 * @date 2020/11/10 0010 20:28
 */
public class CreateJwtTestDemo {
    /***
     * 创建令牌
     */
    @Test
    public void testCreateToken(){

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
        System.out.println(token);
    }

    @Test
    public void testParseToken(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRyZXNzIjoic3oiLCJuaWtlbmFtZSI6InRvbWNhdCIsImF1dGhvcml0aWVzIjpbImFkbWluIiwib2F1dGgiXX0.lLP0BgA9MRDz2QMD4YZUuOWfzV3reR4zoisTWG0g1AawTu0_fvnDGtA_oUWM5HMvriv_xlHlkNyTaIi8K1PD2K_8G-Od_a82pSa_TkYmMlKlGzToPgEeZ9qMUt-yWvo8sd3S43c2F0Fl5EGJHJbasOsL1cctc3Y3sw7GX-4YYqsN-kEVnGMhkWhp31XMsgTJQfRnPzBWj5vnrxr29oMIhKB_dwi5TinQxXvCT0V4UADkPpUrAnrWxIqABE2fLmYRdxxHO2_MMt2LClLJ6z3-axtjc2zS_AsqVPBQAiyH9JejLNmZgWkpMarPPZyYoSrgskMBeEP_iNzCmL7I-z9bZQ";
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+kNYzA30Xbous+mHunPRAyoUN+KnicI+cpv3oHijIoeHW+tTjjX0zFIGGhupPVYuCTl/LDYoJSUbZgDTnuNcbjyBTZpl9YPMKe4U/4HBCCEi6+hFC9V+5s0qm1zIQB5Lq7J3wgZsRHU95M3vjOFFTK6JV2elH3vj4rZK+CxwPoC1mrwL7odCL2d0q6SzozpheYJMiG6jzVYx7jFwgH8Xrep0+5tzVchGkIobEhD7wHC4azenVBodC4YZZ2vRlOVE0kP/DV3e8Pi8/TrJ/46fNfu1u9B+4gsv5bKtawwahYo+KYQh+OkjX2kbU5PAjBbollpmHRJYBT3YU9ychd9IQIDAQAB-----END PUBLIC KEY-----";

        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        String claims = jwt.getClaims();

        System.out.println(claims);
    }
}