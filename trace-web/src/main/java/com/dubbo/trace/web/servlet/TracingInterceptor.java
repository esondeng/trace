package com.dubbo.trace.web.servlet;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.trace.core.Span;
import com.trace.core.TraceContext;
import com.trace.core.constants.TraceConstants;

/**
 * @author dengxiaolin
 * @since 2021/01/22
 */
public class TracingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Span span = TraceContext.get();
            if (span != null) {
                // 入口处name记成了path
                span.putTag(TraceConstants.HTTP_PATH_TAG_KEY, span.getName());
                Method method = handlerMethod.getMethod();
                String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
                span.setName(methodName);
            }
        }
        return true;
    }
}
