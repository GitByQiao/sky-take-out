package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和口味
     *
     * @param dishDTO
     */
    void postDish(DishDTO dishDTO);

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult getPageDish(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    void deleteByIdsDish(List<Long> ids);

    /**
     * 菜品起售和停售
     *
     * @param status
     * @param id
     */
    void startOrStopDish(Long status, Long id);

    /**
     * 根据id分页查询菜品和口味
     *
     * @param id
     * @return
     */
    DishVO getWithFlavorByIdDish(Long id);

    /**
     * 修改菜品
     *
     * @param dishVO
     */
    void putDish(DishVO dishVO);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     */
    List<Dish> getTypeByIdDish(Long categoryId);
}
