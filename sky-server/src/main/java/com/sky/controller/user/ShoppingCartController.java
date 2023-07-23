package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/user/shoppingCart")
@Api(tags = "用户购物车接口")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation(value = "添加购物车")
    @PostMapping("/add")
    public Result postShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车：{}", shoppingCartDTO);
        shoppingCartService.postShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @ApiOperation("查看购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> getShoppingCart() {
        log.info("查询购物车");
        List<ShoppingCart> shoppingCarts = shoppingCartService.getShoppingCart();
        return Result.success(shoppingCarts);
    }

    /**
     * 清空某用户购物车
     *
     * @return
     */
    @ApiOperation("清空某用户购物车")
    @DeleteMapping("/clean")
    public Result deleteAllByUserIdShoppingCart() {
        log.info("删除购物车");
        shoppingCartService.deleteAllByUserIdShoppingCart();
        return Result.success();
    }

    @ApiOperation("删除购物车中的一个商品")
    @PostMapping("/sub")
    public Result deleteOneByUserIdShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车中的一个商品:{}", shoppingCartDTO);
        shoppingCartService.deleteOneByUserIdShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
