package com.liyifu.clothesdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.liyifu.clothesdp.model.vo.UserVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Struct;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.liyifu.clothesdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.liyifu.clothesdp.utils.RedisConstants.LOGIN_USER_TTL;

/**
 * 刷新拦截器
 *
 * 拦截一切请求，只要用户操作，就会刷新token的TTL
 */
public class RefreshInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate=stringRedisTemplate;
    }

    /**
     * 在请求处理前调用  （也就是对请求做预处理）
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、获取请求头中的token
        String token = request.getHeader("authorization");
            //如果token不存在，直接放行 （针对于访问一些不需要登录的页面）
        if(StrUtil.isBlank(token)){
            return true;
        }
        //2、基于token获取redis中的用户数据
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
            //用户不存在，放行  （就算此时token过期了，也能访问不需要登录的页面）
        if(userMap.isEmpty()){
            return true;
        }
        //3、将用户数据保存在ThreadLocal中
            //3、1 解析userMap中存放的用户信息
        UserVO userVO = BeanUtil.fillBeanWithMap(userMap, new UserVO(), false);
            //3、2 存放在ThreadLocal中
        UserThreadLocal.saveUser(userVO);
        //4、刷新token的TTL
        stringRedisTemplate.expire(LOGIN_USER_KEY+token,LOGIN_USER_TTL, TimeUnit.MINUTES);
        //5、放行
        return true;
    }

    /**
     * 在请求结束后调用  （也就是对资源进行清理）
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.removeUser();
    }
}
