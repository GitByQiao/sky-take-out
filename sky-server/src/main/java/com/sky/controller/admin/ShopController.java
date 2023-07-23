package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ResponseBody
@RequestMapping("/admin/shop")
@RestController("adminShopControl")
@Api(tags = "管理端店铺营业状态接口类")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String key = "SHOP_STATUS";

    /**
     * 设置店铺营业状态
     *
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result putStatusShop(@PathVariable Integer status) {
        log.info("设置店铺营业状态：{}", status);
        redisTemplate.opsForValue().set(key, status);
        return Result.success();
    }

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
