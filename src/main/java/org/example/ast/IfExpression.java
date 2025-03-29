package org.example.ast;


import org.example.token.Token;

import java.util.StringJoiner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IfExpression implements Expression {
    private Token token;
    private Expression condition;
    private BlockStatement consequence;
    private BlockStatement alternative;

    public IfExpression(Token token, Expression condition,
                        BlockStatement consequence, BlockStatement alternative) {
        this.token = token;
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add("if")
                .add(condition.toString())
                .add(consequence.toString());

        if (alternative != null) {
            sj.add("else");
            sj.add(alternative.toString());
        }

        return sj.toString();
    }

    @Override
    public void expressionNode() {

    }

    // Getters


}
