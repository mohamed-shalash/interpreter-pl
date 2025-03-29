package org.example.ast;

import org.example.token.Token;

public class OperatorExpression implements Expression {
    private Expression left;
    private Token operator;
    private Expression right;
    public OperatorExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    public String tokenLiteral() {
        return operator.getLiteral();
    }


    public void expressionNode() {
        // empty
    }
}