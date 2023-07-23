package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private FlavorDishMapper flavorDishMapper;

    /**
     * 新增菜品和口味
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void postDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.postDish(dish);
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors == null || flavors.size() == 0) {
            return;
        }
        flavors.forEach(s -> s.setDishId(id));
        dishMapper.postFlavors(flavors);
    }

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult getPageDish(DishPageQueryDTO dishPageQueryDTO) {
        int page = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();
        //分页查询分页
        PageHelper.startPage(page, pageSize);
        //动态查询获得查询数据
        Page<DishVO> page1 = dishMapper.getPageDish(dishPageQueryDTO);
        long total = page1.getTotal();
        List<DishVO> result = page1.getResult();
        return new PageResult(total, result);
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteByIdsDish(List<Long> ids) {
        //不能删除----正在起售
        for (Long id : ids) {
            Dish dish = dishMapper.getStatusDish(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //不能删除----被套餐关联
        List<Long> listDish = setMealMapper.getCountByIdDish(ids);
        if (listDish != null && listDish.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品之后要删除关联的口味
        for (Long id : ids) {
            dishMapper.deleteDishById(id);
            flavorDishMapper.deleteByDishIdFlavor(id);
        }
    }

    /**
     * 菜品起售和停售
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStopDish(Long status, Long id) {
        dishMapper.startOrStopDish(status, id);
    }

    /**
     * 根据id分页查询菜品和口味
     *
     * @param id
     * @return
     */
    @Transactional
    @Override
    public DishVO getWithFlavorByIdDish(Long id) {
//        获得口味数据
        List<DishFlavor> dishFlavors = dishMapper.getFlavorsById(id);
        System.out.println("获得口味：" + dishFlavors);
//        获得菜品数据
        DishDTO dishDTO = dishMapper.getDishById(id);
//        将数据存储
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dishDTO, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品的基本信息和口味信息
     *
     * @param dishVO
     */
    @Transactional
    @Override
    public void putDish(DishVO dishVO) {
        //修改菜品基本信息
        dishMapper.putBaseDish(dishVO);
        //修改口味信息
        List<DishFlavor> flavors = dishVO.getFlavors();
        Long dishId = dishVO.getId();
        //删除口味
        flavorDishMapper.deleteByDishIdFlavor(dishId);
        if (flavors != null && flavors.size() > 0) {
//            新增口味的菜品id
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            //新增口味
            dishMapper.postFlavors(flavors);
        }
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     */
    @Override
    public List<Dish> getTypeByIdDish(Long categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        return dishMapper.getByCategoryIdDish(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Transactional
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.getByCategoryIdDish(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishMapper.getFlavorsById(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
