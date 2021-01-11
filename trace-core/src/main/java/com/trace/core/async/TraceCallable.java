package com.trace.core.async;

import java.util.concurrent.Callable;

import com.trace.core.Span;
import com.trace.core.TraceContext;
import com.trace.core.manager.TraceManager;


/**
 * @author dengxiaolin
 * @since 2021/01/09
 */
public class TraceCallable<V> implements Callable<V> {
    private Span asyncParent;
    private Callable<V> callable;


    public TraceCallable(Span asyncParent, Callable<V> callable) {
        this.asyncParent = asyncParent;
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        return TraceManager.asyncParent(this);
    }

    public static <V> Callable<V> getInstance(Callable<V> callable) {
        if (callable == null) {
            return null;
        }

        Span span = TraceContext.get();
        if (span == null) {
            return callable;
        }

        return new TraceCallable<>(Span.copyAsAsyncParent(span), callable);
    }

    public Span getAsyncParent() {
        return asyncParent;
    }

    public Callable<V> getCallable() {
        return callable;
    }
}
