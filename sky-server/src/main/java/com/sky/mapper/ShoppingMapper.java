package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingMapper {
    /**
     * 通过用户id，菜品id，套餐id，口味等查询购物车数据
     *
     * @param shoppingCart
     * @return
     */
    ShoppingCart getShoppingCarDao(ShoppingCart shoppingCart);

    /**
     * 添加购物车数据
     *
     * @param shoppingCart
     */
    void postShoppingCart(ShoppingCart shoppingCart);

    /**
     * 更新购物车相同订单数量
     *
     * @param ShoppingCart
     */
    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void putByNumberShoppingCar(ShoppingCart ShoppingCart);

    /**
     * 通过用户id查询购物车
     *
     * @param userId
     * @return
     */
    @Select("select * from shopping_cart where user_id=#{userId}")
    List<ShoppingCart> getByUserIdShoppingCart(Long userId);

    /**
     * 清空某用户购物车
     *
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id=#{userId}")
    void deleteAllByUserIdShoppingCart(Long userId);

    /**
     * 删除某用户购物车某个商品
     *
     * @param shoppingCarId
     */
    @Delete("delete from shopping_cart where id=#{shoppingCarId}")
    void deleteOneByUserIdShoppingCart(Long shoppingCarId);
}
