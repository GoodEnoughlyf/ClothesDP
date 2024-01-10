package com.liyifu.clothesdp.controller;

import com.liyifu.clothesdp.common.BaseResponse;
import com.liyifu.clothesdp.common.ResultUtils;
import com.liyifu.clothesdp.model.entity.Voucher;
import com.liyifu.clothesdp.service.VoucherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Resource
    private VoucherService voucherService;

    /**
     * 根据商铺id查询优惠劵（包括秒杀劵）
     * @param shopId
     * @return
     */
    @GetMapping("/list/{shopId}")
    public BaseResponse queryVoucherByShopId(@PathVariable("shopId") Long shopId){
        List<Voucher> vouchers = voucherService.queryVoucherOfShop(shopId);
        return ResultUtils.success(vouchers);
    }

    /**
     * 新增普通劵
     * @return
     */
    @PostMapping("/add/voucher")
    public BaseResponse addVoucher(@RequestBody Voucher voucher){
        voucherService.save(voucher);
        return ResultUtils.success(voucher.getId());
    }

    /**
     * 新增秒杀劵
     * @return
     */
    @PostMapping("/add/secskill")
    public BaseResponse addSecSkill(@RequestBody Voucher voucher){
        Long id = voucherService.addSeckillVoucher(voucher);
        return ResultUtils.success(id);
    }
}
