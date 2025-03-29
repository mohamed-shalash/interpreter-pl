package org.example.ast;

import org.example.token.Token;

public class AssignmentExpression implements Expression {
    private Token token; // The '=' token
    private Identifier name; // The variable being assigned to (left-hand side)
    private Expression value; // The value being assigned (right-hand side)

    public AssignmentExpression(Token token, Identifier name, Expression value) {
        this.token = token;
        this.name = name;
        this.value = value;
    }

    public Identifier getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }


    public Token getToken() {
        return token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        return name.toString() + " = " + value.toString();
    }

    @Override
    public void expressionNode() {

    }
}