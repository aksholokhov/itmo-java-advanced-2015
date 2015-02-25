package ru.ifmo.ctddev.sholokhov.arrayset;

import java.util.*;

/**
 * Created by Шолохов on 24.02.2015.
 */
public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {

    private ArrayList<T> data;
    private Comparator<? super T> comparator;

    private class MyComparator<T> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            return ((Comparable<T>) o1).compareTo(o2);
        }
    }

    private class MyIterator<T> implements Iterator<T> {
        Iterator<T> c;

        public MyIterator(Iterator<T> c) {
            this.c = c;
        }

        @Override
        public boolean hasNext() {
            return c.hasNext();
        }

        @Override
        public T next() {
            return c.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public ArraySet(Collection<T> data) {
        ArrayList<T> data2 = new ArrayList<T>(data);
        Collections.sort(data2, new MyComparator());
        this.data = new ArrayList<T>(data);
        this.comparator = new MyComparator<T>();
    }

    public ArraySet(Collection<T> data, Comparator<? super T> comparator) {
        ArrayList<T> data2 = new ArrayList<T>(data);
        Collections.sort(data2, comparator);
        this.data = new ArrayList<T>(data2);
        this.comparator = comparator;
    }

    @Override
    public Iterator<T> iterator() {
        return new MyIterator<T>(data.iterator());
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return new ArraySet<T>(data.subList(Collections.binarySearch(data, fromElement, comparator), Collections.binarySearch(data, toElement, comparator)), comparator);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        int index = Collections.binarySearch(data, toElement, comparator);
        if (index>0) {
            return new ArraySet<T>(data.subList(0, index), comparator);
        }
        else return new ArraySet<T>(new ArrayList<T>(), comparator);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        int index = Collections.binarySearch(data, fromElement, comparator);
        if (index>0) {
            return new ArraySet<T>(data.subList(index, data.size()), comparator);
        }
        else return new ArraySet<T>(new ArrayList<T>(), comparator);
    }

    @Override
    public T first() {
        return data.get(0);
    }

    @Override
    public T last() {
        return data.get(data.size() - 1);
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {if (size() == 0) return true; else return false;}

    @Override
    public Object[] toArray() {return data.toArray();}

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
