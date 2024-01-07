package com.liyifu.clothesdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liyifu.clothesdp.model.dto.LoginFormDTO;
import com.liyifu.clothesdp.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author liyifu
* @description 针对表【user】的数据库操作Service
* @createDate 2024-01-07 10:47:02
*/
public interface UserService extends IService<User> {

    /**
     * 发送验证码
     * @param phone
     */
    void sendCode(String phone);

    /**
     * 用户登录
     * @param loginFormDTO
     * @return
     */
    String userLogin(LoginFormDTO loginFormDTO);

}
