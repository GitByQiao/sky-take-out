package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    void postShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     *
     * @return
     */
    List<ShoppingCart> getShoppingCart();

    /**
     * 清空某用户购物车
     */
    void deleteAllByUserIdShoppingCart();

    /**
     * 删除购物车的一个商品
     *
     * @param shoppingCartDTO
     */
    void deleteOneByUserIdShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
