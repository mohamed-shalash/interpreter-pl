package org.example.ast;

import org.example.token.Token;
import java.util.Map;

public class HashLiteral implements Expression {
    private final Token token;
    private final Map<Expression, Expression> pairs;

    public HashLiteral(Token token, Map<Expression, Expression> pairs) {
        this.token = token;
        this.pairs = pairs;
    }

    public Map<Expression, Expression> getPairs() {
        return pairs;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        String[] pairStrings = pairs.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .toArray(String[]::new);
        sb.append(String.join(", ", pairStrings));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void expressionNode() {

    }
}
