package org.example.object;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class StringObject  implements Object,Hashable {
    private final String value;

    public StringObject(String value) {
        this.value = value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.STRING;
    }

    @Override
    public String inspect() {
        return value;
    }

    // Getter
    public String getValue() {
        return value;
    }

    @Override
    public HashKey hashKey() {
        CRC32 crc = new CRC32();
        crc.update(value.getBytes(StandardCharsets.UTF_8));
        return new HashKey(type(), crc.getValue());
    }
}