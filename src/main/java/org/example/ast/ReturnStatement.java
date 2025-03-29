package org.example.ast;

import org.example.token.Token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnStatement implements Statement {
    private Token token;  // The 'return' token
    private Expression returnValue;

    public ReturnStatement(Token token, Expression returnValue) {
        this.token = token;
        this.returnValue = returnValue;
    }

    // Interface implementation
    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    // Getter methods
    public Token getToken() {
        return token;
    }

    public Expression getReturnValue() {
        return returnValue;
    }

    @Override
    public void statementNode() {

    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tokenLiteral()).append(" ");
        if (returnValue != null) {
            sb.append(returnValue.toString());
        }
        sb.append(";");
        return sb.toString();
    }


}
