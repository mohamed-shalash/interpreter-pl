package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.token.Token;


@Getter
@Setter
@AllArgsConstructor
public class IntegerLiteral implements Expression {
    private Token token;
    private int value;


    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }


}

