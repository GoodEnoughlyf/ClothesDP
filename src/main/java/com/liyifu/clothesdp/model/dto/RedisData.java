package com.liyifu.clothesdp.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基于逻辑过期解决缓存击穿问题
 *      将商铺信息和逻辑过期时间进行封装
 */
@Data
public class RedisData {
    Object data;

    LocalDateTime expireTime;
}
