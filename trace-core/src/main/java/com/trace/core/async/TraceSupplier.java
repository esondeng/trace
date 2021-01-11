package com.trace.core.async;

import java.util.function.Supplier;

import com.trace.core.Span;
import com.trace.core.TraceContext;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/11
 */
public class TraceSupplier<T> implements Supplier<T> {
    private Span asyncParent;
    private Supplier<T> supplier;

    public TraceSupplier(Span asyncParent, Supplier<T> supplier) {
        this.asyncParent = asyncParent;
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return TraceManager.asyncParent(this);
    }

    public static <T> Supplier<T> getInstance(Supplier<T> supplier) {
        if (supplier == null) {
            return null;
        }

        Span span = TraceContext.get();
        if (span == null) {
            return supplier;
        }

        return new TraceSupplier<>(Span.copyAsAsyncParent(span), supplier);
    }

    public Span getAsyncParent() {
        return asyncParent;
    }

    public Supplier<T> getSupplier() {
        return supplier;
    }
}
