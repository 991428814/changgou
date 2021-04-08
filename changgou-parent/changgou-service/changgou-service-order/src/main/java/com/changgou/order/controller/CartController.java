package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/18 0018 19:36
 */
@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /***
     * 加入购物车
     * 1.加入购物车数量
     * 2.商品id
     */
    @GetMapping(value = "/add")
    public Result add(Integer num,Long id){

        Map<String, String> userInfo = TokenDecode.getUserInfo();
        //获取用户登录名
        String username = userInfo.get("username");

        cartService.add(num,id,username);
        return new Result(true, StatusCode.OK,"加入购物车成功!");
    }


    /***
     * 购物车列表
     */
    @GetMapping(value = "/list")
    public Result<List<OrderItem>> list(){

        Map<String, String> userInfo = TokenDecode.getUserInfo();

        //获取用户登录名
        String username = userInfo.get("username");

        //查询购物车列表
        List<OrderItem> orderItems = cartService.list(username);
        return new Result<List<OrderItem>>(true,StatusCode.OK,"购物车列表查询成功!",orderItems);

    }
}