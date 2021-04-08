package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/10/31 0031 11:32
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/search")
public class SkuController {
    @Autowired
    private SkuService skuService;

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map<String,String> searchMap)throws Exception{
        return  skuService.search(searchMap);
    }

    /**
     * 导入索引库
     * @return
     */
    @GetMapping("/import")
    public Result importData(){
        skuService.importData();
        return new Result(true, StatusCode.OK,"导入数据到索引库中成功！");
    }
}