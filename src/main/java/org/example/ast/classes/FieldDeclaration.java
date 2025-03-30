package org.example.ast.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ast.Expression;
import org.example.ast.Identifier;
import org.example.token.Token;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FieldDeclaration implements ClassMember{
    private Token token;
    private Identifier name;
    private Expression initializer;
    private boolean isPrivate;

    public FieldDeclaration(Token token, Identifier name, Expression initializer) {
        this.token = token;
        this.name = name;
        this.initializer = initializer;
        this.isPrivate = false;
    }

    @Override
    public String toString() {

        return "FieldDeclaration{" +
                "token=" + token +
                ", name=" + name +
                ", initializer=" + initializer +
                '}';
    }
}
