// ArrayLiteral.java
package org.example.ast;

import org.example.token.Token;

import java.util.List;

public class ArrayLiteral implements Expression {
    private final Token token; // The '[' token
    private final List<Expression> elements;

    public ArrayLiteral(Token token, List<Expression> elements) {
        this.token = token;
        this.elements = elements;
    }

    public List<Expression> getElements() {
        return elements;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i).toString());
            if (i < elements.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public void expressionNode() {

    }
}
