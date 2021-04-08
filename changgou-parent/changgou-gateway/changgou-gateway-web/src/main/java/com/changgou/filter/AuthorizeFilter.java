package com.changgou.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author chx
 * @version 1.0
 * @description: 全局过滤器
 * 实现用户权限鉴别(校验)
 * @date 2020/11/8 0008 10:19
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    /***
     * 全局拦截
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取用户令牌信息(参数,头文件，Cookie),如果没有令牌,则拦截
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        boolean hasToken = true;

        if(StringUtils.isEmpty(token)){
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            hasToken = false;
        }

        if(StringUtils.isEmpty(token)){
            HttpCookie httpCookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if(httpCookie!=null){
                token = httpCookie.getValue();
            }
        }

        //如果没有令牌,则拦截

        if(StringUtils.isEmpty(token)){
            //设置没有权限的状态码 401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //响应空数据
            return response.setComplete();
        }

        //如果有令牌,则校验令牌是否有效
//        try {
//            JwtUtil.parseJWT(token);
//            request.mutate().header(AUTHORIZE_TOKEN,token);
//            return chain.filter(exchange);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //无效拦截
//            //设置没有权限的状态码 401
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            //响应空数据
//            return response.setComplete();
//
//        }

        //令牌为空,不允许访问直接拦截
        if(StringUtils.isEmpty(token)){
            //设置没有权限的状态码 401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //响应空数据
            return response.setComplete();
        }else {
            if(!hasToken){
                if(!token.startsWith("bearer") && !token.startsWith("Bearer")){
                    token = "bearer "+token;
                }
                //将令牌封装到头文件中
                request.mutate().header(AUTHORIZE_TOKEN,token);
            }
        }


        //有效放行
        return chain.filter(exchange);
    }

    /***
     * 排序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
