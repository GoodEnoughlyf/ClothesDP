package com.liyifu.clothesdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesdp.exception.MyException;
import com.liyifu.clothesdp.model.entity.ShopType;
import com.liyifu.clothesdp.mapper.ShopTypeMapper;
import com.liyifu.clothesdp.service.ShopTypeService;
import com.liyifu.clothesdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.liyifu.clothesdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;
import static com.liyifu.clothesdp.utils.RedisConstants.CACHE_SHOP_TYPE_TTL;

/**
 * @author liyifu
 * @description 针对表【shop_type】的数据库操作Service实现
 * @createDate 2024-01-07 10:47:02
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType>
        implements ShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询商铺类型，并排序
     */
    @Override
    public List<ShopType> queryShopTypeList() {
        //1、查询redis收否有缓存
        String jsonShopType = stringRedisTemplate.opsForValue().get(CACHE_SHOP_TYPE_KEY);

        //2、有缓存则将数据解析后返回
        if (StrUtil.isNotBlank(jsonShopType)) {
            List<ShopType> shopTypeList = JSONUtil.toList(jsonShopType, ShopType.class);
            return shopTypeList;
        }

        //3、没有缓存则查询数据库
        QueryWrapper<ShopType> shopTypeQueryWrapper = new QueryWrapper<>();
        shopTypeQueryWrapper.orderByAsc("sort");
        List<ShopType> shopTypeList = this.list(shopTypeQueryWrapper);


        //4、数据库查询不到，报错
        if(shopTypeList==null){
            throw new MyException(401,"没有商铺列表！");
        }

        //5、查到了，将其结果转为json数组放入redis
        jsonShopType= JSONUtil.toJsonStr(shopTypeList);
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_TYPE_KEY,jsonShopType,CACHE_SHOP_TYPE_TTL, TimeUnit.MINUTES);

        return shopTypeList;
    }
}




