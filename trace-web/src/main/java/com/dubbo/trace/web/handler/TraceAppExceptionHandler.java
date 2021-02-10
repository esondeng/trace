package com.dubbo.trace.web.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eson.common.core.exception.ServiceException;
import com.eson.common.web.WebResponse;
import com.eson.common.web.handler.AppExceptionHandler;
import com.trace.core.Span;
import com.trace.core.TraceContext;

/**
 * @author dengxiaolin
 * @since 2021/02/10
 */
public class TraceAppExceptionHandler extends AppExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    @Override
    public WebResponse<Void> handleException(Exception e, HttpServletRequest request) {
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

        return super.handleException(e, request);
    }
}
