package ru.ifmo.ctddev.sholokhov.implementor;

/**
 * Created by Шолохов on 03.03.2015.
 */
public class Main {
    public static void main (String[] args) {
        try {
            Class input = Class.forName(args[0]);

        } catch (ClassNotFoundException e) {
            System.err.println("Class " + args[0] + " not found");
        }

    }
}
