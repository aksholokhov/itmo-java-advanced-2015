package ru.ifmo.ctddev.sholokhov.jarredimplementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;
import java.util.List;
import java.util.NavigableSet;

/**
 * Created by Шолохов on 03.03.2015.
 */
public class Main {
    public static void main (String[] args) {
        Implementor i = new Implementor();
        try {
            i.implement(Class.forName(args[1]), new File("ImplementClasses"));
        } catch (ImplerException e) {
            System.err.println("Impler exception: " + e.toString());
        } catch (Exception e) {
            System.err.println("class " + args[0] + " not found");
        }

    }
}
