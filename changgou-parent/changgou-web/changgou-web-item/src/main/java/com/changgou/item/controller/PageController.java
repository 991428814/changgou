package com.changgou.item.controller;

import com.changgou.item.service.PageService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/4 0004 20:27
 */
@RestController
@RequestMapping(value = "/page")
public class PageController {

    @Autowired
    private PageService pageService;


    /**
     * 生成静态页面
     * @param id
     * @return
     */
    @RequestMapping("/createHtml/{id}")
    public Result createHtml(@PathVariable(name="id") Long id){
        pageService.createHtml(id);
        return new Result(true, StatusCode.OK,"ok");
    }
}