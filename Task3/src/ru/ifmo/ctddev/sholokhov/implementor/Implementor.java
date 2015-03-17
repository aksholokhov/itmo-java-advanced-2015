package ru.ifmo.ctddev.sholokhov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import javax.management.modelmbean.ModelMBean;
import java.io.*;
import java.lang.reflect.*;

/**
 * Created by Шолохов on 03.03.2015.
 */
public class Implementor implements Impler {
    @Override
    public void implement(Class<?> token, File root) throws ImplerException {
        if (token.isPrimitive()) {
            throw new ImplerException("Class is primitive");
        }

        if (Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Class is final");
        }

        try {

            Class c = token;
            if (c.isInterface()) {
                BufferedWriter out = null;


                File outputFile = new File(root, token.getCanonicalName().replace(".", File.separator) + "Impl.java");
                outputFile.getParentFile().mkdirs();


                try {
                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
                    out.write("package " + c.getPackage().getName() + ";" + '\n');

                    out.write("public class " + token.getSimpleName() + "Impl" + " implements " + c.getCanonicalName()+ " {" + '\n');

                    Method[] methods = c.getMethods();

                    for (Method m : methods) {
                        int modifiers = m.getModifiers();
                        Class returnType = m.getReturnType();
                        String methodName = m.getName();
                        Class[] args = m.getParameterTypes();

                        if (Modifier.isPublic(modifiers)) {
                            out.write("public ");
                        } else if (Modifier.isPrivate(modifiers)) {
                            out.write("private ");
                        } else if (Modifier.isProtected(modifiers)) {
                            out.write("protected ");
                        }

                        if (Modifier.isStatic(modifiers)) {
                            out.write("static ");
                        }

                        out.write(returnType.getCanonicalName() + " " + methodName + " (");

                        for (int i = 0; i < args.length; i++) {
                            out.write(args[i].getCanonicalName() + " arg" + i);
                            if (i+1 < args.length) {
                                out.write(", ");
                            }
                        }
                        out.write(") {" + '\n');
                        if (!returnType.equals(void.class)) {
                            out.write(" return");
                            if (returnType.equals(int.class) || returnType.equals(float.class) || returnType.equals(double.class) ||
                                    returnType.equals(short.class) || returnType.equals(char.class) || returnType.equals(long.class) ||
                                    returnType.equals(byte.class)) {
                                out.write(" 0;");
                            } else if (returnType.equals(boolean.class)) {
                                out.write(" false;");
                            }
                            else {
                                out.write(" null;");
                            }
                        }
                        out.write('\n' + "}" + '\n' +'\n');

                    }
                    out.write('\n' + "}" + '\n' +'\n');
                    out.close();
                } catch (IOException e) {
                    System.out.println("IO fail");
                }

            }
        } catch (Exception e) {
            System.out.println("Exception");
        }
    }
}
