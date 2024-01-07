package com.liyifu.clothesdp.controller;

import com.liyifu.clothesdp.common.BaseResponse;
import com.liyifu.clothesdp.common.ResultUtils;
import com.liyifu.clothesdp.model.entity.ShopType;
import com.liyifu.clothesdp.service.ShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shop_type")
public class ShopTypeController {
    @Resource
    private ShopTypeService shopTypeService;

    /**
     * 查询商铺类型，并排序
     * @return
     */
    @GetMapping("/list")
    public BaseResponse queryTypeList(){
        List<ShopType> shopTypes = shopTypeService.queryShopTypeList();
        return ResultUtils.success(shopTypes);
    }

}
