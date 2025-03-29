// ArrayObject.java
package org.example.object;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayObject implements Object {

    private final List<Object> elements;

    public ArrayObject(List<Object> elements) {
        this.elements = elements;
    }

    public List<Object> getElements() {
        return elements;
    }

    @Override
    public ObjectType type() {
        return ObjectType.ARRAY_OBJ;
    }

    @Override
    public String inspect() {
        String elementsString = elements.stream()
                .map(Object::inspect)
                .collect(Collectors.joining(", "));
        return "[" + elementsString + "]";
    }
}