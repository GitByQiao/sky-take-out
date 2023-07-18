package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.properties.AliOssProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Api(tags = "分类相关接口类")
@RestController
@ResponseBody
@Slf4j
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分类相关接口分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @ApiOperation(value = "分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> pageQueryCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQueryCategory(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "根据类型查询分类")
    @GetMapping("/list")
    public Result<ArrayList<Category>> queryByTypeCategory(Integer type) {
        log.info("根据类型进行查询分类参数：{}", type);
        ArrayList<Category> categories = categoryService.queryByTypeCategory(type);
        return Result.success(categories);
    }

    /**
     * 启用或禁用分类
     *
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用禁用分类")
    @PostMapping("/status/{status}")
    public Result putStatusCategory(
            @PathVariable("status") Integer status,
            @RequestParam Long id) {
        log.info("启动禁用分类请求体id：{},地址参数：{}", id, status);
        categoryService.putStatusCategory(status, id);
        return Result.success();
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @ApiOperation("新增分类")
    @PostMapping
    public Result insertCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.insertCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     * @return
     */
    @ApiOperation("修改分类")
    @PutMapping
    public Result putTypeCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.putTypeCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id删除分类")
    @DeleteMapping
    public Result deleteByIdCategory(Long id) {
        log.info("删除：{}", id);
        categoryService.deleteByIdCategory(id);
        return Result.success();
    }
}
