package com.trace.core.function;

/**
 * @author dengxiaolin
 * @since 2021/01/08
 */
@FunctionalInterface
public interface ThrowSupplier<T> {
    T get() throws Throwable;
}
