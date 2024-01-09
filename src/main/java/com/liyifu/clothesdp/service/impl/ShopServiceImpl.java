package com.liyifu.clothesdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesdp.exception.MyException;
import com.liyifu.clothesdp.model.dto.RedisData;
import com.liyifu.clothesdp.model.entity.Shop;
import com.liyifu.clothesdp.mapper.ShopMapper;
import com.liyifu.clothesdp.service.ShopService;
import com.liyifu.clothesdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.liyifu.clothesdp.utils.RedisConstants.*;

/**
 * @author liyifu
 * @description 针对表【shop】的数据库操作Service实现
 * @createDate 2024-01-07 10:47:02
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop>
        implements ShopService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    // 创建线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 根据id查询商铺信息
     *
     * @param id
     * @return
     */
    @Override
    public Shop queryById(Long id) {
        //  缓存穿透
        //Shop shop = this.queryByIdWithThrough(id);
        //  缓存击穿
        Shop shop = this.queryByIdWithJC(id);

        if(shop==null){
            throw new MyException(401,"商铺不存在！");
        }
        return shop;
    }

    /**
     * 根据id查询，解决缓存穿透问题
     *
     * @param id
     * @return
     */
    public Shop queryByIdWithThrough(Long id) {
        //1、查询redis是否有缓存
        String jsonShop = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        //2、有缓存，判断value是否为空值
        //2、1 不为空值，直接返回
        if (StrUtil.isNotBlank(jsonShop)) {
            Shop shop = JSONUtil.toBean(jsonShop, Shop.class);
            return shop;
        }
        //2、2 为空值，返回redis中的商铺信息
        if (jsonShop != null) {
            return null;
        }

        //3、没有缓存，查询数据库，判断数据是否存在
        Shop shop = this.getById(id);
        //3、1 不存在，将其空值“”写入redis，返回空值
        if (shop == null) {
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, "", CACHE_SHOP_TTL, TimeUnit.MINUTES);
            return null;
        }
        //3、2 存在，将其缓存到redis，返回商铺信息
        jsonShop = JSONUtil.toJsonStr(shop);
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, jsonShop, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        return shop;
    }

    /**
     * 根据id查询，解决缓存击穿问题
     *
     * @param id
     * @return
     */
    public Shop queryByIdWithJC(Long id) {
        //1、查询是否有缓存
        String jsonShop = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        //2、没有缓存，返回空
        if (StrUtil.isBlank(jsonShop)) {
            return null;
        }

        //3、有缓存，判断缓存是否过期
        RedisData redisData = JSONUtil.toBean(jsonShop, RedisData.class);
        JSONObject data = (JSONObject) redisData.getData();
        Shop shop = JSONUtil.toBean(data, Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
            //如果商铺不是热点，那么jsonShop不能转成redisData类，因此shop为null
        if(shop==null){
            return JSONUtil.toBean(jsonShop,Shop.class);
        }
            //3、1 没过期，返回缓存店铺信息
        if (expireTime.isAfter(LocalDateTime.now())) {
            return shop;
        }
            //3、2 过期，尝试获取锁
        String lockKey=LOCK_SHOP_KEY+id;
        boolean isLock = this.tryLock(lockKey);

        //4、获取锁失败，返回过期的缓存
        if(!isLock){
            return shop;
        }

        //5、获取锁成功，开启独立线程，独立线程查询数据库，重建缓存,同时返回过期的缓存
        CACHE_REBUILD_EXECUTOR.submit(()->{
            try {
                saveShop2Redis(id,30L);
            }catch (Exception e){
                throw new MyException(401,"重建缓存失败!");
            }finally {
                this.unLock(lockKey);
            }
        });

        return shop;
    }

    /**
     * 修改商铺信息
     *
     * @param shop
     */
    @Transactional
    @Override
    public void updateShop(Shop shop) {
        //1、先操作数据库
        this.updateById(shop);

        //2、再删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
    }

    /**
     * 尝试获取锁
     * @param key
     * @return
     */
    private boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放锁
     * @param key
     */
    private void unLock(String key){
        stringRedisTemplate.delete(key);
    }

    /**
     * 将热点shop数据存入redis
     */
    public void saveShop2Redis(Long id, Long expireDays) {
        //1、查询商户数据
        Shop shop = this.getById(id);

        //2、封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusDays(expireDays));

        //3、写入redis
        String jsonRedisData = JSONUtil.toJsonStr(redisData);
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, jsonRedisData);
    }


}




