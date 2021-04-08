package com.itheima.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: 令牌的生成和解析实现
 * @date 2020/11/7 0007 16:31
 */
public class JwtTest {

    /***
     * 创建令牌
     */
    @Test
    public void testCreateToken(){
        //构建Jwt令牌的对象
        JwtBuilder builder = Jwts.builder();
        builder.setIssuer("黑马训练营");
        builder.setIssuedAt(new Date());
        builder.setExpiration(new Date(System.currentTimeMillis()+3600000));
        builder.setSubject("JWT令牌测试");

        //自定义载荷信息
        Map<String,Object> userInfo = new HashMap<String,Object>();
        userInfo.put("company","黑马训练营");
        userInfo.put("address","中南海");
        userInfo.put("money",3500);
        builder.addClaims(userInfo);  //添加载荷

        builder.signWith(SignatureAlgorithm.HS256,"itcast");//1.签名算法 2.秘钥(盐)
        String token = builder.compact();
        System.out.println(token);

    }

    /***
     * 令牌解析
     */
    @Test
    public void parseToken(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiLpu5Hpqazorq3nu4PokKUiLCJpYXQiOjE2MDQ3MzkxOTksImV4cCI6MTYwNDc0Mjc5OSwic3ViIjoiSldU5Luk54mM5rWL6K-VIiwiYWRkcmVzcyI6IuS4reWNl-a1tyIsIm1vbmV5IjozNTAwLCJjb21wYW55Ijoi6buR6ams6K6t57uD6JClIn0.e_GJlugQV6l-W8rgpeWB7iuxsXIItI_VnZtepF6hgDk";
        Claims claims = Jwts.parser()
                .setSigningKey("itcast") //盐
                .parseClaimsJws(token)   //要解析的对象
                .getBody();
        System.out.println(claims.toString());
    }
}