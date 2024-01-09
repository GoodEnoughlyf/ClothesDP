package com.liyifu.clothesdp.utils;

import cn.hutool.json.JSONUtil;
import com.liyifu.clothesdp.model.dto.RedisData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 封装Redis工具类
 *  todo 完成封装redis工具类
 */
@Component
public class CacheClient {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将数据加入缓存
     */
    public void set(String key, Object value, Long time, TimeUnit timeUnit){
        //数据转为json格式存储
        String jsonValue = JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key,jsonValue,time,timeUnit);
    }


    /**
     * 将数据加入缓存，并设置逻辑过期时间
     */
    public void setWithLogicalExpire(String key,Object value,Long time,TimeUnit timeUnit){
        //使用封装好的类，里面包括数据和逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusDays(timeUnit.toDays(time)));

        String jsonValue = JSONUtil.toJsonStr(redisData);
        stringRedisTemplate.opsForValue().set(key,jsonValue); //不用设置key的有效期，等到活动结束在手动删除即可
    }

    /**
     * 根据id查询，解决缓存击穿问题
     */

    /**
     * 根据id查询，解决缓存穿透问题
     */

    /**
     * 尝试获取锁
     */

    /**
     * 释放锁
     */
}
