package org.example.object.classes;

import org.example.ast.BlockStatement;
import org.example.ast.Identifier;
import org.example.evaluator.Evaluator;
import org.example.object.Environment;
import org.example.object.FunctionObject;
import org.example.object.Object;

import java.util.List;

public class MethodObject extends FunctionObject {
    private InstanceObject instance;


    public MethodObject(List<Identifier> parameters,
                        BlockStatement body,
                        Environment env) {
        super(parameters, body, env);
    }

    public MethodObject bind(InstanceObject instance) {
        this.instance = instance;
        return this;
    }


    public Object apply(List<Object> args) {
        Environment methodEnv = new Environment(this.getEnv());
        methodEnv.set("this", instance); // Bind 'this' to the instance

        // Add parameters to the environment
        for (int i = 0; i < getParameters().size(); i++) {
            methodEnv.set(getParameters().get(i).getValue(), args.get(i));
        }

        return Evaluator.eval(getBody(), methodEnv);
    }

    @Override
    public String inspect() {
        return "method " + instance;
    }
    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {

        return "MethodObject{" +
                "instance=" + instance +
                "} " + super.toString();
    }

    public FunctionObject getFunction() {
        return this;
    }
}
