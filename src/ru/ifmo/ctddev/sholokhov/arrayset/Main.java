package ru.ifmo.ctddev.sholokhov.arrayset;

import ru.ifmo.ctddev.sholokhov.arrayset.ArraySet;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Шолохов on 24.02.2015.
 */
public class Main {
    public static void main(String[] args) {
        ArrayList<Integer> t = new ArrayList<Integer>();


        for (int i = 0; i < 400000; i++) {
            t.add(1);
        }

        ArraySet<Integer> test = new ArraySet<Integer>(t, Comparator.<Integer>naturalOrder());
        System.out.println(test.size());
       // final ArraySet<Integer> set = new ArraySet<Integer>(new Object[]{});

    }
}
