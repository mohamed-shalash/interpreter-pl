package org.example.ast;

import org.example.token.Token;

public class ContinueStatement implements Statement {
    private Token token;

    public ContinueStatement(Token token) {
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
        return "continue";
    }

    @Override
    public void statementNode() {

    }
}