package org.example.token;

import java.util.HashMap;
import java.util.Map;

public class Token {
    public enum TokenType {
        ILLEGAL("ILLEGAL"),
        EOF("EOF"),
        // Identifiers + literals
        IDENT("IDENT"), // add, foobar, x, y, ...
        INT("INT"),     // 1343456
        // Operators
        ASSIGN("="),
        PLUS("+"),
        MINUS("-"),
        BANG("!"),
        SLASH("/"),
        ASTERISK("*"),
        LT("<"),
        GT(">"),
        // Delimiters
        COMMA(","),
        SEMICOLON(";"),
        LPAREN("("),
        RPAREN(")"),
        LBRACE("{"),
        RBRACE("}"),
        LBRACKET("["),
        RBRACKET("]"),
        COLON(":"),
        STRING("STRING"),//STRING
        // Keywords
        FUNCTION("FUNCTION"),
        LET("LET"),
        IF("if"),
        WHILE("while"),
        FOR("for"),
        ELSE("else"),
        RETURN("return"),
        TRUE("true"),
        FALSE("false"),

        BREAK("BREAK"),
        CONTINUE("CONTINUE"),
        LE("LE"),
        GE("GE"),
        IN("IN"),

        //equlity
        EQ("=="),
        NOT_EQ("!="),
        CLASS("CLASS"),
        THIS("THIS"),
        NEW("NEW"),
        SUPER("SUPER"),
        DOT("."),
        PRIVATE("PRIVATE")
        ;

        private final String literal;

        TokenType(String literal) {
            this.literal = literal;
        }

        public String getLiteral() {
            return literal;
        }
    }

    private final TokenType type;
    private final String literal;

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("fn", TokenType.FUNCTION);
        keywords.put("let", TokenType.LET);
        keywords.put("if", TokenType.IF);
        keywords.put("while", TokenType.WHILE);
        keywords.put("for", TokenType.FOR);
        keywords.put("in", TokenType.IN);
        keywords.put("else", TokenType.ELSE);
        keywords.put("return", TokenType.RETURN);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("break", TokenType.BREAK);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("class", Token.TokenType.CLASS);
        keywords.put("this", Token.TokenType.THIS);
        keywords.put("new", Token.TokenType.NEW);
        keywords.put("super", Token.TokenType.SUPER);
        keywords.put("private", Token.TokenType.PRIVATE);
    }

    public Token(TokenType type, String literal) {
        this.type = type;
        this.literal = literal;
    }


    public static TokenType lookupIdent(String ident) {
        return keywords.getOrDefault(ident, TokenType.IDENT);
    }
    public TokenType getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return String.format("Token(Type: %s, Literal: %s)", type, literal);
    }
}

