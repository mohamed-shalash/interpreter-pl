// IndexExpression.java
package org.example.ast;

import org.example.token.Token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexExpression implements Expression {
    private  Token token; // The [ token
    private Expression left;
    private Expression index;

    public IndexExpression(Token token, Expression left, Expression index) {
        this.token = token;
        this.left = left;
        this.index = index;
    }



    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "[" + index.toString() + "])";
    }

    @Override
    public void expressionNode() {

    }
}