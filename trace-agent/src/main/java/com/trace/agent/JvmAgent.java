package com.trace.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * 利用JDK1.6的动态代理
 *
 * @author dengxiaolin
 * @since 2021/01/10
 */
public class JvmAgent {
    /**
     * 类加载调用
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("premain agent start");
        ClassFileTransformer transformer = new TraceClassFileTransformer();
        instrumentation.addTransformer(transformer, true);
        System.out.println("premain agent end");
    }
}
