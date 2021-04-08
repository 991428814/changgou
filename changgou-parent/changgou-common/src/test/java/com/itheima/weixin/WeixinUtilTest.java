package com.itheima.weixin;

import com.github.wxpay.sdk.WXPayUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/23 0023 20:14
 */
public class WeixinUtilTest {

    @Test
    public void testDemo() throws Exception {
        //随机字符串
        String s = WXPayUtil.generateNonceStr();
        //System.out.println(s);

        //将Map转成XML字符串
        Map<String,String> dataMap = new HashMap<String,String>();
        dataMap.put("id","No.001");
        dataMap.put("title","畅购商城杯具支付");
        dataMap.put("money","998");

        String xmlstr = WXPayUtil.mapToXml(dataMap);
        System.out.println(xmlstr);

        String generateSignedXml = WXPayUtil.generateSignedXml(dataMap, "itcast");
        System.out.println(generateSignedXml);

        Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlstr);
        System.out.println(mapResult);
    }
}