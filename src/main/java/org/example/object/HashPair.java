package org.example.object;


public class HashPair {
    private final Object key;
    private final Object value;

    public HashPair(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}