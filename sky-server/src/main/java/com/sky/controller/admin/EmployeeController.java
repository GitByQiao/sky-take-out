package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.util.Units;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口类")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 添加员工
     *
     * @param employeeDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("添加员工")
    public Result saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工信息分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工信息分页查询")
    public Result<PageResult> pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工参数信息：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 进行员工状态栏状态修改进行锁定或者正常
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("status/{status}")
    @ApiOperation("修改员工状态启用禁用")
    public Result putStatus(@PathVariable Integer status, Long id) {
        log.info("启用禁用员工账号：{},{}", status, id);
        employeeService.putStatus(status, id);
        return Result.success();
    }

    /**
     * 通过id查询员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("通过id查询员工信息")
    public Result<Employee> QueryEmployeeById(@PathVariable Long id) {
        log.info("通过id查询员工信息：{}", id);
        Employee employee = employeeService.QueryEmployeeById(id);
        log.info("通过id查询到的员工信息：{}", employee);
        return Result.success(employee);
    }

    /**
     * 通过id修改员工信息
     *
     * @param employeeDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("通过id修改员工信息")
    public Result putEmployeeById(@RequestBody EmployeeDTO employeeDTO) {
        log.info("通过id修改员工信息：{}", employeeDTO);
        employeeService.putEmployeeById(employeeDTO);
        return Result.success();
    }

    /**
     * 通过id修改员工密码
     *
     * @param passwordEditDTO
     * @return
     */
    @PutMapping("/editPassword")
    @ApiOperation("通过id修改员工密码")
    public Result putEmployeePassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        log.info("通过id修改员工密码：{}", passwordEditDTO);
        String message = employeeService.putEmployeePassword(passwordEditDTO);
        if (MessageConstant.OLD_PASSWORD_ERROR.equals(message)) {
            return Result.error(MessageConstant.PASSWORD_EDIT_FAILED);
        } else {
            return Result.success(MessageConstant.PASSWORD_EDIT_SUCCESS);
        }
    }
}
