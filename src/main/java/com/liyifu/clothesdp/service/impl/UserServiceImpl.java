package com.liyifu.clothesdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesdp.exception.MyException;
import com.liyifu.clothesdp.model.dto.LoginFormDTO;
import com.liyifu.clothesdp.model.entity.User;
import com.liyifu.clothesdp.mapper.UserMapper;
import com.liyifu.clothesdp.model.vo.UserVO;
import com.liyifu.clothesdp.service.UserService;
import com.liyifu.clothesdp.utils.RedisConstants;
import com.liyifu.clothesdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.liyifu.clothesdp.utils.RedisConstants.*;

/**
* @author liyifu
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-01-07 10:47:02
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送验证码
     * @param phone
     */
    @Override
    public void sendCode(String phone) {
        //1、验证手机号是否符合规范
        boolean phoneInvalid = RegexUtils.isPhoneInvalid(phone);
        if(phoneInvalid){
            //2、不规范，报错
            throw new MyException(401,"手机号码不符合规范!");
        }

        //3、规范，生成验证码
        String code = RandomUtil.randomNumbers(6);

        //4、将验证码保存在redis中
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY +phone,code,LOGIN_CODE_TTL, TimeUnit.MINUTES);

        //5、发送验证码  (这里没有短信服务，就简化在日志中输出验证码)
        log.debug("验证码:{}",code);
    }

    /**
     * 用户登录
     * @param loginFormDTO
     * @return
     */
    @Override
    public String userLogin(LoginFormDTO loginFormDTO) {
        //1、校验手机号
        String phone = loginFormDTO.getPhone();
        if(RegexUtils.isPhoneInvalid(phone)){
            throw new MyException(401,"手机号不符合规范!");
        }

        //2、校验验证码
        String code = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if(StrUtil.isBlank(loginFormDTO.getCode()) || !code.equals(loginFormDTO.getCode())){
            throw new MyException(401,"验证码错误！");
        }

        //3、在数据库中查询用户是否存在
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq("phone",phone);
        User user = this.getOne(userQueryWrapper);

        //4、不存在，注册当前用户
        if(user==null){
           user=createWithPhone(phone);
        }

        //5、存在，将当前用户脱敏后保存到redis
            //5、1 脱敏
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            //5、2 生成随机token作为登录令牌
        String token = UUID.randomUUID().toString(true);
            //5、3 将脱敏后的用户信息封装
        Map<String, String> userMap = new HashMap<>();
        userMap.put("id",userVO.getId().toString());
        userMap.put("nickName",userVO.getNickName());
            //5、4 存入redis
        String key=LOGIN_USER_KEY+token;
        stringRedisTemplate.opsForHash().putAll(key,userMap);
            //5、5 设置redis有效期
        stringRedisTemplate.expire(key,LOGIN_USER_TTL,TimeUnit.MINUTES);

        //6、将token返回给客户端
        return token;
    }

    /**
     * 根据手机号注册用户
     * @param phone
     * @return
     */
    private User createWithPhone(String phone){
        User user = new User();
        user.setPhone(phone);
        user.setNickName("user_"+RandomUtil.randomNumbers(10));
        this.save(user);
        return user;
    }
}




