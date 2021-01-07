package com.trace.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import com.trace.core.ConsumerContext;
import com.trace.core.constants.TraceConstants;
import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/07
 */
@Activate(
        group = {"provider"}
)
public class TraceDubboProviderFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String name = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();

        ConsumerContext consumerContext = (ConsumerContext) invocation.getObjectAttachment(TraceConstants.CONSUMER_CONTEXT);
        if (consumerContext == null) {
            TraceManager.startSpan(TraceConstants.DUMMY_CONSUMER_CONTEXT, ServiceType.DUBBO, name);
        }
        else {
            TraceManager.startSpan(consumerContext, ServiceType.DUBBO, name);
        }

        try {
            return invoker.invoke(invocation);
        }
        finally {
            TraceManager.endSpan();
        }
    }
}
