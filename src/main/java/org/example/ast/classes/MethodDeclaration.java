package org.example.ast.classes;

import org.example.ast.BlockStatement;
import org.example.ast.Identifier;
import org.example.ast.Statement;
import org.example.token.Token;

import java.util.List;

public class MethodDeclaration implements Statement,ClassMember  {
    private final Token token;
    private final Identifier name;
    private final List<Identifier> parameters;
    private final BlockStatement body;

    public MethodDeclaration(Token token, Identifier name,
                             List<Identifier> parameters, BlockStatement body) {
        this.token = token;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    // Getters
    public Token getToken() { return token; }
    public Identifier getName() { return name; }
    public List<Identifier> getParameters() { return parameters; }
    public BlockStatement getBody() { return body; }

    @Override
    public String tokenLiteral() {
        return null;
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String toString() {
         return "MethodDeclaration{" +
                "token=" + token +
                ", name=" + name +
                ", parameters=" + parameters +
                ", body=" + body +
                '}';
    }
}
