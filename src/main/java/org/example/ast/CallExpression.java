package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.token.Token;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class CallExpression implements Expression {
    private Token token;
    private Expression function;
    private List<Expression> arguments;

    public CallExpression(Token token, Expression function, List<Expression> arguments) {
        this.token = token;
        if (arguments == null) {
            this.arguments = new java.util.ArrayList<>();
        }
        else {
            this.arguments = arguments;
        }
        this.function = function;
    }

    @Override
    public void expressionNode() {

    }


    @Override
    public String tokenLiteral() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(function.toString()).append("(");  // e.g., "add("
        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i).toString());  // Add each argument's string
            if (i < arguments.size() - 1) {
                sb.append(", ");  // Add ", " except after the last argument
            }
        }
        sb.append(")");  // Close parentheses
        return sb.toString();
    }
}
