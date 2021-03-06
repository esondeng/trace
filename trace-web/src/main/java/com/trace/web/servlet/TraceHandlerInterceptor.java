package com.trace.web.servlet;

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
public class TraceHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Span span = TraceContext.peek();
            if (span != null) {
                // 入口处name记成了path
                span.putTag(TraceConstants.HTTP_PATH_TAG_KEY, span.getName());
                span.putTag(TraceConstants.HTTP_URL_TAG_KEY, request.getRequestURI());
                span.putTag(TraceConstants.HTTP_METHOD_TAG_KEY, request.getMethod());

                Method method = handlerMethod.getMethod();
                String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
                span.setName(methodName);
            }
        }
        return true;
    }
}
