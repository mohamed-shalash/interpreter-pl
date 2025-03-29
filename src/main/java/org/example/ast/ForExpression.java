package org.example.ast;

import org.example.token.Token;

public class ForExpression implements Expression{
    private final Token token;
    private final Identifier loopVariable;
    private final Expression iterable;
    private final BlockStatement body;

    public ForExpression(Token token, Identifier loopVariable,
                         Expression iterable, BlockStatement body) {
        this.token = token;
        this.loopVariable = loopVariable;
        this.iterable = iterable;
        this.body = body;
    }

    // Getters
    public Token getToken() { return token; }
    public Identifier getLoopVariable() { return loopVariable; }
    public Expression getIterable() { return iterable; }
    public BlockStatement getBody() { return body; }

    @Override
    public String toString() {
        return "for " + loopVariable + " in " + iterable + " " + body;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return null;
    }
}