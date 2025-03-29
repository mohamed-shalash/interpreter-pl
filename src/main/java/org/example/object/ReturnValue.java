package org.example.object;

public class ReturnValue implements Object {
    private final Object value;

    public ReturnValue(Object value) {
        this.value = value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.RETURN_VALUE;
    }

    @Override
    public String inspect() {
        return value.inspect(); // Delegate to the wrapped value's Inspect()
    }

    // Optional getter if needed
    public Object getValue() {
        return value;
    }
}