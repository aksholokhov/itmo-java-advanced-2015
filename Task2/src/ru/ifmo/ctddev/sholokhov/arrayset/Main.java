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
        t.add(1);
        t.add(-5);
        t.add(3);
        t.add(-2);

        ArraySet<Integer> test = new ArraySet<Integer>(t, Comparator.<Integer>naturalOrder());

        test.headSet(-2).forEach((x) -> System.out.print(x));
        System.out.println();
        test.tailSet(1).forEach((x) -> System.out.print(x));

        ArrayList<Integer> t2 = new ArrayList<Integer>();
        test = new ArraySet<Integer>(t);

       // final ArraySet<Integer> set = new ArraySet<Integer>(new Object[]{});

    }
}
