package com.itheima.httpclient;

import entity.HttpClient;
import org.junit.Test;

import java.io.IOException;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/23 0023 20:27
 */
public class HttpClientTest {

    /***
     * 发送http/https请求
     */
    @Test
    public void testHttpClient() throws IOException {
        //https://api.mch.weixin.qq.com/pay/orderquery
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        //创建HttpClient对象
        HttpClient httpClient = new HttpClient(url);

        //要发送的xml数据
        String xml="<xml><name>张三</name></xml>";

        //设置请求的xml参数
        httpClient.setXmlParam(xml);

        //http/https
        httpClient.setHttps(true);

        //发送请求
        httpClient.post();

        //获取响应数据
        String result = httpClient.getContent();
        System.out.println(result);
    }
}