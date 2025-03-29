package org.example.object;


import java.util.Map;
import java.util.stream.Collectors;

public class HashObject implements Object {
    private final Map<HashKey, HashPair> pairs;

    public HashObject(Map<HashKey, HashPair> pairs) {
        this.pairs = pairs;
    }

    public Map<HashKey, HashPair> getPairs() {
        return pairs;
    }

    @Override
    public ObjectType type() {
        return ObjectType.HASH_OBJ;
    }

    @Override
    public String inspect() {
        return pairs.values().stream()
                .map(pair -> pair.getKey().inspect() + ": " + pair.getValue().inspect())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
