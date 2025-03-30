package org.example.ast.classes;

import org.example.ast.Expression;
import org.example.ast.Identifier;
import org.example.token.Token;

import java.util.List;

public class NewInstanceExpression implements Expression {
    private final Identifier className;
    private final List<Expression> arguments;

    public NewInstanceExpression(Token token, Identifier className,
                                 List<Expression> arguments) {
        this.className = className;
        this.arguments = arguments;
    }

    // Getters
    public Identifier getClassName() { return className; }
    public List<Expression> getArguments() { return arguments; }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return null;
    }
}
