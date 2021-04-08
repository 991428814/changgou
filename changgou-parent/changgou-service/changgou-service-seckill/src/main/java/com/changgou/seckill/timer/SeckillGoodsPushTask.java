package com.changgou.seckill.timer;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author chx
 * @version 1.0
 * @description: 定时将秒杀商品存入到Redis缓存
 * @date 2020/12/27 0027 14:35
 */
@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/5 * * * * ?")
    public void loadGoodsPushRedis() throws ParseException {

        String str="2019-09-14 08:01:01";

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date =sdf.parse(str);

        //求时间菜单
        List<Date> dateMenus= DateUtil.getDateMenus(date);

        //循环查询每个时间区间的秒杀商品
        for (Date dateMenu : dateMenus) {
            //yyyyMMddHH
            String timespace = "SeckillGoods_"+DateUtil.date2Str(dateMenu,"yyyyMMddHH");
            /****
             * 1.审核状态->审核通过 status:1
             * 2.秒杀商品库存>0
             * 3.开始时间 start_time，结束时间end_time
             *  now()<=end_time
             *  时间:时间菜单
             *  时间菜单的开始时间=<start_time &&  end_time<时间菜单的开始时间+2
             */
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();

            //审核状态->审核通过 status:1
            criteria.andEqualTo("status","1");
            //秒杀商品库存>0
            criteria.andGreaterThan("stockCount",0);
            criteria.andGreaterThanOrEqualTo("startTime",dateMenu);
            criteria.andLessThanOrEqualTo("endTime",DateUtil.addDateHour(dateMenu,2));

            /***
             * 排除已经存入到了Redis中的SecKillGoods
             * 1)求出当前命名空间下所有的商品的ID(key)
             * 2)每次查询排除之前存在的商品的key数据
             */
            Set keys = redisTemplate.boundHashOps(timespace).keys();
            if(keys!=null && keys.size()>0){
                criteria.andNotIn("id",keys);
            }

            //查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);


            for (SeckillGoods seckillGood : seckillGoods) {
                System.out.println("商品ID:"+seckillGood.getId()+"---存入到Redis---"+timespace);
                //存入到redis
                redisTemplate.boundHashOps(timespace).put(seckillGood.getId(),seckillGood);
                //给每一个商品做一个队列
                redisTemplate.boundListOps("SeckillGoodsCountList_"+seckillGood.getId()).leftPushAll(putAllIds(seckillGood.getStockCount(),seckillGood.getId()));
            }
        }
    }

    /***
     * 获取每个商品的ID集合
     */
    public Long[] putAllIds(Integer num,Long id){
        Long[] ids = new Long[num];
        for (int i = 0; i < ids.length; i++) {
            ids[i]=id;
        }
        return ids;
    }
}