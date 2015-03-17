package ru.ifmo.ctddev.sholokhov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;

/**
 * Created by Шолохов on 03.03.2015.
 */
public class Main {
    public static void main (String[] args) {
        String className;
        boolean generateJar;

        if (args != null && args.length == 1 && args[0] != null) {
            className = args[0];
            generateJar = false;
        } else if (args != null && args.length == 2 && args[0] != null && args[1] != null && args[0].equals("-jar")) {
            className = args[1];
            generateJar = true;
        } else {
            System.err.println("incorrect args");
            return;
        }

        try {
            Implementor impler = new Implementor();
            impler.generateJar =  generateJar;
            impler.implement(Class.forName(className), new File("."));
        } catch (ImplerException e) {
            System.err.println("Impler exception: " + e.toString());
        } catch (ClassNotFoundException e) {
            System.err.println("class " + args[0] + " not found");
        }
    }
}
