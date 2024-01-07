package com.liyifu.clothesdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liyifu.clothesdp.model.entity.ShopType;

import java.util.List;

/**
* @author liyifu
* @description 针对表【shop_type】的数据库操作Service
* @createDate 2024-01-07 10:47:02
*/
public interface ShopTypeService extends IService<ShopType> {

    /**
     * 查询商铺类型，然后排序
     */
    List<ShopType> queryShopTypeList();

}
