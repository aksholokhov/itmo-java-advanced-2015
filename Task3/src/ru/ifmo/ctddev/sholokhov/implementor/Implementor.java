package ru.ifmo.ctddev.sholokhov.implementor;

import javax.management.modelmbean.ModelMBean;
import java.io.*;
import java.lang.reflect.*;

/**
 * Created by Шолохов on 03.03.2015.
 */
public class Implementor {
    Implementor(String s) {
        try {
            Class c = Class.forName(s);
            if (c.isInterface()) {
                String className = c.getSimpleName() + "Impl";
                String fileName = className + ".java";
                BufferedWriter out = null;


                try {
                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
                    out.write("public class " + className + " implements " + c.getCanonicalName()+ " {" + '\n');

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
                                    returnType.equals(short.class) || returnType.equals(char.class)) {
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
        } catch (ClassNotFoundException e) {
            System.out.println("CNF fail");
        }
    }
}
