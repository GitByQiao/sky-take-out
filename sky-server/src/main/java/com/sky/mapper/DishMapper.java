package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询所有
     *
     * @param id
     * @return
     */
    @Select("select count(id) from dish where category_id=#{id} order by id")
    Long getCountDish(Long id);

    /**
     * 新增菜品
     *
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void postDish(Dish dish);

    /**
     * 新增口味
     *
     * @param flavors
     */
    void postFlavors(List<DishFlavor> flavors);

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> getPageDish(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 通过id查询菜品状态
     *
     * @param id
     * @return
     */
    @Select("select *from dish where id =#{id}")
    Dish getStatusDish(Long id);

    /**
     * 通过id删除菜品
     *
     * @param id
     */
    @Delete("delete from dish where id=#{id}")
    void deleteDishById(Long id);

    /**
     * 菜品起售和停售
     *
     * @param status
     * @param id
     */
    void startOrStopDish(Long status, Long id);

    /**
     * 根据菜品id查询口味
     *
     * @param id
     * @return
     */
    @Select("select *from dish_flavor where dish_id=#{id}")
    List<DishFlavor> getFlavorsById(Long id);

    /**
     * 根据菜品id查询菜品
     *
     * @param id
     * @return
     */
    @Select("select *from dish where id=#{id}")
    DishDTO getDishById(Long id);

    /**
     * 修改菜品基本信息
     *
     * @param dishVO
     */
    void putBaseDish(DishVO dishVO);

}
