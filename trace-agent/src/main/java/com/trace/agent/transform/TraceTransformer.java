package com.trace.agent.transform;

import javassist.CtClass;

/**
 * @author dengxiaolin
 * @since 2021/01/10
 */
public interface TraceTransformer {

    boolean needTransform(String className);

    void doTransform(CtClass clazz);
}
