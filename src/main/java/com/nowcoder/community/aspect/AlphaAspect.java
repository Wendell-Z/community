package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {

    //切入点 返回值任意 拦截service包下所有类 所有方法 所有参数
//    @Pointcut("execution(* com.nowcoder.community.controller.DiscussPostController.addDiscussPost(..))")
//    public void pointcut() {
//
//    }

    //方法执行前调用
//    @Before("pointcut()")
//    public void before() {
//        System.out.println("before");
//    }

    //方法执行后调用
//    @After("pointcut()")
//    public void after() {
//        System.out.println("after");
//    }

    //方法返回值后调用
    @AfterReturning(value = "@annotation(com.nowcoder.community.annontation.CalculateScore)", returning = "responseStr")
    public void afterRetuning(String responseStr) {
        System.out.println(responseStr);
        System.out.println("afterRetuning");
    }

    //    //抛异常后调用
//    @AfterThrowing("pointcut()")
//    public void afterThrowing() {
//        System.out.println("afterThrowing");
//    }
//
    //方法执行前后调用
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        //joinPoint即对应的方法 即连接点
        //joinPoint.proceed();将service中执行的方法封装成了joinPoint  调用proceed()执行方法
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }

}
