package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingMapper shoppingMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Transactional
    @Override
    public void postShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //查询当前数据在购物车有没有
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //设置用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //System.out.println("购物车里面有什么：" + shoppingCart);
//        查询到的数据
        ShoppingCart getShoppingCart = shoppingMapper.getShoppingCarDao(shoppingCart);
        //购物车没有当前数据
        if (getShoppingCart == null) {
            //插入当前数据到购物车表
            //String dishFlavor = shoppingCartDTO.getDishFlavor();
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();
            //如果当前数据是菜品
            if (dishId != null) {
                DishDTO dishDTO = dishMapper.getDishById(dishId);
                shoppingCart.setAmount(dishDTO.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setImage(dishDTO.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setName(dishDTO.getName());
            } else {
                //如果当前数据是套餐
                Setmeal setMeal = setMealMapper.getByIdSetMeal(setmealId);
                shoppingCart.setAmount(setMeal.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setImage(setMeal.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setName(setMeal.getName());
            }
            //添加数据到购物车表
            shoppingMapper.postShoppingCart(shoppingCart);
        } else {
            //购物车有当前数据
            //通过查询到的数据将购物车相同菜品或套餐数量+1
            getShoppingCart.setNumber(getShoppingCart.getNumber() + 1);
            //将查询到的数据通过id更新数量
            shoppingMapper.putByNumberShoppingCar(getShoppingCart);
        }
    }

    /**
     * 删除购物车的一个商品
     *
     * @param shoppingCartDTO
     */
    @Transactional
    @Override
    public void deleteOneByUserIdShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCartDTO.getDishId();
        String dishFlavor = shoppingCartDTO.getDishFlavor();
        Long setmealId = shoppingCartDTO.getSetmealId();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);
        //查询购物车中的商品数量
        ShoppingCart shoppingCarDao = shoppingMapper.getShoppingCarDao(shoppingCart);
        Integer number = shoppingCarDao.getNumber();
        //如果数量为1就删除
        if (number <= 1) {
            //获得查询到的购物车id
            Long shoppingCarId = shoppingCarDao.getId();
            //删除当前id的数据
            shoppingMapper.deleteOneByUserIdShoppingCart(shoppingCarId);
        } else {
            //如果比1大就减一
            shoppingCarDao.setNumber(shoppingCarDao.getNumber() - 1);
            shoppingMapper.putByNumberShoppingCar(shoppingCarDao);
        }
    }

    /**
     * 清空购物车
     */
    @Override
    public void deleteAllByUserIdShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingMapper.deleteAllByUserIdShoppingCart(userId);
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        return shoppingMapper.getByUserIdShoppingCart(userId);
    }
}
