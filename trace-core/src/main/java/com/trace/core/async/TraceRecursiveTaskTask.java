package com.trace.core.async;

import java.util.concurrent.ForkJoinTask;

import com.trace.core.Span;
import com.trace.core.TraceContext;

/**
 * @author dengxiaolin
 * @since 2021/01/11
 */
public abstract class TraceRecursiveTaskTask<T> extends ForkJoinTask<T> {
    private Span asyncParent;
    private T result;


    public TraceRecursiveTaskTask() {
        asyncParent = Span.copyAsAsyncParent(TraceContext.get());
    }

    /**
     * The main computation performed by this task.
     */
    protected abstract T compute();

    @Override
    protected boolean exec() {
        if (asyncParent == null) {
            result = compute();
            return true;
        }
        else {
            TraceContext.set(asyncParent);
            try {
                result = compute();
                return true;
            }
            finally {
                // 异步的parent已经收集过，直接放弃
                TraceContext.pop();
            }
        }
    }

    @Override
    public final T getRawResult() {
        return result;
    }

    @Override
    protected final void setRawResult(T value) {
        result = value;
    }
}
