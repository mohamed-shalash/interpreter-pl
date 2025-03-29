package org.example.object;

public class BreakObject implements Object {
    public static final BreakObject BREAK = new BreakObject();

    private BreakObject() {}

    @Override
    public ObjectType type() {
        return ObjectType.BREAK;
    }

    @Override
    public String inspect() {
        return "break";
    }
}
