package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/19 0019 10:21
 */
@Configuration
public class TokenRequestInterceptor implements RequestInterceptor {
    /***
     * feign执行之前进行拦截
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        /***
         * 从数据库加载查询用户信息
         * 1.Feign调用之前,没有令牌则生成令牌(admin)
         * 2.令牌携带过去
         * 3.令牌放到请求头中
         * 4.请求->Feign调用->RequestInterceptor拦截器->Feign调用之前拦截
         */

        String token = AdminToken.adminToken();
        template.header("Authorization","bearer "+token);

    }
}