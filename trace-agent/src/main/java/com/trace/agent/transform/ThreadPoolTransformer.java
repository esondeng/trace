package com.trace.agent.transform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.eson.common.core.utils.Strings;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * @author dengxiaolin
 * @since 2021/01/10
 */
public class ThreadPoolTransformer implements TraceTransformer {

    private static final Set<String> TRANSFORM_CLASS_SET = new HashSet<>();

    static {
        TRANSFORM_CLASS_SET.add("java.util.concurrent.ThreadPoolExecutor");
        TRANSFORM_CLASS_SET.add("java.util.concurrent.ScheduledThreadPoolExecutor");
    }

    private static final Set<String> TRANSFORM_METHOD_SET = new HashSet<>();

    static {
        TRANSFORM_METHOD_SET.add("execute");
        TRANSFORM_METHOD_SET.add("submit");
        TRANSFORM_METHOD_SET.add("schedule");
        TRANSFORM_METHOD_SET.add("scheduleAtFixedRate");
        TRANSFORM_METHOD_SET.add("scheduleWithFixedDelay");
    }

    private static Map<String, String> CLASS_NAME_MAP = new HashMap<>();

    static {
        CLASS_NAME_MAP.put("java.lang.Runnable", "com.trace.core.async.TraceRunnable");
        CLASS_NAME_MAP.put("java.util.concurrent.Callable", "com.trace.core.async.TraceCallable");
    }

    @Override
    public boolean needTransform(String className) {
        return TRANSFORM_CLASS_SET.contains(className);
    }

    @Override
    public void doTransform(CtClass clazz) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> TRANSFORM_METHOD_SET.contains(method.getName()))
                .forEach(method -> transformMethod(clazz, method));
    }

    private static void transformMethod(CtClass clazz, CtMethod method) {
        if (method.getDeclaringClass() != clazz) {
            return;
        }

        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
            return;
        }

        try {
            CtClass[] parameterTypes = method.getParameterTypes();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < parameterTypes.length; i++) {
                CtClass paraType = parameterTypes[i];
                String paraTypeName = paraType.getName();

                if (CLASS_NAME_MAP.containsKey(paraTypeName)) {
                    int paramIndex = i + 1;
                    String replaceCode = Strings.of("${} = {}.get({});", paramIndex, CLASS_NAME_MAP.get(paraTypeName), paramIndex);
                    sb.append(replaceCode);
                    System.out.println("insert code before method " + method + " of class " + method.getDeclaringClass().getName() + ": " + replaceCode);
                }
            }

            if (sb.length() > 0) {
                method.insertBefore(sb.toString());
            }
        }
        catch (Exception e) {
        }
    }
}
