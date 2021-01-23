package com.trace.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import com.trace.core.ConsumerContext;
import com.trace.core.Span;
import com.trace.core.TraceContext;
import com.trace.core.constants.TraceConstants;
import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/07
 */
@Activate(
        group = {"consumer"}
)
public class TraceDubboConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Span span = TraceContext.peek();
        if (span == null) {
            return invoker.invoke(invocation);
        }
        else {
            ServiceType serviceType = span.isAsyncParent() ? ServiceType.DUBBO_ASYNC_CONSUMER : ServiceType.DUBBO_CONSUMER;
            String name = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();

            return TraceManager.tracingWithReturn(
                    serviceType,
                    name,
                    () -> {
                        ConsumerContext consumerContext = ConsumerContext.of(TraceContext.peek());
                        invocation.setAttachment(TraceConstants.CONSUMER_CONTEXT, consumerContext);

                        return invoker.invoke(invocation);
                    });
        }
    }
}
