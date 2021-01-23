package com.trace.core;

import java.util.Stack;

/**
 * Span执行栈上下文
 *
 * @author dengxiaolin
 * @since 2021/01/06
 */
public class TraceContext {

    private static final ThreadLocal<Stack<Span>> CALL_STACK = new ThreadLocal<>();

    public static Span peek() {
        Stack<Span> stack = CALL_STACK.get();
        if (stack == null) {
            return null;
        }
        else {
            return stack.peek();
        }
    }

    public static void push(Span span) {
        Stack<Span> stack = CALL_STACK.get();
        if (stack == null) {
            stack = new Stack<>();
            CALL_STACK.set(stack);
        }
        stack.push(span);
    }

    public static Span pop() {
        Stack<Span> stack = CALL_STACK.get();
        Span span = stack.pop();
        span.end();

        if (stack.isEmpty()) {
            CALL_STACK.remove();
        }

        return span;
    }
}
