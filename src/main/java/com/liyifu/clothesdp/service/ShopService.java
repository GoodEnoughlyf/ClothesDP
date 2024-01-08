package com.liyifu.clothesdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liyifu.clothesdp.model.entity.Shop;

/**
* @author liyifu
* @description 针对表【shop】的数据库操作Service
* @createDate 2024-01-07 10:47:02
*/
public interface ShopService extends IService<Shop> {
    /**
     * 根据id查询商户
     * @param id
     * @return
     */
    Shop queryById(Long id);

    /**
     * 修改商户信息
     * @param shop
     * @return
     */
    void updateShop(Shop shop);
}
