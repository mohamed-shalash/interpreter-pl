package org.example.ast;


import lombok.Getter;
import lombok.Setter;
import org.example.token.Token;

@Getter
@Setter
public class ExpressionStatement implements Statement {
    private Token token;  // First token of the expression
    private Expression expression;

    public ExpressionStatement(Token token, Expression expression) {
        this.token = token;
        this.expression = expression;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    // Getters
    public Token getToken() {
        return token;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String toString() {
        return (expression != null) ? expression.toString() : "";
    }
}
