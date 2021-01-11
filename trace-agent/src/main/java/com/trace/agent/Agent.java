package com.trace.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * @author dengxiaolin
 * @since 2021/01/10
 */
public class Agent {
    private static Instrumentation inst;

    /**
     * 命令行启动
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        inst = instrumentation;
        install(inst);
    }

    /**
     * 类加载调用
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        install(instrumentation);
    }

    private static void install(Instrumentation instrumentation) {
        System.out.println("premain agent start");
        ClassFileTransformer transformer = new TraceClassFileTransformer();
        instrumentation.addTransformer(transformer, true);
        System.out.println("premain agent end");
    }
}
