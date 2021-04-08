package com.changgou.item.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/5 0005 17:57
 */
@Configuration
public class EnableMvcConfig implements WebMvcConfigurer {
    /***
     * 静态资源过滤
     * mapping:请求路径映射
     * location:本地查找路径
     *
     * <mvc:resources mapping="" location=""/>
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/items/**").addResourceLocations("classpath:/items/");

    }
}