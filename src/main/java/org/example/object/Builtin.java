package org.example.object;

import java.util.List;

public class Builtin implements Object {
    private final BuiltinFunction fn;

    public Builtin(BuiltinFunction fn) {
        this.fn = fn;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BUILTIN_OBJ;
    }

    @Override
    public String inspect() {
        return "builtin function";
    }

    public Object apply(List<Object> args) {
        return fn.apply(args);
    }
}
