package com.liyifu.clothesdp.controller;

import com.liyifu.clothesdp.common.BaseResponse;
import com.liyifu.clothesdp.common.ResultUtils;
import com.liyifu.clothesdp.service.VoucherOrderService;
import com.liyifu.clothesdp.service.VoucherService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    private VoucherOrderService voucherOrderService;

    /**
     * 购买秒杀劵
     * @param voucherId
     * @return
     */
    @PostMapping("/secskill/{id}")
    public BaseResponse secskillVoucherOrder(@PathVariable("id") Long voucherId){
        Long orderId = voucherOrderService.secSkillVoucherOrder(voucherId);
        return ResultUtils.success(orderId);
    }

}
