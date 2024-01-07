package com.liyifu.clothesdp.utils;

/**
 * redis中的key常量
 */
public class RedisConstants {

    //redis中存放验证码的key前缀
    public static final String LOGIN_CODE_KEY="login:code:";

    //redis中验证码存放时间
    public static final Long LOGIN_CODE_TTL = 2L;

    //redis存放用户登录token
    public static final String LOGIN_USER_KEY = "login:token:";
    //为了方便测试，这里将ttl设置久一点
    public static final Long LOGIN_USER_TTL = 600000L;
}
