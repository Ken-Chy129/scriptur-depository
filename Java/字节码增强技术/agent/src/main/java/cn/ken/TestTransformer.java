package cn.ken;

import jdk.internal.org.objectweb.asm.ClassVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author Ken-Chy129
 * @date 2025/5/13
 */
public class TestTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        System.out.println("Transforming " + className);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
