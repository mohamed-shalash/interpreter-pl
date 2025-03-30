package org.example.object.classes;


import org.example.ast.classes.FieldDeclaration;
import org.example.object.Object;
import org.example.object.ObjectType;

import java.util.Map;

public class ClassObject implements Object {
    private final String name;
    private final ClassObject superClass;
    private final Map<String, MethodObject> methods;
    private final Map<String, FieldDeclaration> fields;

    public ClassObject(String name, ClassObject superClass,
                       Map<String, FieldDeclaration> fields,
                       Map<String, MethodObject> methods) {
        this.name = name;
        this.superClass = superClass;
        this.fields = fields;
        this.methods = methods;
    }

    @Override
    public ObjectType type() { return ObjectType.CLASS; }

    @Override
    public String inspect() {
        return null;
    }

    // Getters
    public String getName() { return name; }
    public ClassObject getSuperClass() { return superClass; }
    public MethodObject getMethod(String name) {
        MethodObject method = methods.get(name);
        if (method == null && superClass != null) {
            return superClass.getMethod(name);
        }
        return method;
    }

    public Map<? extends String, ? extends org.example.ast.classes.FieldDeclaration> getFields() {
        return fields;
    }

    public Map<String, MethodObject> getMethods() {
        return methods;
    }

    @Override
    public String toString() {

        return "ClassObject{" +
                "name='" + name + '\'' +
                ", superClass=" + superClass +
                ", methods=" + methods +
                ", fields=" + fields +
                '}';
    }

    public FieldDeclaration  getField(String name) {
        FieldDeclaration field = fields.get(name);
        if (field == null && superClass != null) {
            return superClass.getField(name);
        }
        return field;
    }
}
