package org.example.ast.classes;

import org.example.ast.Expression;
import org.example.token.Token;

public class ThisExpression implements Expression {
    private final Token token;

    public ThisExpression(Token token) {
        this.token = token;
    }

    // Getter
    public Token getToken() { return token; }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return null;
    }

    @Override
    public String toString() {
         //return "this";
        return null;
    }
}