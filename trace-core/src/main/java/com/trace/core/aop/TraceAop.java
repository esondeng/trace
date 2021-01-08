package com.trace.core.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/08
 */
@Aspect
public class TraceAop {

    @Pointcut("@annotation(com.trace.core.annotion.Tracing)")
    public void methodPointcut() {

    }


    @Around("methodPointcut()")
    public Object methodAdvice(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        String name = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        return TraceManager.tracingWithReturn(ServiceType.INNER_CALL, name, point::proceed);
    }
}
