package org.example.ast.classes;


import org.example.ast.Expression;
import org.example.ast.Identifier;
import org.example.token.Token;


public class DotExpression implements Expression {
    private Token token;
    private Expression left;
    private Identifier field;

    public DotExpression(Token token, Expression left, Identifier field) {
        this.token = token;
        this.left = left;
        this.field = field;
    }

    // Getters, toString, etc.
    public Expression getLeft() { return left; }
    public Identifier getField() { return field; }
    @Override
    public String toString() { return left.toString() + "." + field.toString(); }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return null;
    }
}
