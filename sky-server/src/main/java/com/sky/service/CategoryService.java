package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.ArrayList;

public interface CategoryService {

    /**
     * 分类相关接口分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQueryCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    ArrayList<Category> queryByTypeCategory(Integer type);

    /**
     * 启用或禁用分类
     * @param status
     * @param id
     */
    void putStatusCategory(Integer status, Long id);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void insertCategory(CategoryDTO categoryDTO);

    /**
     * 修改分类
     * @param categoryDTO
     */
    void putTypeCategory(CategoryDTO categoryDTO);

    /**
     * 通过id进行删除分类
     * @param id
     * @return
     */
    void deleteByIdCategory(Long id);
}
