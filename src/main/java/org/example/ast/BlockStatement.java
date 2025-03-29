package org.example.ast;

import org.example.token.Token;

import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockStatement implements Statement {
    private  Token token;
    private  List<Statement> statements;

    public BlockStatement(Token token, List<Statement> statements) {
        this.token = token;
        if (statements == null) {
            this.statements = new java.util.ArrayList<>();
        }else {
            this.statements = statements;
        }
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Statement stmt : statements) {
            sb.append(stmt.toString());
        }
        return sb.toString();
    }

    // Getters
    public Token getToken() {
        return token;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public void statementNode() {

    }

    public void addStatement(Statement stmt) {
        statements.add(stmt);
    }
}
