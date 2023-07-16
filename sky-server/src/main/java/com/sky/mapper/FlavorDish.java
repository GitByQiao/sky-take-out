package com.sky.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FlavorDish {

    /**
     * 通过菜品id删除口味
     *
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteByDishIdFlavor(Long id);
}
