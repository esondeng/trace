package com.trace.core.async;

import com.trace.core.Span;
import com.trace.core.TraceContext;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/09
 */
public class TraceRunnable implements Runnable {
    private Span asyncParent;
    private Runnable runnable;

    public TraceRunnable(Span asyncParent, Runnable runnable) {
        this.asyncParent = asyncParent;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        TraceManager.asyncParent(this);
    }

    public static Runnable getInstance(Runnable runnable) {
        if (runnable == null) {
            return null;
        }

        Span asyncParent = Span.copyAsAsyncParent(TraceContext.get());
        return asyncParent == null ? runnable : new TraceRunnable(asyncParent, runnable);
    }

    public Span getAsyncParent() {
        return asyncParent;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
