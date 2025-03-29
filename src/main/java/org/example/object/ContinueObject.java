package org.example.object;

public class ContinueObject implements Object {
    public static final ContinueObject CONTINUE = new ContinueObject();

    private ContinueObject() {}

    @Override
    public ObjectType type() {
        return ObjectType.CONTINUE;
    }

    @Override
    public String inspect() {
        return "continue";
    }
}