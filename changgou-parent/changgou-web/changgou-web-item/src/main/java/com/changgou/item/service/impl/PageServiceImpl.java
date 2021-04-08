package com.changgou.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.item.service.PageService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/11/4 0004 20:33
 */
@Service
public class PageServiceImpl implements PageService {

    /***
     * 生成静态页引擎
     */
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    //生成静态页存储路径
    @Value("${pagepath}")
    private String pagepath;

    /***
     * 查询Spu、List<Sku>、三个分类信息
     * @param spuId
     */
    public Map<String,Object> buildDataModel(Long spuId){

        //查询Spu
        Result<Spu> spuResult = spuFeign.findById(spuId);
        Spu spu = spuResult.getData();
        //查询三个分类信息
        Result<Category> category1Result = categoryFeign.findById(spu.getCategory1Id());
        Result<Category> category2Result = categoryFeign.findById(spu.getCategory2Id());
        Result<Category> category3Result = categoryFeign.findById(spu.getCategory3Id());
        //查询List<Sku>
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        Result<List<Sku>> skuListResult = skuFeign.findList(sku);

        Map<String,Object> dataMap = new HashMap<String, Object>();
        dataMap.put("spu",spu);
        dataMap.put("sku",skuListResult.getData());
        dataMap.put("category1",category1Result.getData());
        dataMap.put("category2",category2Result.getData());
        dataMap.put("category3",category3Result.getData());
        //处理图片
        dataMap.put("images",spu.getImages().split(","));
        dataMap.put("specificationList", JSON.parseObject(spu.getSpecItems(),Map.class));

        //spec_items

        return dataMap;
    }

    /***
     * 生成静态页
     * @param spuid
     */
    @Override
    public void createHtml(Long spuid) {
        try {
            //创建一个容器对象，用于存储页面所需的变量信息 Context
            Context context = new Context();
            //查询所需数据
            Map<String,Object> dataMap = buildDataModel(spuid);
            context.setVariables(dataMap);

            String path = PageServiceImpl.class.getResource("/").getPath()+"/items";

            //判断当前目录是否存在，不存在则创建
            //File dir = new File(pagepath);
            File dir = new File(path);
            if(!dir.exists()){
                dir.mkdirs();
            }

            //创建一个FieldWriter对象，并指定生成的静态页文件全路径
            //FileWriter fieldWriter = new FileWriter(pagepath+"/"+spuid+".html");
            FileWriter fieldWriter = new FileWriter(path+"/"+spuid+".html");

            /***
             * 执行生成操作
             *1.指定模板
             * 2.模板所需的数据类型
             * 3.输出文件对象(文件生成到哪里去)
             */
            templateEngine.process("item",context,fieldWriter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}