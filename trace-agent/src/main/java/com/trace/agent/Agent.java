package com.trace.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * @author dengxiaolin
 * @since 2021/01/10
 */
public class Agent {

    private static Instrumentation inst;

    public static void main(String[] args) {

    }

    /**
     * 类加载调用
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("agent start");
        ClassFileTransformer transformer = new TraceClassFileTransformer();
        instrumentation.addTransformer(transformer, true);
        System.out.println("agent end");
    }
}
