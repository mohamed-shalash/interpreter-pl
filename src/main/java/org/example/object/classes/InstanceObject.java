package org.example.object.classes;

import org.example.object.Object;
import org.example.object.ObjectType;

import java.util.HashMap;
import java.util.Map;

public class InstanceObject implements Object {
    private final ClassObject classObject;
    private final Map<String, Object> properties;

    public InstanceObject(ClassObject classObject) {
        this.classObject = classObject;
        this.properties = new HashMap<>();
    }

    @Override
    public ObjectType type() { return ObjectType.INSTANCE; }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(classObject.getName()).append(" instance");
        if (!properties.isEmpty()) {
            sb.append(" {");
            properties.forEach((k, v) -> sb.append(k).append(": ").append(((Object) v).inspect()).append(", "));
            if (sb.charAt(sb.length() - 2) == ',') {
                sb.setLength(sb.length() - 2);
            }
            sb.append("}");
        }
        sb.append(">");
        return sb.toString();
    }

    public Object getMethod(String name) {
        MethodObject method = classObject.getMethod(name);
        if (method != null) {
            return method.bind(this); // Bind method to this instance
        }
        return null;
    }

    // Property access
    public Object getProperty(String name) {
        Object value = properties.get(name);
        if (value == null) {
            MethodObject method = classObject.getMethod(name);
            if (method != null) {
                return method.bind(this);
            }
        }
        return value;
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public void setField(String name, Object value) {
        properties.put(name, value);
    }

    public ClassObject getClassObject() {
        return classObject;
    }



}