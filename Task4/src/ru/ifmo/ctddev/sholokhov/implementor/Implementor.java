package ru.ifmo.ctddev.sholokhov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.management.modelmbean.ModelMBean;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Class that implements other classes and pack them to Jar Files.
 * Does both classes and JARs.
 *
 * @author Alexey Shlolkhov
 *
 * @see info.kgeorgiy.java.advanced.implementor.Impler
 * @see java.lang.Class
 * @see javax.tools.JavaCompiler
 */
public class Implementor implements JarImpler {

    /**
     * Produces code implementing class or interface specified by provided <tt>token</tt>.
     * <p>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * <tt>root</tt> directory and have correct file name.
     *
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws ImplerException in the following cases:<ol>
     *      <li>if {@code token} is a primitive class</li>
     *      <li>if {@code token} is final and, therefore, cannot be extended</li>
     *      </ol>
     */

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
                BufferedWriter out;


                File outputFile = new File(root, token.getCanonicalName().replace(".", File.separator) + "Impl.java");
                File manifest = new File(root, "META-INF/MANIFEST.MF");
                
                manifest.getParentFile().mkdirs();
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
                    out.write('\n' + "}" + '\n' + '\n');

                    if (generateJar) {
                        compileFile(root, outputFile);
                        createJar(root, token);
                    }

                    out.close();

                } catch (IOException e) {
                    System.out.println("IO fail");
                }

            }
        } catch (Exception e) {
            System.out.println("Exception");
        }

    }


    public boolean generateJar;

    /**
     * Produces <tt>.jar</tt> file implementing class or interface specified by provided <tt>token</tt>.
     * <p>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added.
     *
     * @param token type token to create implementation for.
     * @param jarFile target <tt>.jar</tt> file.
     * @throws ImplerException when implementation cannot be generated (see {@link #implement(Class, File)}).
     */
    @Override
    public void implementJar(Class<?> token, File jarFile) throws ImplerException {
        boolean prevGenerateJar = generateJar;
        generateJar = true;
        implement(token, jarFile);
        generateJar  = prevGenerateJar;
    }

    /**
     * Compiles <tt>source</tt> file in <tt>root</tt> directory.
     * <p>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added.
     *
     * @param root root directory.
     * @param source source <tt>.java</tt> file.
     * @throws ImplerException when compilation error happens.
     */
    private void compileFile(File root, File source) throws ImplerException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<String> args = new ArrayList<>();
        args.add(source.getPath());
        args.add("-cp");
        args.add(root.getPath() + File.pathSeparator + System.getProperty("java.class.path"));

        int exitCode = compiler.run(null, null, null, args.toArray(new String[args.size()]));
        if (exitCode != 0) {
            throw new ImplerException("error during compile");
        }
    }

    /**
     * Creates jar in <tt>classFile</tt> for class <tt>token</tt> in <tt>root</tt> directory.
     *
     * @param root root directory.
     * @param token type token to create jar for.
     * @throws ImplerException when {@link java.io.IOException} it occurs in {@link #addToJar(File, JarOutputStream)}.
     */
    private void createJar(File root, Class<?> token) throws ImplerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        File jarFile = new File(root, token.getSimpleName() + "Impl.jar");
        File pathToClasses = new File(token.getPackage().getName().split("\\.")[0]);
        try (JarOutputStream stream = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
            addToJar(pathToClasses, stream);
        } catch (IOException e) {
            throw new ImplerException(e);
        }
    }

    /**
     * Adds <tt>file</tt> in <tt>stream</tt>.
     *
     * @param file file to be added.
     * @param stream jar stream to add file in.
     * @throws IOException if it is thrown while reading <tt>file</tt>.
     */
    private void addToJar(File file, JarOutputStream stream) throws IOException {
        if (file.isDirectory()) {
            JarEntry entry = new JarEntry(file.getPath());
            stream.putNextEntry(entry);
            stream.closeEntry();

            for (File nestedFile : file.listFiles()) {
                addToJar(nestedFile, stream);
            }
        } else if (file.getName().endsWith(".class")) {
            JarEntry entry = new JarEntry(file.getPath());
            stream.putNextEntry(entry);

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[4096];
            int count;

            while ((count = in.read(buffer)) != -1) {
                stream.write(buffer, 0, count);
            }

            stream.closeEntry();
        }
    }
}
