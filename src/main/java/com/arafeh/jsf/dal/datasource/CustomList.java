package com.arafeh.jsf.dal.datasource;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface CustomList<T, ID> {

    long size();


    boolean isEmpty();


    boolean contains(Object o);


    Iterator<T> iterator();


    Object[] toArray();


    <T1> T1[] toArray(T1[] a);


    boolean add(T t);


    boolean remove(Object o);


    boolean containsAll(Collection<?> c);


    boolean addAll(Collection<? extends T> c);


    boolean addAll(ID id, Collection<? extends T> c);


    boolean removeAll(Collection<?> c);


    boolean retainAll(Collection<?> c);


    void clear();


    T get(ID index);


    T set(ID index, T element);


    void add(ID index, T element);


    T removeById(ID index);


    Long indexOf(Object o);


    Long lastIndexOf(Object o);


    ListIterator<T> listIterator();


    ListIterator<T> listIterator(int index);


    List<T> subList(int fromIndex, int toIndex);

}
