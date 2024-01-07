package com.liyifu.clothesdp.utils;

import com.liyifu.clothesdp.model.vo.UserVO;

/**
 * ThreadLocal线程变量
 */
public class UserThreadLocal {
   private static final ThreadLocal<UserVO> tl=new ThreadLocal<>();

    /**
     * 存放用户信息到ThreadLocal
     * @param userVO
     */
   public static void saveUser(UserVO userVO){
       tl.set(userVO);
   }

    /**
     * 获取ThreadLocal中的用户信息
     * @return
     */
   public static UserVO getUser(){
       return tl.get();
   }

    /**
     * 删除ThreadLocal中的信息
     */
   public static void removeUser(){
       tl.remove();
   }
}
