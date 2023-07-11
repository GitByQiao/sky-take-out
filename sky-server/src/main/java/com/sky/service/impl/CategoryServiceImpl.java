package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * 分类相关接口分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueryCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        int page = categoryPageQueryDTO.getPage();
        int pageSize = categoryPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);
        Page<Category> categories = categoryMapper.pageQueryCategory(categoryPageQueryDTO);
        long total = categories.getTotal();
        List<Category> result = categories.getResult();
        return new PageResult(total, result);
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @Override
    public ArrayList<Category> queryByTypeCategory(Integer type) {
        ArrayList<Category> categories = categoryMapper.queryByTypeCategory(type);
        return categories;
    }

    /**
     * 启用或禁用分类
     *
     * @param status
     * @param id
     */
    @Override
    public void putStatusCategory(Integer status, Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setId(id);
        categoryMapper.putCategory(category);
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    @Override
    public void insertCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.insertCategory(category);
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    @Override
    public void putTypeCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.putCategory(category);
    }

    /**
     * 通过id删除分类
     *
     * @param id
     */
    @Override
    public void deleteByIdCategory(Long id) {
        String msg = "";
        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Long countDish = dishMapper.getCountDish(id);
        if (countDish > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(
                    MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        Long countSetMeal = setMealMapper.getCountSetMeal(id);
        if (countSetMeal > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(
                    MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        //删除分类数据
        categoryMapper.deleteByIdCategory(id);
    }
}
