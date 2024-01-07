package com.liyifu.clothesdp.controller;

import com.liyifu.clothesdp.common.BaseResponse;
import com.liyifu.clothesdp.common.ResultUtils;
import com.liyifu.clothesdp.model.dto.LoginFormDTO;
import com.liyifu.clothesdp.model.vo.UserVO;
import com.liyifu.clothesdp.service.UserService;
import com.liyifu.clothesdp.utils.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public BaseResponse sendCode(@RequestParam("phone") String phone){
       userService.sendCode(phone);
       return ResultUtils.success();
    }

    /**
     * 用户登录
     * @param loginFormDTO
     * @return
     */
    @PostMapping("/login")
    public BaseResponse userLogin(@RequestBody LoginFormDTO loginFormDTO){
        String token = userService.userLogin(loginFormDTO);
        return ResultUtils.success(token);
    }

    @GetMapping("/getLoginUser")
    public BaseResponse getLoginUser(){
        UserVO user = UserThreadLocal.getUser();
        if(user==null){
            return ResultUtils.error(402,"未登录！");
        }
        return ResultUtils.success(user);
    }
}
