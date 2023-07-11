package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {
    @Select("select count(id) from dish where category_id=#{id} order by id")
    Long getCountDish(Long id);
}
