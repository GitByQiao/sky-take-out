package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealMapper {
    @Select("select count(id) from setmeal where category_id=#{id} order by id")
    public Long getCountSetMeal(Long id);
}
