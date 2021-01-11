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
    private static Instrumentation instr;

    /**
     * 命令行使用
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("premain agent start");
        instr = instrumentation;
        install();
        System.out.println("premain agent end");
    }

    /**
     * 类加载调用
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("agentmain agent start");
        instr = instrumentation;
        install();
        System.out.println("agentmain agent end");
    }

    private static void install() {
        ClassFileTransformer transformer = new TraceClassFileTransformer();
        instrumentation().addTransformer(transformer, true);
    }

    private static Instrumentation instrumentation() {
        return instr;
    }


}
