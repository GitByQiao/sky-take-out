package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 定义自动填充切点
     */
    @Pointcut("execution(* com.sky.mapper.*Mapper.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillCutPoint() {
    }

    @Before(value = "autoFillCutPoint()")
    public void autoFillCutAspect(JoinPoint joinPoint) {
//        获得当前注解的类别：（插入或者更新）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
//        获得注解方法的参数第一个
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object arg = args[0];
//            对注解的类型进行判断
        if (operationType.equals(OperationType.UPDATE)) {
//            如果是更新就填充四个默认值
//            通过获得参数的set方法进行自动填充
            try {
                Method setUpdateTime = arg.getClass()
                        .getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass()
                        .getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Long currentId = BaseContext.getCurrentId();
                LocalDateTime now = LocalDateTime.now();
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType.equals(OperationType.INSERT)) {
//            如果是插入就更新两个默认值
            try {
                Method setCreatTime = arg.getClass()
                        .getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreatUser = arg.getClass()
                        .getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = arg.getClass()
                        .getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass()
                        .getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Long currentId = BaseContext.getCurrentId();
                LocalDateTime now = LocalDateTime.now();
                setCreatTime.invoke(arg,now);
                setCreatUser.invoke(arg,currentId);
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
