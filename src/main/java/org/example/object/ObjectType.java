package org.example.object;

public enum ObjectType {
    INTEGER("INTEGER"),
    BOOLEAN("BOOLEAN"),
    STRING("STRING"),
    ERROR("ERROR"),
    NULL("NULL"),
    RETURN_VALUE("RETURN_VALUE"),

    FUNCTION_OBJ ("FUNCTION"),
    BUILTIN_OBJ ("BUILTIN"),
    ARRAY_OBJ ("ARRAY"),
    CONTINUE("CONTINUE"),
    BREAK("BREAK"),
    HASH_OBJ ("HASH"),
    CLASS("CLASS"),
    INSTANCE("INSTANCE"),;

    private final String name;

    ObjectType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
