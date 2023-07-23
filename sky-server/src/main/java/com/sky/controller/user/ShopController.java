package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ResponseBody
@RequestMapping("/user/shop")
@RestController("userShopControl")
@Api(tags = "用户端店铺营业状态接口类")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private final static String key = "SHOP_STATUS";

    /**
     * 获得店铺营业状态
     *
     * @return
     */
    @ApiOperation("获得店铺营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatusShop() {
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        log.info("获得店铺营业状态:{}", status);
        return Result.success(status);
    }
}
