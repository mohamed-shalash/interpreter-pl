package org.example.object;

public class ErrorObject implements Object{
    private final String message;

    public ErrorObject(String message) {
        this.message = message;
    }

    @Override
    public ObjectType type() {
        return ObjectType.ERROR;
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }

    // Optional getter
    public String getMessage() {
        return message;
    }
}
