package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;


public interface SetMealService {

    /**
     * 新增套餐
     */
    void postSetMeal(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult getPageQuerySetMeal(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除套餐
     * @param ids
     */
    void deleteSetMeal(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getByIdSetMeal(Long id);

    /**
     * 修改套餐和套餐菜品表
     * @param setmealVO
     */
    void putSetMeal(SetmealVO setmealVO);

    /**
     * 修改起售或停售状态
     * @param status
     * @param id
     */
    void postStatusSetMeal(Integer status, Long id);
}
