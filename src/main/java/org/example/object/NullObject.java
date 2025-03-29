package org.example.object;

public class NullObject implements Object {
    public static final NullObject NULL = new NullObject();

    private NullObject() {} // Private constructor for singleton

    @Override
    public ObjectType type() {
        return ObjectType.NULL;
    }

    @Override
    public String inspect() {
        return "null";
    }
}
