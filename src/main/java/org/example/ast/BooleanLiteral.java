package org.example.ast;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.token.Token;

@Getter
@Setter
@AllArgsConstructor
public class BooleanLiteral implements Expression {
    private  Token token;
    private  boolean value;


    public boolean getValue() {
        return value;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        return token.getLiteral(); // Returns "true" or "false"
    }

    @Override
    public void expressionNode() {

    }
}