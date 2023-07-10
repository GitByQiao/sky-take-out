package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {
    /**
     * 添加员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 员工信息分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启动或禁用员工状态栏状态
     * @param status
     * @param id
     */
    void putStatus(Integer status,Long id);

    /**
     * 通过id获得员工信息
     * @param id
     * @return
     */
    Employee QueryEmployeeById(Long id);

    /**
     * 通过id修改员工信息
     * @param employeeDTO
     */
    void putEmployeeById(EmployeeDTO employeeDTO);

    /**
     * 通过id修改员工密码
     * @param passwordEditDTO
     */
    String putEmployeePassword(PasswordEditDTO passwordEditDTO);
}
