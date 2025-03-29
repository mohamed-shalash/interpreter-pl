package org.example.ast;

import org.example.token.Token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringLiteral implements Expression {
    private final Token token;
    private final String value;

    public StringLiteral(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    @Override
    public void expressionNode() {
        // Marker interface implementation
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        return token.getLiteral();
    }

}
