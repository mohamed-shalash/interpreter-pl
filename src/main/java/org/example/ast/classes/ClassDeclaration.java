package org.example.ast.classes;

import org.example.ast.Identifier;
import org.example.ast.Statement;
import org.example.ast.classes.ClassMember;
import org.example.token.Token;
import java.util.List;

public class ClassDeclaration implements Statement {
    private final Token token;
    private final Identifier name;
    private final Identifier superClass;
    private final List<ClassMember> members;


    public ClassDeclaration(Token token, Identifier name,
                            Identifier superClass, List<ClassMember> members) {
        this.token = token;
        this.name = name;
        this.members = members;
        this.superClass = superClass;
    }

    // Getters
    public Token getToken() { return token; }
    public Identifier getName() { return name; }
    public List<ClassMember> getMembers() { return members; }

    public Identifier getSuperClass(){
        return superClass;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ").append(name).append(" {\n");
        for (ClassMember member : members) {
            sb.append("    ").append(member).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void statementNode() {

    }
}