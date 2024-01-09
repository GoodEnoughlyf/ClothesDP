package com.liyifu.clothesdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.liyifu.clothesdp.model.dto.RedisData;
import com.liyifu.clothesdp.model.entity.Shop;
import com.liyifu.clothesdp.service.ShopService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static com.liyifu.clothesdp.utils.RedisConstants.CACHE_SHOP_KEY;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShopServiceImplTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopService shopService;

    /**
     * 将热点shop数据加入缓存，并设置逻辑过期时间
     */
    @Test
    public void saveShop2RedisTest(){
        //1、查询商户数据
        Long id=1L;
        Long expireSeconds=30L;
        Shop shop = shopService.getById(id);

        //2、封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));

        //3、写入redis
        String jsonRedisData = JSONUtil.toJsonStr(redisData);
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,jsonRedisData);
    }

}