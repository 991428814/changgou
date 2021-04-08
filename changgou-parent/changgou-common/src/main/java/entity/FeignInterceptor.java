package entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/19 0019 10:21
 */

public class FeignInterceptor implements RequestInterceptor {
    /***
     * feign执行之前进行拦截
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        /***
         * 获取用户的令牌
         * 将令牌再封装到头文件中
         */

        //记录了当前用户请求的所有数据,包含请求头和请求参数等
        //用户当前请求的时候对应的线程的数据,如果开启了熔断,默认是线程池隔离,需要将熔断策略换成信号量隔离,此时不会开启新的线程
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        /***
         * 获取所有请求头中的数据
         */
        Enumeration<String> headerNames = requestAttributes.getRequest().getHeaderNames();

        while (headerNames.hasMoreElements()){
            //请求头的key
            String headKey = headerNames.nextElement();
            //获取请求头的值
            String headerValue = requestAttributes.getRequest().getHeader(headKey);
            System.out.println(headKey+":"+headerValue);

            //将请求头信息封装到头中，使用Feign调用的时候传递给下一个微服务
            template.header(headKey,headerValue);
        }
    }
}