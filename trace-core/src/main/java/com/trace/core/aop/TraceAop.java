package com.trace.core.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.trace.core.ChildContext;
import com.trace.core.Span;
import com.trace.core.TraceContext;
import com.trace.core.constants.TraceConstants;
import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/08
 */
@Aspect
public class TraceAop {

    @Pointcut("@annotation(com.trace.core.annotion.TraceMethod) && execution(* *(..))")
    public void methodPointcut() {

    }


    @Around("methodPointcut()")
    public Object methodAdvice(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        String name = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        try {
            Span span = TraceContext.get();
            if (span != null) {
                TraceManager.startSpan(ChildContext.of(span), ServiceType.INNER_CALL, name);
            }
            else {
                TraceManager.startSpan(TraceConstants.DUMMY_CONSUMER_CONTEXT, ServiceType.INNER_CALL, name);
            }

            return point.proceed();
        }
        catch (Exception e) {
            Span span = TraceContext.get();
            if (span != null) {
                span.fillErrors(e);
            }

            throw e;
        }
        finally {
            TraceManager.endSpan();
        }
    }
}
