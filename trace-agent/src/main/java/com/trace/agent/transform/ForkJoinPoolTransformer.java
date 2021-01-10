package com.trace.agent.transform;

import javassist.CtClass;

/**
 * @author dengxiaolin
 * @since 2021/01/10
 */
public class ForkJoinPoolTransformer implements TraceTransformer {

    @Override
    public boolean needTransform(String className) {
        return false;
    }

    @Override
    public void doTransform(CtClass clazz) {

    }
}
