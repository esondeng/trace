package com.trace.core.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.trace.core.Span;
import com.trace.core.TraceContext;
import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/08
 */
@Aspect
public class TraceAspect {

    @Pointcut("@annotation(com.trace.core.annotation.Tracing) || @within(com.trace.core.annotation.Tracing)")
    public void tracePointCut() {

    }

    @Around("tracePointCut()")
    public Object traceAdvice(ProceedingJoinPoint point) throws Throwable {
        // 内部调用时，验证是否在Trace context下
        Span parentSpan = TraceContext.get();
        if (parentSpan == null) {
            return point.proceed();
        }
        else {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            String name = method.getDeclaringClass().getSimpleName() + "." + method.getName();

            ServiceType serviceType = parentSpan.isAsyncParent() ? ServiceType.ASYNC : ServiceType.INNER_CALL;
            return TraceManager.tracingWithReturn(serviceType, name, point::proceed);
        }
    }
}
