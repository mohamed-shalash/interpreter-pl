package org.example.ast;


import org.example.token.Token;
import java.util.Objects;

public class PrefixExpression implements Expression {
    private final Token token;
    private final String operator;
    private final Expression right;

    public PrefixExpression(Token token, String operator, Expression right) {
        this.token = Objects.requireNonNull(token);
        this.operator = Objects.requireNonNull(operator);
        this.right = Objects.requireNonNull(right);
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        return "(" + operator + right + ")";
    }

    // Getters
    public Token getToken() { return token; }
    public String getOperator() { return operator; }
    public Expression getRight() { return right; }

    @Override
    public void expressionNode() {

    }
}
