package org.example.object;

public class HashKey  {
    private final ObjectType type;
    private final long value;

    public HashKey(ObjectType type, long value) {
        this.type = type;
        this.value = value;
    }

    public ObjectType getType() {
        return type;
    }

    public long getValue() {
        return value;
    }



    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashKey hashKey = (HashKey) o;
        return value == hashKey.value && type == hashKey.type;
    }


    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (int) (value ^ (value >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "HashKey{type=" + type + ", value=" + value + "}";
    }
}