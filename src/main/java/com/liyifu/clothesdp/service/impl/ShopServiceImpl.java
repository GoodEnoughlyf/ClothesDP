package com.liyifu.clothesdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesdp.model.entity.Shop;
import com.liyifu.clothesdp.mapper.ShopMapper;
import com.liyifu.clothesdp.service.ShopService;
import com.liyifu.clothesdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.liyifu.clothesdp.utils.RedisConstants.CACHE_SHOP_KEY;
import static com.liyifu.clothesdp.utils.RedisConstants.CACHE_SHOP_TTL;

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

    /**
     * 根据id查询商铺信息
     * @param id
     * @return
     */
    @Override
    public Shop queryById(Long id) {
        //1、查询redis是否有缓存
        String jsonShop = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        //2、有缓存，判断value是否为空值
            //2、1 不为空值，直接返回
        if(StrUtil.isNotBlank(jsonShop)){
            Shop shop = JSONUtil.toBean(jsonShop, Shop.class);
            return shop;
        }
            //2、2 为空值，返回redis中的商铺信息
        if(jsonShop!=null){
            return null;
        }

        //3、没有缓存，查询数据库，判断数据是否存在
        Shop shop = this.getById(id);
            //3、1 不存在，将其空值“”写入redis，返回空值
        if(shop==null){
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,"",CACHE_SHOP_TTL,TimeUnit.MINUTES);
            return null;
        }
            //3、2 存在，将其缓存到redis，返回商铺信息
        jsonShop = JSONUtil.toJsonStr(shop);
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,jsonShop,CACHE_SHOP_TTL, TimeUnit.MINUTES);

        return shop;
    }

    /**
     * 修改商铺信息
     * @param shop
     */
    @Transactional
    @Override
    public void updateShop(Shop shop) {
        //1、先操作数据库
        this.updateById(shop);

        //2、再删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY+shop.getId());
    }
}




