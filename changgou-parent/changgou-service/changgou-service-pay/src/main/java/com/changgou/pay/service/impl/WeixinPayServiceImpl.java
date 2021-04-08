package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/23 0023 20:51
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    //应用id
    @Value("${weixin.appid}")
    private String appid;
    //商户号
    @Value("${weixin.partner}")
    private String partner;
    //秘钥
    @Value("${weixin.partnerkey}")
    private String partnerkey;
    //支付回调地址
    @Value("${weixin.notifyurl}")
    private String notifyurl;

    /***
     * 创建二维码操作
     * @param parameterMap
     * @return
     */
    @Override
    public Map createnative(Map<String, String> parameterMap) {
        try {
            //参数
            Map<String, String> paramMap = new HashMap<>();

            //2.设置参数值(根据文档来写)
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("body", "畅购商城商品不错");
            //订单号
            paramMap.put("out_trade_no", parameterMap.get("outtradeno"));
            paramMap.put("total_fee", parameterMap.get("totalfee"));//单位是分
            paramMap.put("spbill_create_ip", "127.0.0.1");//终端的IP
            paramMap.put("notify_url", notifyurl);
            paramMap.put("trade_type", "NATIVE");//扫码支付类型

            //获取自定义数据
            String exchange = parameterMap.get("exchange");
            String routingkey = parameterMap.get("routingkey");
            Map<String,String> attachMap = new HashMap<String,String>();
            attachMap.put("exchange",exchange);
            attachMap.put("routingkey",routingkey);
            //如果是秒杀订单,需要传username
            String username = parameterMap.get("username");
            if(!StringUtils.isEmpty(username)){
                attachMap.put("username",username);
            }
            String attach = JSON.toJSONString(attachMap);
            paramMap.put("attach", attach);

            String xmlparameters = WXPayUtil.generateSignedXml(paramMap,partnerkey);
            //url地址
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient = new HttpClient(url);
            //提交方式
            httpClient.setHttps(true);
            //提交参数
            httpClient.setXmlParam(xmlparameters);
            //执行提交
            httpClient.post();
            //获取返回数据
            String result = httpClient.getContent();
            //返回数据转成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    /***
     * 查询支付状态
     * @return
     */
    @Override
    public Map queryStatus(String outstradeno) {
        try {
            //参数
            Map<String, String> paramMap = new HashMap<>();

            //2.设置参数值(根据文档来写)
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //订单号
            paramMap.put("out_trade_no", outstradeno);


            String xmlparameters = WXPayUtil.generateSignedXml(paramMap,partnerkey);
            //url地址
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            HttpClient httpClient = new HttpClient(url);
            //提交方式
            httpClient.setHttps(true);
            //提交参数
            httpClient.setXmlParam(xmlparameters);
            //执行提交
            httpClient.post();
            //获取返回数据
            String result = httpClient.getContent();
            //返回数据转成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}