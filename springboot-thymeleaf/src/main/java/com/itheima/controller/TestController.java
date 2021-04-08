package com.itheima.controller;

import com.itheima.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/1 0001 13:16
 */
@Controller
@RequestMapping(value = "/test")
public class TestController {

    /***
     * 基本案例
     * @return
     */
    @GetMapping(value = "/hello")
    public String hello(Model model){
        model.addAttribute("message","hello thymeleaf!");

        //创建一个List<User>,用thymeleaf展示
        //集合数据
        List<User> users = new ArrayList<User>();
        users.add(new User(1,"张三","深圳"));
        users.add(new User(2,"李四","北京"));
        users.add(new User(3,"王五","武汉"));
        model.addAttribute("users",users);

        //Map定义
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("No","123");
        dataMap.put("address","深圳");
        model.addAttribute("dataMap",dataMap);

        //日期
        model.addAttribute("now",new Date());

        //if条件
        model.addAttribute("age",22);

        return "demo1";
    }
}