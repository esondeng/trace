package com.trace.agent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import com.trace.agent.transform.ThreadPoolTransformer;
import com.trace.agent.transform.TraceTransformer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

/**
 * @author dengxiaolin
 * @since 2021/01/10
 */
public class TraceClassFileTransformer implements ClassFileTransformer {
    List<TraceTransformer> transformerList = new ArrayList<>();

    public TraceClassFileTransformer() {
        transformerList.add(new ThreadPoolTransformer());
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (className == null) {
                return null;
            }

            className = toClassName(className);
            for (TraceTransformer transformer : transformerList) {
                if (transformer.needTransform(className)) {
                    System.out.println(("Transforming class " + className));
                    final CtClass clazz = getCtClass(classfileBuffer, loader);
                    transformer.doTransform(clazz);
                    return clazz.toBytecode();
                }
            }
        }
        catch (Throwable t) {
            String msg = "Fail to transform class " + className + ", cause: " + t.toString();
            System.out.println(msg);
            throw new IllegalStateException(msg, t);
        }

        return null;
    }

    private String toClassName(final String classFile) {
        return classFile.replace('/', '.');
    }

    private CtClass getCtClass(byte[] classFileBuffer, ClassLoader classLoader) throws IOException {
        ClassPool classPool = new ClassPool(true);
        if (null != classLoader) {
            classPool.appendClassPath(new LoaderClassPath(classLoader));
        }

        CtClass clazz = classPool.makeClass(new ByteArrayInputStream(classFileBuffer), false);
        clazz.defrost();
        return clazz;
    }
}
