package com.trace.core.async;

import java.util.concurrent.ForkJoinTask;

import com.trace.core.Span;
import com.trace.core.TraceContext;

/**
 * @author dengxiaolin
 * @since 2021/01/11
 */
public abstract class TraceRecursiveAction extends ForkJoinTask<Void> {
    private Span asyncParent;

    public TraceRecursiveAction() {
        asyncParent = Span.copyAsAsyncParent(TraceContext.get());
    }

    @Override
    public Void getRawResult() {
        return null;
    }

    @Override
    protected void setRawResult(Void value) {

    }

    /**
     * The main computation performed by this task.
     */
    protected abstract void compute();

    @Override
    protected boolean exec() {
        if (asyncParent == null) {
            compute();
            return true;
        }
        else {
            TraceContext.set(asyncParent);
            try {
                compute();
                return true;
            }
            finally {
                // 异步的parent已经收集过，直接放弃
                TraceContext.pop();
            }
        }
    }
}
