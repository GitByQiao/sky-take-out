package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对密码进行md5加密，然后再进行比对
        String md5DigestPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        //System.out.println("================》"+md5DigestPassword);
        if (!md5DigestPassword.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 查询员工信息
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> result = page.getResult();
        System.out.println(employeePageQueryDTO + "=====>");
        return new PageResult(total, result);
    }

    /**
     * 添加员工
     *
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //拷贝员工属性
        BeanUtils.copyProperties(employeeDTO, employee);
        //用户密码进行md5加密
        employee.setPassword
                (
                        DigestUtils.md5DigestAsHex(PasswordConstant
                                .DEFAULT_PASSWORD
                                .getBytes(StandardCharsets.UTF_8))
                );
        //用户状态是否锁定：0锁定，1正常
        employee.setStatus(StatusConstant.ENABLE);
//        //创建时间
//        employee.setCreateTime(LocalDateTime.now());
//        //更新时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //创建和修改创建人ID
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.save(employee);

    }

    /**
     * 更新启动或禁用状态栏状态
     */
    @Override
    public void putStatus(Integer status, Long id) {
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
//        System.out.println("我的状态员工+"+employee);
        employeeMapper.putStatus(employee);
    }

    /**
     * 通过id获得员工信息
     *
     * @param id
     * @return
     */
    @Override
    public Employee QueryEmployeeById(Long id) {
        Employee employee = employeeMapper.QueryEmployeeById(id);
        employee.setPassword("******");
        return employee;
    }

    /**
     * 通过id修改用户信息
     *
     * @param employeeDTO
     */
    @Override
    public void putEmployeeById(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //复制数据到新的对象
        BeanUtils.copyProperties(employeeDTO, employee);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
//        System.out.println("通过id修改用户信息===》" + employee);
        employeeMapper.putEmployeeById(employee);
    }

    /**
     * 通过id修改员工密码
     *
     * @param passwordEditDTO
     */
    @Override
    public String putEmployeePassword(PasswordEditDTO passwordEditDTO) {
        //通过id先获得旧的通过md5加密的密码
        String oldPassword = employeeMapper
                .QueryEmployeeById(passwordEditDTO.getEmpId())
                .getPassword();
        //获得输入框的密码并md5加密
        String PasswordMd5 = DigestUtils.md5DigestAsHex(passwordEditDTO
                .getOldPassword()
                .getBytes(StandardCharsets.UTF_8));
        if (!oldPassword.equals(PasswordMd5)) {
            return MessageConstant.OLD_PASSWORD_ERROR;
        }
        //旧密码正确才能进行新密码修改
        Employee employee = new Employee();
        employee.setId(passwordEditDTO.getEmpId());
        //修改的密码进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(passwordEditDTO
                .getNewPassword()
                .getBytes(StandardCharsets.UTF_8)));
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.putEmployeeById(employee);
        return MessageConstant.PASSWORD_EDIT_SUCCESS;
    }
}