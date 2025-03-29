package org.example.ast;

import org.example.token.Token;

public class BreakStatement implements Statement {
    private Token token;

    public BreakStatement(Token token) {
        this.token = token;
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
        return "break";
    }

    @Override
    public void statementNode() {

    }
}