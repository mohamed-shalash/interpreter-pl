package org.example.ast;

import lombok.Getter;
import lombok.Setter;
import org.example.token.Token;

@Getter
@Setter
public class LetStatement implements Statement {
    private Token token; // the LET token
    private Identifier name;
    private Expression value;
    public LetStatement(Token token, Identifier name, Expression value) {
        this.token = token;
        this.name = name;
        this.value = value;
    }
    public String tokenLiteral() {
        return token.getLiteral();
    }
    public void statementNode() {
        // empty
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tokenLiteral()).append(" ");
        sb.append(name.toString());
        sb.append(" = ");
        if (value != null) {
            sb.append(value.toString());
        }
        sb.append(";");
        return sb.toString();
    }
}