package com.changgou.controller;

import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/1 0001 16:28
 */
@Controller
@RequestMapping(value = "/search")
public class SkuController {

    @Autowired
    private SkuFeign skuFeign;

    /***
     * 实现搜索调用
     */
    @GetMapping(value = "/list")
    public String search(@RequestParam(required = false)Map<String,String>searchMap, Model model)throws Exception{

        //替换特殊字符
        handlerSearchMap(searchMap);

        //调用搜索微服务
        Map resultMap = skuFeign.search(searchMap);

        model.addAttribute("result",resultMap);


        Page<SkuInfo> pageInfo = new Page<SkuInfo>(
                Long.parseLong(resultMap.get("total").toString()),
                Integer.parseInt(resultMap.get("pageNumber").toString())+1,
                Integer.parseInt(resultMap.get("pageSize").toString()));

        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("searchMap",searchMap);

        //获取上次请求地址
        String[] urls = url(searchMap);

        //两个url url1:不带排序 url2:带排序
        model.addAttribute("url",urls[0]);
        model.addAttribute("sorturl",urls[1]);
        return "search";
    }

    /***
     * 拼接组装用户请求的url
     * 获取用户每次请求的地址
     * 页面需要在这次请求的地址上面添加额外的搜索条件
     * http://localhost:18086/search/list
     * http://localhost:18086/search/list?keywords=华为
     * http://localhost:18086/search/list?keywords=华为&brand=华为
     */
    public String[] url(Map<String,String>searchMap){
        String url = "/search/list"; //初始化地址
        String sorturl = "/search/list"; //初始化地址

        if (searchMap!=null && searchMap.size()>0){
            url+="?";
            sorturl+="?";
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                //跳过分页参数
                if (key.equalsIgnoreCase("pageNum")){
                    continue;
                }

                url+=key+"="+value+"&";
                if (key.equalsIgnoreCase("sortField")||key.equalsIgnoreCase("sortRule")){
                    continue;
                }
                sorturl+=key+"="+value+"&";

            }

            //去掉最后一个&
            url=url.substring(0,url.length()-1);
            sorturl=sorturl.substring(0,sorturl.length()-1);
        }

        return new String[]{url,sorturl};
    }

    /***
     * 替换特殊字符
     */
    public void handlerSearchMap(Map<String,String>searchMap){
        if(searchMap!=null){
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                if(entry.getKey().startsWith("spec_")){
                    entry.setValue(entry.getValue().replace("+","%2B"));
                }
            }
        }
    }
}