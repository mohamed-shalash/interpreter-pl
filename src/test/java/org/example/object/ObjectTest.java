package org.example.object;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ObjectTest {

    @Test
    void testStringHashKey() {
        StringObject hello1 = new StringObject("Hello World");
        StringObject hello2 = new StringObject("Hello World");
        StringObject diff1 = new StringObject("My name is johnny");
        StringObject diff2 = new StringObject("My name is johnny");

        assertEquals(hello1.hashKey(), hello2.hashKey());
        assertEquals(diff1.hashKey(), diff2.hashKey());
        assertNotEquals(hello1.hashKey(), diff1.hashKey());
    }

    @Test
    void testBooleanHashKey() {
        BooleanObject true1 = new BooleanObject(true);
        BooleanObject true2 = new BooleanObject(true);
        BooleanObject false1 = new BooleanObject(false);
        BooleanObject false2 = new BooleanObject(false);

        assertEquals(true1.hashKey(), true2.hashKey());
        assertEquals(false1.hashKey(), false2.hashKey());
        assertNotEquals(true1.hashKey(), false1.hashKey());
    }

    @Test
    void testIntegerHashKey() {
        IntegerObject num1 = new IntegerObject(42);
        IntegerObject num2 = new IntegerObject(42);
        IntegerObject diff = new IntegerObject(99);

        assertEquals(num1.hashKey(), num2.hashKey());
        assertNotEquals(num1.hashKey(), diff.hashKey());
    }
}