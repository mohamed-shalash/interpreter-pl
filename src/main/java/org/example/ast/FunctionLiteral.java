package org.example.ast;


import lombok.Getter;
import lombok.Setter;
import org.example.token.Token;

import java.util.List;
import java.util.StringJoiner;

@Getter
@Setter
public class FunctionLiteral implements Expression {
    private Token token;
    private List<Identifier> parameters;
    private BlockStatement body;

    public FunctionLiteral(Token token, List<Identifier> parameters, BlockStatement body) {
        this.token = token;
        if (parameters == null) {
            this.parameters = new java.util.ArrayList<>();
        }
        else {
            this.parameters = parameters;
        }
        this.body = body;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        for (Identifier param : parameters) {
            sj.add(param.toString());
        }

        return tokenLiteral()
                + "("
                + sj.toString()
                + ") "
                + body.toString();
    }

    @Override
    public void expressionNode() {

    }
}
