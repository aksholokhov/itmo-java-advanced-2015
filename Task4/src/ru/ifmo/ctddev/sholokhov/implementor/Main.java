package ru.ifmo.ctddev.sholokhov.implementor;

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
            i.implementJar(javax.xml.bind.Element.class, new File(".", "test02_azaza.jar"));
        } catch (Exception e) {
        }

    }
}
