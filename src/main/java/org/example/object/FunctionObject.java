package org.example.object;

import org.example.ast.BlockStatement;
import org.example.ast.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionObject implements Object {
    private final List<Identifier> parameters;
    private final BlockStatement body;
    private final Environment env;

    public FunctionObject(List<Identifier> parameters,
                          BlockStatement body,
                          Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    @Override
    public ObjectType type() {
        return ObjectType.FUNCTION_OBJ;
    }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        String params = parameters.stream()
                .map(Identifier::toString)
                .collect(Collectors.joining(", "));

        sb.append("fn(")
                .append(params)
                .append(") {\n")
                .append(body.toString())
                .append("\n}");

        return sb.toString();
    }

    public FunctionObject bind(Environment env) {
        Environment newEnv = new Environment(env);
        return new FunctionObject(this.parameters, this.body, newEnv);
    }

    // Getters
    public List<Identifier> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    public Environment getEnv() {
        return env;
    }

    @Override
    public String toString() {

        return "FunctionObject{" +
                "parameters=" + parameters +
                ", body=" + body +
                ", env=" + env +
                '}';
    }
}
