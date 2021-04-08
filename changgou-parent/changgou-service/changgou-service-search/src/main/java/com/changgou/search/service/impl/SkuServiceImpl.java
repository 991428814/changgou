package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/10/31 0031 11:14
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate; //可以实现索引库的增删改查

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        //搜索条件封装
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildBasicQuery(searchMap);

        //集合搜索
        Map<String, Object> resultMap = searchList(nativeSearchQueryBuilder);

//        if (searchMap==null || StringUtil.isEmpty(searchMap.get("category"))){
//            //分类分组查询
//            List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);
//            resultMap.put("categoryList",categoryList);
//        }
//
//        if (searchMap==null || StringUtil.isEmpty(searchMap.get("brand"))) {
//            //查询品牌集合
//            List<String> brandList = searchBrandList(nativeSearchQueryBuilder);
//            resultMap.put("brandList", brandList);
//        }
        //规格汇总合并
//        Map<String, Set<String>> specList = searchSpecList(nativeSearchQueryBuilder);
//        resultMap.put("specList",specList);

        /***
         * 分组搜索实现
         */
        Map<String, Object> groupMap = searchGroupList(nativeSearchQueryBuilder, searchMap);
        resultMap.putAll(groupMap);
        return resultMap;
    }

    /***
     * 分组查询->分类分组、品牌分组、规格分组
     * @param nativeSearchQueryBuilder
     * @return
     */
    public Map<String,Object> searchGroupList(NativeSearchQueryBuilder nativeSearchQueryBuilder,Map<String,String> searchMap) {
        /***
         * 分组查询分类查询
         * addAggregation 添加要给聚合操作
         */

        //定义一个Map存储分组结果
        Map<String,Object> groupMapResult = new HashMap<String,Object>();


        if (searchMap==null || StringUtil.isEmpty(searchMap.get("category"))) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        }
        if (searchMap==null || StringUtil.isEmpty(searchMap.get("brand"))){
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        }
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        if (searchMap==null || StringUtil.isEmpty(searchMap.get("category"))) {
            StringTerms categoryTerms = aggregatedPage.getAggregations().get("skuCategory");
            List<String> categoryList = getGroupList(categoryTerms);
            groupMapResult.put("categoryList",categoryList);
        }
        if (searchMap==null || StringUtil.isEmpty(searchMap.get("brand"))) {
            StringTerms brandTerms = aggregatedPage.getAggregations().get("skuBrand");
            List<String> brandList = getGroupList(brandTerms);
            groupMapResult.put("brandList",brandList);
        }

        //获取分组数据
        StringTerms specTerms = aggregatedPage.getAggregations().get("skuSpec");
        List<String> specList = getGroupList(specTerms);
        Map<String, Set<String>> specMap = putAllSpec(specList);
        groupMapResult.put("specList",specMap);

        return groupMapResult;
    }

    /***
     * 获取分组集合数据
     * @param stringTerms
     * @return
     */
    public List<String> getGroupList(StringTerms stringTerms) {
        List<String> groupList = new ArrayList<String>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String feildName = bucket.getKeyAsString();//其中的一个数据(分类名字)
            groupList.add(feildName);
        }
        return groupList;
    }

    public NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {

        //BoolQuery must,must_not,should
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


        //NativeSearchQueryBuilder搜索条件构建对象,用于封装各种搜索条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        if(searchMap!=null && searchMap.size()>0){
            //根据关键词搜索
            String keywords = searchMap.get("keywords");
            //如果关键词不为空,则搜索关键词
            if (!StringUtil.isEmpty(keywords)){
                //nativeSearchQueryBuilder.withQuery(QueryBuilders.queryStringQuery(keywords).field("name"));
                boolQueryBuilder.must(QueryBuilders.queryStringQuery(keywords).field("name"));
            }

            //输入了分类
            if (!StringUtil.isEmpty(searchMap.get("category"))){
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName",searchMap.get("category")));
            }

            //输入了品牌
            if (!StringUtil.isEmpty(searchMap.get("brand"))){
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
            }

            //规格过滤实现spec_网络 = 3G & spec_颜色 = 红
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                //如果key以spec_开始，则表示规格查询
                if (key.startsWith("spec_")){
                    String value = entry.getValue().replace("//","");
                    boolQueryBuilder.must(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",value));
                }
            }

            String price = searchMap.get("price");
            if (!StringUtil.isEmpty(price)){
                price = price.replace("元","").replace("以上","");
                String[] prices = price.split("-");
                if (prices!=null && prices.length>0){
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(prices[0])));
                    if(prices.length==2){
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(prices[1])));
                    }
                }
            }

            //去掉中文
            //根据-分割 [0,500]  [500,1000]

        }

        //排序实现
        String sortField = searchMap.get("sortField"); //指定排序的域
        String sortRule = searchMap.get("sortRule"); //指定排序的规则
        if (!StringUtil.isEmpty(sortField) && !StringUtil.isEmpty(sortRule)){
            nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sortField).order(SortOrder.valueOf(sortRule)));
        }

        //分页,如果用户不传分页参数，则默认第1页
        Integer pageNum = coverterPage(searchMap);
        Integer size = 15;

        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum-1,size));

        //将boolQueryBuilder填充给nativeSearchQueryBuilder
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        return nativeSearchQueryBuilder;
    }

    /***
     * 接收前端传入的分页参数
     * @param searchMap
     * @return
     */
    public Integer coverterPage(Map<String,String> searchMap){
        Integer pageNum = 1;
        if(searchMap!=null){
            if (!StringUtil.isEmpty(searchMap.get("pageNum"))) {
                try {
                    pageNum = Integer.valueOf(searchMap.get("pageNum"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    pageNum=1;
                }
            }
        }
        return pageNum;
    }

    //结果集搜索
    public Map<String, Object> searchList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {

        //高亮配置
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");//指定高亮域

        //前缀 <em style="color:red;">
        field.preTags("<em style=\"color:red;\">");
        //后缀 </em>
        field.postTags("</em>");
        //碎片长度
        field.fragmentSize(100);//关键词数据的长度
        //添加高亮
        nativeSearchQueryBuilder.withHighlightFields(field);

        //执行搜索
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
               List<T> list = new ArrayList<T>();
                //获取所有数据
                for (SearchHit hit : response.getHits()) {
                    SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(),SkuInfo.class);

                    HighlightField highlightField = hit.getHighlightFields().get("name");

                    if (highlightField!=null && highlightField.getFragments()!=null){
                        Text[] fragments = highlightField.getFragments();
                        StringBuffer buffer = new StringBuffer();
                        for (Text fragment : fragments) {
                            buffer.append(fragment.toString());
                        }
                        skuInfo.setName(buffer.toString());
                    }
                    list.add((T) skuInfo);
                }

                return new AggregatedPageImpl<T>(list,pageable,response.getHits().getTotalHits());
            }
        });

        //分页参数-总记录数
        long totalElements = page.getTotalElements();
        //总页数
        int totalPages = page.getTotalPages();
        //获取数据结果集
        List<SkuInfo> contents = page.getContent();
        //封装一个Map存储所有数据,并返回
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("rows",contents);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);

        //获取搜索封装信息
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        Pageable pageable = query.getPageable();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        //分页数据
        resultMap.put("pageSize",pageSize);
        resultMap.put("pageNumber",pageNumber);
        return resultMap;
    }



    /***
     * 品牌分组查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    public List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /***
         * 分组查询分类查询
         * addAggregation 添加要给聚合操作
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));

        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        List<String> brandList = new ArrayList<String>();
        //获取分组数据
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuBrand");
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String brandName = bucket.getKeyAsString();//其中的一个数据(品牌名字)
            brandList.add(brandName);
        }
        return brandList;
    }

    /***
     * Spec分组查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    public Map<String, Set<String>> searchSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /***
         * 分组查询规格查询
         * addAggregation 添加要给聚合操作
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));

        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        List<String> specList = new ArrayList<String>();
        //获取spec数据
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuSpec");
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String specName = bucket.getKeyAsString();//其中的一个数据(品牌名字)
            specList.add(specName);
        }
        Map<String, Set<String>> allSpec = putAllSpec(specList);
        return allSpec;
    }

    public Map<String, Set<String>> putAllSpec(List<String> specList) {
        //合并后的Map对象
        Map<String, Set<String>> allSpec = new HashMap<String,Set<String>>();
        //1.循环specList
        for (String spec : specList) {
            //2.将每个JSON字符串装成Map
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            //3.将每个Map对象合成一个Map<String,Set<String>>
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                //4.合并流程
                String key = entry.getKey();//规格名字
                String value = entry.getValue();

                //获取当前规格对应的Set数据
                Set<String> specSet = allSpec.get(key);
                if (specSet==null){
                    //表示allSpec中没有该规格
                    specSet = new HashSet<String>();
                }
                specSet.add(value);
                allSpec.put(key,specSet);
            }
        }
        return allSpec;
    }

    /***
     * 导入索引库
     */
    @Override
    public void importData() {
        //Feign调用,查询List<Sku>
        Result<List<Sku>> skuResult = skuFeign.findAll();
        //将List<Sku>转成List<SkuInfo>
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuResult.getData()),SkuInfo.class);

        //循环当前SkuInfoList
        for (SkuInfo skuInfo : skuInfoList) {
            Map<String,Object> specMap = JSON.parseObject(skuInfo.getSpec(),Map.class);
            //动态域实现:Map<String,Object>的每个key都会生成一个域
            skuInfo.setSpecMap(specMap);
        }
        //调用Dao实现数据批量导入
        skuEsMapper.saveAll(skuInfoList);
    }
}