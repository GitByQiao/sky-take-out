package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface CategoryMapper {

    /**
     * 分类相关接口分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQueryCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    ArrayList<Category> queryByTypeCategory(Integer type);

    /**
     * 修改分类管理
     *
     * @param category
     */
    @AutoFill(value = OperationType.UPDATE)
    void putCategory(Category category);

    /**
     * 新增分类
     * @param category
     */
    @Insert("insert into category(" +
            "type, name, sort, status," +
            " create_time, update_time, create_user, update_user)" +
            "VALUES (" +
            "#{type},#{name},#{sort},#{status}," +
            "#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insertCategory(Category category);

    /**
     * 通过id删除分类
     * @param id
     */
    @Delete("delete from category where id=#{id}")
    void deleteByIdCategory(Long id);
}
