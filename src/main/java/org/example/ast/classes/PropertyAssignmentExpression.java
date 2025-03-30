package org.example.ast.classes;


import lombok.Getter;
import lombok.Setter;
import org.example.ast.Expression;
import org.example.ast.Identifier;
import org.example.token.Token;

@Getter
@Setter
public class PropertyAssignmentExpression implements Expression {

    private Token token;
    private Expression object;
    private Identifier property;
    private Expression value;

    public PropertyAssignmentExpression(Token token, Expression object,
                                        Identifier property, Expression value) {
        this.token = token;
        this.object = object;
        this.property = property;
        this.value = value;
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
        return object.toString() + "." + property.toString() + " = " + value.toString();
    }
}
