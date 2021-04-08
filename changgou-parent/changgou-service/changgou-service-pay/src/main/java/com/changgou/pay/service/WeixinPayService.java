package com.changgou.pay.service;

import java.util.Map;

public interface WeixinPayService {

    /***
     * 创建二维码操作
     */
    Map createnative(Map<String,String> parameterMap);

    /***
     *查询微信支付状态
     */
    Map queryStatus(String outtradeno);
}
