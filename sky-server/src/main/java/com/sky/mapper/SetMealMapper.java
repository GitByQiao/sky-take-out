package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {
    /**
     * 根据类别id查询当前类别套餐数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from setmeal where category_id=#{categoryId}")
    Long getCountSetMeal(Long categoryId);

    /**
     * 根据菜品id查询套餐数量
     *
     * @param ids
     * @return
     */
    List<Long> getCountByIdDish(List<Long> ids);

    /**
     * 添加套餐表套餐
     *
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void postSetMeal(Setmeal setmeal);

    /**
     * 添加套菜和菜品联表里面的数据
     *
     * @param setmealDish
     */
    void postSetMealDishes(SetmealDish setmealDish);


    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> getPageQuerySetMeal(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 通过id查询套餐
     *
     * @param id
     * @return
     */
    @Select("select *from setmeal where id=#{id}")
    Setmeal getByIdSetMeal(Long id);

    /**
     * 删除套餐表中的数据
     *
     * @param id
     */
    @Delete("delete from setmeal where id=#{id}")
    void deleteByIdSetMeal(Long id);

    /**
     * 删除套餐菜品表中的数据
     *
     * @param setMealId
     */
    @Delete("delete from setmeal_dish where setmeal_id=#{setMealId}")
    void deleteByIdSetMealWithDish(Long setMealId);

    /**
     * 根据id查询套餐
     *
     * @param setMealId
     * @return
     */
    @Select("select *from setmeal_dish where setmeal_id=#{setMealId}")
    List<SetmealDish> getSetMealWithDishBySetMealId(Long setMealId);

    /**
     * 修改套餐表数据
     *
     * @param setmealVO
     */
    void putSetMeal(SetmealVO setmealVO);

    /**
     * 通过菜品id删除套餐菜品表数据
     *
     * @param dishId
     */
    @Delete("delete from setmeal_dish where dish_id=#{dishId}")
    void deleteByDishIdSetMealWithDish(Long dishId);
}
