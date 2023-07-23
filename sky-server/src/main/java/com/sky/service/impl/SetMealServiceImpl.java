package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.ProviderException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Handler;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void postSetMeal(SetmealDTO setmealDTO) {
        //添加套餐表数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.postSetMeal(setmeal);
        Long setmealId = setmeal.getId();
        //添加套餐包含菜品表数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
                setMealMapper.postSetMealDishes(setmealDish);
            }
        }
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult getPageQuerySetMeal(SetmealPageQueryDTO setmealPageQueryDTO) {
        //分页查找添加分页操作
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        //获得分页查询的数据
        Page<SetmealVO> setmealVOPage = setMealMapper.getPageQuerySetMeal(setmealPageQueryDTO);
        //获得总查询数
        long total = setmealVOPage.getTotal();
        //获得查询的数据
        List<SetmealVO> setmealVOList = setmealVOPage.getResult();
        return new PageResult(total, setmealVOList);
    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteSetMeal(List<Long> ids) {
        //判断能否删除
        for (Long id : ids) {
            Setmeal setmeal = setMealMapper.getByIdSetMeal(id);
            Integer status = setmeal.getStatus();
            if (StatusConstant.ENABLE.equals(status)) {
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        //删除套餐表中的数据
        for (Long id : ids) {
            setMealMapper.deleteByIdSetMeal(id);
            //通过套餐的id删除套餐菜品关系表中的数据
            setMealMapper.deleteByIdSetMealWithDish(id);
        }
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Transactional
    @Override
    public SetmealVO getByIdSetMeal(Long id) {
        //查询套餐表中的数据
        Setmeal setMeal = setMealMapper.getByIdSetMeal(id);
        //根据套餐id查询套餐菜品表中的数据
        List<SetmealDish> setmealDishes = setMealMapper.getSetMealWithDishBySetMealId(id);
        //将查询到的所有数据封装好一起
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setMeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐和套餐菜品表
     *
     * @param setmealVO
     */
    @Transactional
    @Override
    public void putSetMeal(SetmealVO setmealVO) {
        //修改后的套餐下必须有菜品
        List<SetmealDish> setmealDishes = setmealVO.getSetmealDishes();
        if (setmealDishes == null || setmealDishes.size() == 0) {
            throw new ProviderException(MessageConstant.CATEGORY_NOT_FOUND);
        }
        Long setmealVOId = setmealVO.getId();
        //更新套餐表
        setmealVO.setUpdateTime(LocalDateTime.now());
        System.out.println("我的setmealVO：" + setmealVO);
        setMealMapper.putSetMeal(setmealVO);
        //更新套餐菜品表
        setMealMapper.deleteByIdSetMealWithDish(setmealVOId);
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealVOId);
            setMealMapper.postSetMealDishes(setmealDish);
        }
    }

    /**
     * 修改起售或停售状态
     *
     * @param status
     * @param id     套餐的id
     */
    @Transactional
    @Override
    public void postStatusSetMeal(Integer status, Long id) {
        //如果正在停售套餐中有要停售的菜品则不能起售
        //获得套餐菜品表
        List<SetmealDish> setmealDishes = setMealMapper.getSetMealWithDishBySetMealId(id);
        System.out.println("获得套餐菜品表" + setmealDishes);
        for (SetmealDish setmealDish : setmealDishes) {
            //获得菜品id
            Long dishId = setmealDish.getDishId();
            System.out.println("获得菜品id" + dishId);
            //获得菜品表
            DishDTO dishDTO = dishMapper.getDishById(dishId);
            //如果通过菜品id获得的菜品表中没有菜品则删除套餐下的菜品id
            if (dishDTO == null) {
                setMealMapper.deleteByDishIdSetMealWithDish(dishId);
                continue;
            }
            System.out.println("获得菜品表" + dishDTO);
            //获得菜品状态
            Integer dishStatus = dishDTO.getStatus();
            //判断菜品是否停售
            if (StatusConstant.DISABLE.equals(dishStatus) && StatusConstant.ENABLE.equals(status)) {
                //如果包含了未起售的菜品并且正在停售中则不能起售
                throw new BaseException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        //修改起售或停售状态
        SetmealVO setmealVO = new SetmealVO();
        setmealVO.setId(id);
        setmealVO.setStatus(status);
        setmealVO.setUpdateTime(LocalDateTime.now());
        setMealMapper.putSetMeal(setmealVO);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setMealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setMealMapper.getDishItemBySetmealId(id);
    }
}
