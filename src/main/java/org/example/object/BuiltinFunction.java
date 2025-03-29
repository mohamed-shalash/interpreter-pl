package org.example.object;

import java.util.List;

@FunctionalInterface
public interface BuiltinFunction {
    Object apply(List<Object> args);
}
