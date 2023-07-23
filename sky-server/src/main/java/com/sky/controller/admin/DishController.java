package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@ResponseBody
@Slf4j
@Api(tags = "菜品相关接口类")
@RequestMapping(value = "/admin/dish")
public class DishController {
    private static final String DISH_ALL = "dish_*";
    private static final String DISH_ = "dish_";
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品和口味
     *
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "新增菜品和口味")
    @PostMapping
    public Result postDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品和口味：{}", dishDTO);
        //修改菜品和口味
        dishService.postDish(dishDTO);
        //获得菜品分类id
        Long categoryId = dishDTO.getCategoryId();
        //删除redis缓存
        redisTemplate.delete(DISH_ + categoryId);
        return Result.success();
    }

    /**
     * 根据id分页查询菜品和口味
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id分页查询菜品和口味")
    @GetMapping("/{id}")
    public Result<DishVO> getWithFlavorByIdDish(@PathVariable Long id) {
        log.info("根据id分页查询菜品：{}", id);
        DishVO dishVO = dishService.getWithFlavorByIdDish(id);
        return Result.success(dishVO);
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> getPageDish(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageDish = dishService.getPageDish(dishPageQueryDTO);
        return Result.success(pageDish);
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @ApiOperation("批量删除菜品")
    @DeleteMapping
    public Result deleteByIdsDish(@RequestParam List<Long> ids) {
        log.info("批量删除菜品：{},", ids);
        dishService.deleteByIdsDish(ids);
        //查找所有dish_*符合命名的菜品键
        Set keys = redisTemplate.keys(DISH_ALL);
        //删除所有菜品缓存
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
        return Result.success();
    }

    /**
     * 菜品起售和停售
     *
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("菜品起售和停售")
    @PostMapping("/status/{status}")
    public Result startOrStopDish(@PathVariable Long status, Long id) {
        dishService.startOrStopDish(status, id);
        //查找所有dish_*符合命名的菜品键
        Set keys = redisTemplate.keys(DISH_ALL);
        //删除所有菜品缓存
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
        return Result.success();
    }

    /**
     * 修改菜品
     *
     * @param dishVO
     * @return
     */
    @ApiOperation("修改菜品")
    @PutMapping
    public Result putDish(@RequestBody DishVO dishVO) {
        log.info("修改菜品：{}", dishVO);
        dishService.putDish(dishVO);
        //查找所有dish_*符合命名的菜品键
        Set keys = redisTemplate.keys(DISH_ALL);
        //删除所有菜品缓存
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> getTypeByIdDish(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> dishList = dishService.getTypeByIdDish(categoryId);
        return Result.success(dishList);
    }
}
