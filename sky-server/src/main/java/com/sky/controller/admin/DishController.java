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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@Slf4j
@Api(tags = "菜品相关接口类")
@RequestMapping(value = "/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

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
        dishService.postDish(dishDTO);
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
        return Result.success();
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public Result putDish(@RequestBody DishVO dishVO) {
        log.info("修改菜品：{}", dishVO);
        dishService.putDish(dishVO);
        return Result.success();
    }
}
