---
--- 基于lua判断秒杀库存，一人一单
---
---
-- 1、参数列表
--  1、1 判断秒杀库存，首先需要获取库存的key，库存key=固定前缀1+优惠卷id ，因此需要获取优惠卷id
local voucherId=ARGV[1]

--  1、2 实现一人一单，需要判断用户id是否存在redis的set的value中， 因此需要获取用户id
local userId=ARGV[2]

-- 2、key
--    2、1 库存key   其value为 库存数量
local stockKey='seckill:stock:'..voucherId
--    2、2 订单key  其value为 保存购买了该优惠卷的用户id
local orderKey='seckill:order'..voucherId

-- 3、脚本业务
--      3、1 判断库存是否充足
if(tonumber(redis.call('get',stockKey))<=0) then
--     库存不足
    return 1
end
--      3、2 判断用户是否下单
if (redis.call('sismember',orderKey,userId)==1) then
--    用户已下单
    return 2
end
--      3、3 扣库存
redis.call('incrby',stockKey,-1)
--      3、4 下单（保存用户id到redis的set中）
redis.call('sadd',orderKey,userId)
return 0
