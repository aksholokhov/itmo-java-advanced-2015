package ru.ifmo.ctddev.sholokhov.arrayset;

import java.util.*;

/**
 * Created by Шолохов on 24.02.2015.
 */
public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {

    private List<T> data;
    private Comparator<? super T> comparator = null;
    private boolean hasComparator = true;

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

    private void unify(ArrayList<T> data2) {
        int i = 1;
        while (i < data2.size()) {
            if (comparator.compare(data2.get(i-1), data2.get(i)) == 0) {
                data2.remove(i);
            } else i++;
        }
    }

    public ArraySet() {
        this.data = new ArrayList<>();
        this.comparator = null;
    }

    public ArraySet(Collection<T> data) {
        ArrayList<T> data2 = new ArrayList<T>(data);
        comparator = new MyComparator<>();
        Collections.sort(data2, comparator);
        unify(data2);
        this.data = new ArrayList<T>(data2);
        hasComparator = false;
    }

    public ArraySet(Collection<T> data, Comparator<? super T> comparator) {
        ArrayList<T> data2 = new ArrayList<T>(data);
        Collections.sort(data2, comparator);
        this.comparator = comparator;
        unify(data2);
        this.data = new ArrayList<T>(data2);
    }

    private ArraySet(List<T> data, Comparator<? super T> comparator, boolean sorted) {
        this.data = data;
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
        return hasComparator ? comparator : null;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        int fromIndex = Collections.binarySearch(data, fromElement, comparator);
        int toIndex = Collections.binarySearch(data, toElement, comparator);
        return subSetBounds(fromIndex, true, toIndex, false);
    }

    private SortedSet<T> subSetBounds(int fromIndex, boolean lincl, int toIndex, boolean rincl) {
        if (fromIndex < 0) {
            fromIndex = -(fromIndex+1);
        }
        if (toIndex < 0) {
            toIndex = -(toIndex+1);
        }
        if (((fromIndex == toIndex) && !(lincl && rincl)) || (fromIndex > toIndex) || (fromIndex >= data.size())) return new ArraySet<T>();
        return new ArraySet<T>(data.subList(lincl ? fromIndex : fromIndex+1, rincl ? toIndex+1 : toIndex), comparator, true);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        if (toElement == null) {
            throw new NullPointerException();
        }
        int toIndex = Collections.binarySearch(data, toElement, comparator);
        return subSetBounds(0, true, toIndex, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        if (fromElement == null) {
            throw new NullPointerException();
        }
        int fromIndex = Collections.binarySearch(data, fromElement, comparator);
        return subSetBounds(fromIndex, true, data.size()-1, true);
    }

    @Override
    public T first() {
        if (data.isEmpty()) {
            throw new NoSuchElementException();
        } else return data.get(0);
    }

    @Override
    public T last() {
        if (data.isEmpty()) {
            throw new NoSuchElementException();
        } else return data.get(data.size() - 1);
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

    @Override
    public boolean contains(Object o) {

        int fromIndex = Collections.binarySearch(data, (T)o, comparator);
        return fromIndex >= 0 ? true : false;
    }
}
