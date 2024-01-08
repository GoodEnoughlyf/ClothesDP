package com.liyifu.clothesdp.controller;

import com.liyifu.clothesdp.common.BaseResponse;
import com.liyifu.clothesdp.common.ResultUtils;
import com.liyifu.clothesdp.model.entity.Shop;
import com.liyifu.clothesdp.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private ShopService shopService;

    /**
     * 根据id查询商铺信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public BaseResponse queryShopById(@PathVariable("id") Long id){
        Shop shop = shopService.queryById(id);
        return ResultUtils.success(shop);
    }

    @PostMapping("/update")
    public BaseResponse updateShop(@RequestBody Shop shop){
        shopService.updateShop(shop);
        return ResultUtils.success();
    }
}
