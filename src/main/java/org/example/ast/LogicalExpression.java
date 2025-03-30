package org.example.ast;

import org.example.token.Token;

import java.util.Objects;

import org.example.token.Token;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogicalExpression implements Expression{
    private Token token;
    private Expression left;
    private String operator;
    private  Expression right;

    public LogicalExpression(Token token, Expression left, String operator, Expression right) {
        this.token = Objects.requireNonNull(token);
        this.left = Objects.requireNonNull(left);
        this.operator = Objects.requireNonNull(operator);
        this.right = Objects.requireNonNull(right);
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }



    // Getters
    public Token getToken() { return token; }
    public Expression getLeft() { return left; }
    public String getOperator() { return operator; }
    public Expression getRight() { return right; }

    @Override
    public void expressionNode() {

    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
    }
}