package com.trace.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import com.trace.core.ChildContext;
import com.trace.core.Span;
import com.trace.core.TraceContext;
import com.trace.core.constants.TraceConstants;

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
        Span span = TraceContext.get();
        if (span != null) {
            ChildContext childContext = ChildContext.of(span);
            invocation.setAttachment(TraceConstants.CONSUMER_CONTEXT, childContext);
        }
        return invoker.invoke(invocation);
    }
}
