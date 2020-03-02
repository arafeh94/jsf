package com.arafeh.jsf.core.utils;

public class Pair<K, V> {
    private K k;
    private V v;

    public Pair() {

    }

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public void setK(K k) {
        this.k = k;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Pair) {
            Pair<K, V> tmp = (Pair<K, V>) obj;
            return tmp.k.equals(this.k) && tmp.v.equals(this.v);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "k=" + k +
                ", v=" + v +
                '}';
    }
}
