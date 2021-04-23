package com.trace.web.handler;

import javax.servlet.http.HttpServletRequest;

import com.eson.common.core.exception.ServiceException;
import com.eson.common.web.handler.PreHandler;
import com.trace.core.Span;
import com.trace.core.TraceContext;

/**
 * @author dengxiaolin
 * @since 2021/04/23
 */
public class TracePreHandler implements PreHandler {
    @Override
    public void handle(Exception e, HttpServletRequest httpServletRequest) {
        Span span = TraceContext.peek();
        if (span != null) {
            if (e instanceof ServiceException) {
                ServiceException se = (ServiceException) e;
                span.addError(se.getInterfacePath() + " : " + se.getExceptionClass());
            }
            else {
                span.fillErrors(e);
            }
        }
    }
}
