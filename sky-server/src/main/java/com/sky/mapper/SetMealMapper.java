package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {
    /**
     * 根据类别id查询当前类别套餐数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id=#{id} order by id")
    public Long getCountSetMeal(Long id);

    /**
     * 根据菜品id查询套餐数量
     * @param ids
     * @return
     */
    List<Long> getCountByIdDish(List<Long> ids);
}
