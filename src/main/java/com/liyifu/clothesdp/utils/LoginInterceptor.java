package com.liyifu.clothesdp.utils;

import com.liyifu.clothesdp.model.vo.UserVO;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录拦截器
 *
 * 拦截设置好的请求，某些页面需要用户登录后才能访问
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //查询ThreadLocal中是否存在用户 ，不存在则拦截
        UserVO user = UserThreadLocal.getUser();
        if(user==null){
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
