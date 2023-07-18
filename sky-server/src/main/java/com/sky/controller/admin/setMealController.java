package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@ResponseBody
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
public class setMealController {
    @Autowired
    private SetMealService setMealService;

    /**
     * 新增套餐
     *
     * @return
     */
    @ApiOperation("新增套餐")
    @PostMapping
    public Result postSetMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setMealService.postSetMeal(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> getPageQuerySetMeal(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setMealService.getPageQuerySetMeal(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @ApiOperation("删除功能")
    @DeleteMapping
    public Result deleteSetMeal(@RequestParam List<Long> ids) {
        log.info("删除功能：{}", ids);
        setMealService.deleteSetMeal(ids);
        return Result.success();
    }


    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getByIdSetMeal(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        SetmealVO setmealVO = setMealService.getByIdSetMeal(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐和套餐菜品表
     *
     * @param setmealVO
     * @return
     */
    @ApiOperation("修改套餐和套餐菜品表")
    @PutMapping
    public Result putSetMeal(@RequestBody SetmealVO setmealVO) {
        log.info("修改套餐和套餐菜品表：{}", setmealVO);
        setMealService.putSetMeal(setmealVO);
        return Result.success();
    }

    @ApiOperation("起售或停售")
    @PostMapping("status/{status}")
    public Result postStatusSetMeal(@PathVariable Integer status, Long id) {
        log.info("起售或停售：套餐状态{},套餐id{}", status, id);
        setMealService.postStatusSetMeal(status, id);
        return Result.success();
    }
}
