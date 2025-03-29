package org.example.ast;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Program implements Expression{
    private List<Statement> statements;
    public Program(List<Statement> statements) {
        this.statements = statements;
    }
    public String tokenLiteral() {
        if (statements != null && !statements.isEmpty()) {
            return statements.get(0).tokenLiteral();
        } else {
            return "";
        }
    }

    @Override
    public void expressionNode() {
        // empty
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Statement statement : statements) {
            sb.append(statement.toString());
        }
        return sb.toString();
    }
}
