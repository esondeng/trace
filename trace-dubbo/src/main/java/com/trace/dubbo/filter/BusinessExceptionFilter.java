package com.trace.dubbo.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.filter.ExceptionFilter;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eson.common.core.exception.BusinessException;
import com.eson.common.core.exception.ServiceException;
import com.trace.core.Span;
import com.trace.core.TraceContext;

/**
 * @author dengxiaolin
 * @since 2021/01/05
 */
@Activate(
        group = {"provider"}
)
public class BusinessExceptionFilter implements Filter, Filter.Listener {

    private Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);

    public BusinessExceptionFilter() {
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
            try {
                Throwable exception = appResponse.getException();
                if (!(exception instanceof RuntimeException) && exception instanceof Exception) {
                    return;
                }

                this.logger.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);

                String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                boolean isRpcException = exception instanceof RpcException;

                if (serviceFile != null && exceptionFile != null && !serviceFile.equals(exceptionFile)) {
                    if (!isRpcException) {
                        // rpcException 需要返回调用方
                        // 其他异常消费者不关心提供者的错误堆栈
                        StackTraceElement[] stackTraceElements = exception.getStackTrace();
                        List<String> errorStack = new ArrayList<>();

                        if (stackTraceElements != null && stackTraceElements.length > 0) {
                            int maxErrorNum = Math.min(stackTraceElements.length, 3);
                            for (int i = 0; i < maxErrorNum; i++) {
                                errorStack.add(stackTraceElements[i].toString());
                            }
                        }

                        ServiceException serviceException = new ServiceException(exception.getMessage())
                                .setInterfacePath(invoker.getInterface().getCanonicalName() + "." + invocation.getMethodName())
                                .setErrorStack(errorStack);

                        if (exception instanceof BusinessException) {
                            BusinessException bex = (BusinessException) exception;
                            serviceException.setTrivial(bex.isTrivial());
                            serviceException.setDetailMsg(bex.getDetailMsg());
                        }

                        appResponse.setException(serviceException);

                        Span span = TraceContext.get();
                        if (span != null) {
                            span.setErrorMessages(new ArrayList<>(errorStack));
                        }
                    }
                }
            }
            catch (Throwable var12) {
                this.logger.warn("Fail to ExceptionFilter when called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + var12.getClass().getName() + ": " + var12.getMessage(), var12);
            }
        }

    }

    @Override
    public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
        this.logger.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
    }
}
