package org.example.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BooleanObject implements Object,Hashable {
    private boolean value;
    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);

    public BooleanObject(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    @Override
    public HashKey hashKey() {
        return new HashKey(type(), value ? 1L : 0L);
    }

}